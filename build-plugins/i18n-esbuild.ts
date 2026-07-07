import { readFile } from 'node:fs/promises';
import { join } from 'node:path';

import deepmerge from 'deepmerge';
import type { Plugin } from 'esbuild';
import { hashElement } from 'folder-hash';
import { glob } from 'tinyglobby';

const i18nSourceDir = join(__dirname, '../src/main/webapp/i18n/');

export const prepareLanguage = async (language: string) => {
  const files = await glob('*.json', { cwd: join(i18nSourceDir, language) });
  let merged = {};
  const sourceFiles: string[] = [];
  for (const file of files) {
    const sourceFile = join(i18nSourceDir, language, file);
    sourceFiles.push(sourceFile);
    merged = deepmerge(merged, JSON.parse(await readFile(sourceFile, 'utf-8')));
  }
  return { merged, sourceFiles };
};

export default {
  name: 'copy:i18n',
  async setup(build) {
    const languagesHash = await hashElement(i18nSourceDir, {
      algo: 'md5',
      encoding: 'hex',
      files: { include: ['*.json'] },
    });

    build.initialOptions.define ??= {};
    Object.assign(build.initialOptions.define, {
      I18N_HASH: JSON.stringify(languagesHash.hash),
    });

    build.onResolve({ filter: /^i18n\// }, ({ path }) => {
      return { namespace: 'i18n-json', path };
    });

    build.onLoad({ filter: /^i18n\//, namespace: 'i18n-json' }, async ({ path }) => {
      const match = /^i18n\/(?<lang>.*)\.json/.exec(path);
      const { merged, sourceFiles } = await prepareLanguage(match!.groups!.lang);
      return {
        contents: JSON.stringify(merged),
        loader: 'json',
        // Without this, esbuild's watch mode has no way to know this virtual module
        // depends on the individual i18n/<lang>/*.json files (they're read manually via
        // readFile, not resolved as imports) - editing a translation file silently did not
        // trigger a rebuild/HMR until the dev server was restarted.
        watchFiles: sourceFiles,
      };
    });
  },
} satisfies Plugin;
