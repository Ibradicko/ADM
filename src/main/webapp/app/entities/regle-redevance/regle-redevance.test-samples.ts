import dayjs from 'dayjs/esm';

import { IRegleRedevance, NewRegleRedevance } from './regle-redevance.model';

export const sampleWithRequiredData: IRegleRedevance = {
  id: 15925,
  code: 'uh-huh',
  typeRegle: 'BOUTIQUE',
  taux: 88.21,
  dateDebut: dayjs('2026-05-04'),
  actif: false,
};

export const sampleWithPartialData: IRegleRedevance = {
  id: 9476,
  code: 'following',
  typeRegle: 'BOUTIQUE',
  taux: 16.46,
  dateDebut: dayjs('2026-05-04'),
  dateFin: dayjs('2026-05-04'),
  priorite: 19087,
  actif: true,
};

export const sampleWithFullData: IRegleRedevance = {
  id: 395,
  code: 'dependable',
  typeRegle: 'GROUPE_ARTICLE',
  taux: 17.98,
  dateDebut: dayjs('2026-05-05'),
  dateFin: dayjs('2026-05-05'),
  priorite: 28996,
  actif: true,
};

export const sampleWithNewData: NewRegleRedevance = {
  code: 'fair round',
  typeRegle: 'LOCATAIRE',
  taux: 50.06,
  dateDebut: dayjs('2026-05-05'),
  actif: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
