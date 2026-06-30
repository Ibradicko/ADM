import dayjs from 'dayjs/esm';

import { IAffectationUtilisateur, NewAffectationUtilisateur } from './affectation-utilisateur.model';

export const sampleWithRequiredData: IAffectationUtilisateur = {
  id: 4789,
  dateDebut: dayjs('2026-05-04'),
  actif: false,
};

export const sampleWithPartialData: IAffectationUtilisateur = {
  id: 14741,
  dateDebut: dayjs('2026-05-04'),
  actif: false,
};

export const sampleWithFullData: IAffectationUtilisateur = {
  id: 23219,
  dateDebut: dayjs('2026-05-04'),
  dateFin: dayjs('2026-05-05'),
  actif: true,
};

export const sampleWithNewData: NewAffectationUtilisateur = {
  dateDebut: dayjs('2026-05-04'),
  actif: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
