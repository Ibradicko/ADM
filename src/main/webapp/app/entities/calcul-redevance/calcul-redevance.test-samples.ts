import dayjs from 'dayjs/esm';

import { ICalculRedevance, NewCalculRedevance } from './calcul-redevance.model';

export const sampleWithRequiredData: ICalculRedevance = {
  id: 22848,
  reference: 'guacamole',
  periodeDebut: dayjs('2026-05-04'),
  periodeFin: dayjs('2026-05-05'),
  chiffreAffaires: 4930.38,
  montantRedevance: 4178.39,
  statut: 'PAYEE',
  dateCalcul: dayjs('2026-05-05T00:34'),
};

export const sampleWithPartialData: ICalculRedevance = {
  id: 14217,
  reference: 'worse',
  periodeDebut: dayjs('2026-05-04'),
  periodeFin: dayjs('2026-05-05'),
  chiffreAffaires: 10521.31,
  montantRedevance: 1685.9,
  statut: 'ANNULEE',
  dateCalcul: dayjs('2026-05-04T22:24'),
};

export const sampleWithFullData: ICalculRedevance = {
  id: 13042,
  reference: 'in',
  periodeDebut: dayjs('2026-05-05'),
  periodeFin: dayjs('2026-05-05'),
  chiffreAffaires: 30631.19,
  montantRedevance: 34.48,
  statut: 'REGULARISEE',
  dateCalcul: dayjs('2026-05-04T20:16'),
};

export const sampleWithNewData: NewCalculRedevance = {
  reference: 'upbeat expensive',
  periodeDebut: dayjs('2026-05-05'),
  periodeFin: dayjs('2026-05-04'),
  chiffreAffaires: 25962.71,
  montantRedevance: 8448.32,
  statut: 'ANNULEE',
  dateCalcul: dayjs('2026-05-04T19:13'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
