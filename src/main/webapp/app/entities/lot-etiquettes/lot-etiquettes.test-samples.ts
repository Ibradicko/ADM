import dayjs from 'dayjs/esm';

import { ILotEtiquettes, NewLotEtiquettes } from './lot-etiquettes.model';

export const sampleWithRequiredData: ILotEtiquettes = {
  id: 20949,
  reference: 'ew around',
  dateGeneration: dayjs('2026-05-04T18:50'),
  nombreEtiquettes: 25435,
};

export const sampleWithPartialData: ILotEtiquettes = {
  id: 16392,
  reference: 'aha',
  dateGeneration: dayjs('2026-05-04T15:34'),
  formatImpression: 'freight',
  nombreEtiquettes: 22148,
};

export const sampleWithFullData: ILotEtiquettes = {
  id: 26978,
  reference: 'spirited excluding',
  dateGeneration: dayjs('2026-05-04T22:54'),
  formatImpression: 'obstruct highly',
  nombreEtiquettes: 26684,
};

export const sampleWithNewData: NewLotEtiquettes = {
  reference: 'yuck elevation incidentally',
  dateGeneration: dayjs('2026-05-04T19:10'),
  nombreEtiquettes: 20870,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
