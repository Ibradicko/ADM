import { ILigneCalculRedevance, NewLigneCalculRedevance } from './ligne-calcul-redevance.model';

export const sampleWithRequiredData: ILigneCalculRedevance = {
  id: 20067,
  baseCalcul: 10765.14,
  tauxApplique: 7.98,
  montantRedevance: 18300.91,
};

export const sampleWithPartialData: ILigneCalculRedevance = {
  id: 29109,
  baseCalcul: 10684.21,
  tauxApplique: 30.8,
  montantRedevance: 806.42,
};

export const sampleWithFullData: ILigneCalculRedevance = {
  id: 28924,
  baseCalcul: 14836.22,
  tauxApplique: 35.93,
  montantRedevance: 5168.8,
};

export const sampleWithNewData: NewLigneCalculRedevance = {
  baseCalcul: 2919.37,
  tauxApplique: 45.74,
  montantRedevance: 23062.74,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
