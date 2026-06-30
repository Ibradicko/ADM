import { existsSync, mkdirSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { spawn, spawnSync } from 'node:child_process';
import { fileURLToPath } from 'node:url';

const repoRoot = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const dockerConfig = resolve(repoRoot, '.docker-codex');
const dockerConfigFile = resolve(dockerConfig, 'config.json');
const useIsolatedDockerConfig = process.env.ADM_DOCKER_ISOLATED_CONFIG === '1';
const dockerEnv = {
  ...process.env,
  ...(useIsolatedDockerConfig ? { DOCKER_CONFIG: dockerConfig } : {}),
};
const dockerArgs = process.argv.slice(2);
const dockerDesktopPath = 'C:\\Program Files\\Docker\\Docker\\Docker Desktop.exe';

if (!existsSync(dockerConfig)) {
  mkdirSync(dockerConfig, { recursive: true });
}

if (!existsSync(dockerConfigFile)) {
  writeFileSync(dockerConfigFile, '{}\n');
}

function dockerInfo() {
  return spawnSync('docker', ['info'], {
    cwd: repoRoot,
    env: dockerEnv,
    encoding: 'utf8',
  });
}

function sleep(ms) {
  return new Promise(resolveSleep => setTimeout(resolveSleep, ms));
}

function startDockerDesktop() {
  if (process.platform !== 'win32' || !existsSync(dockerDesktopPath)) {
    return;
  }

  spawnSync('powershell.exe', ['-NoProfile', '-Command', `Start-Process -FilePath '${dockerDesktopPath}' -WindowStyle Hidden`], {
    cwd: repoRoot,
    encoding: 'utf8',
  });
}

async function ensureDockerDaemon() {
  if (dockerArgs[0] === 'help' || dockerArgs.includes('--help')) {
    return;
  }

  let info = dockerInfo();
  if (info.status === 0) {
    return;
  }

  startDockerDesktop();

  const timeoutAt = Date.now() + 90_000;
  while (Date.now() < timeoutAt) {
    await sleep(3_000);
    info = dockerInfo();
    if (info.status === 0) {
      return;
    }
  }

  const output = `${info.stdout ?? ''}${info.stderr ?? ''}`.trim();
  console.error(`
Docker n'est pas pret.

Le client Docker est installe, mais le daemon Docker Desktop ne repond pas encore.
Ouvre Docker Desktop, attends que le moteur Linux soit "Running", puis relance:

  cmd /c npm.cmd run docker:db:up

Diagnostic Docker:
${output || 'docker info a echoue sans sortie detaillee.'}
`);
  process.exit(info.status ?? 1);
}

await ensureDockerDaemon();

const docker = spawn('docker', dockerArgs, {
  cwd: repoRoot,
  env: dockerEnv,
  stdio: 'inherit',
});

docker.on('exit', (code, signal) => {
  if (signal) {
    process.kill(process.pid, signal);
    return;
  }

  process.exit(code ?? 1);
});
