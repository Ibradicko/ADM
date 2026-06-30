import dayjs from 'dayjs/esm';

import { IHistoriqueCodeBarres, NewHistoriqueCodeBarres } from './historique-code-barres.model';

export const sampleWithRequiredData: IHistoriqueCodeBarres = {
  id: 2773,
  nouveauCode: 'yippee willfully indeed',
  dateChangement: dayjs('2026-05-05T01:13'),
};

export const sampleWithPartialData: IHistoriqueCodeBarres = {
  id: 25286,
  nouveauCode: 'kit',
  motif: 'ew whenever',
  dateChangement: dayjs('2026-05-05T01:52'),
};

export const sampleWithFullData: IHistoriqueCodeBarres = {
  id: 20189,
  ancienCode: 'needily pro tightly',
  nouveauCode: 'abaft',
  motif: 'brr during defensive',
  dateChangement: dayjs('2026-05-04T17:48'),
};

export const sampleWithNewData: NewHistoriqueCodeBarres = {
  nouveauCode: 'woot developing brr',
  dateChangement: dayjs('2026-05-04T12:05'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
