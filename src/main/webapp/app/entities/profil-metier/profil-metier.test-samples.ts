import { IProfilMetier, NewProfilMetier } from './profil-metier.model';

export const sampleWithRequiredData: IProfilMetier = {
  id: 9676,
  code: 'solution',
  libelle: 'millet supposing',
  statut: 'ACTIF',
};

export const sampleWithPartialData: IProfilMetier = {
  id: 9922,
  code: 'acidly',
  libelle: 'upon camouflage tenant',
  description: '../fake-data/blob/hipster.txt',
  statut: 'INACTIF',
};

export const sampleWithFullData: IProfilMetier = {
  id: 22520,
  code: 'plump outlandish',
  libelle: 'wherever',
  description: '../fake-data/blob/hipster.txt',
  statut: 'SUSPENDU',
};

export const sampleWithNewData: NewProfilMetier = {
  code: 'ha hollow',
  libelle: 'negligible',
  statut: 'ACTIF',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
