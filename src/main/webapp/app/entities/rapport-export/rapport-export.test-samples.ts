import dayjs from 'dayjs/esm';

import { IRapportExport, NewRapportExport } from './rapport-export.model';

export const sampleWithRequiredData: IRapportExport = {
  id: 2141,
  reference: 'zebra yet while',
  typeRapport: 'almost phooey while',
  format: 'EXCEL',
  dateGeneration: dayjs('2026-05-04T23:48'),
};

export const sampleWithPartialData: IRapportExport = {
  id: 6159,
  reference: 'ick lowball',
  typeRapport: 'uh-huh broken regularly',
  format: 'PDF',
  periodeFin: dayjs('2026-05-05'),
  dateGeneration: dayjs('2026-05-05T01:49'),
};

export const sampleWithFullData: IRapportExport = {
  id: 24808,
  reference: 'cellar',
  typeRapport: 'sequester for ah',
  format: 'PDF',
  periodeDebut: dayjs('2026-05-04'),
  periodeFin: dayjs('2026-05-04'),
  cheminFichier: 'forenenst',
  dateGeneration: dayjs('2026-05-05T06:13'),
};

export const sampleWithNewData: NewRapportExport = {
  reference: 'commandeer promise even',
  typeRapport: 'whoever aw',
  format: 'PDF',
  dateGeneration: dayjs('2026-05-04T13:01'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
