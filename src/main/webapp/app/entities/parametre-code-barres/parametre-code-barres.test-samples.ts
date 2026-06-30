import { IParametreCodeBarres, NewParametreCodeBarres } from './parametre-code-barres.model';

export const sampleWithRequiredData: IParametreCodeBarres = {
  id: 20494,
  formatParDefaut: 'QR_CODE',
  actif: true,
};

export const sampleWithPartialData: IParametreCodeBarres = {
  id: 10499,
  formatParDefaut: 'EAN8',
  actif: true,
};

export const sampleWithFullData: IParametreCodeBarres = {
  id: 16114,
  formatParDefaut: 'INTERNE',
  prefixe: 'popularity hence tou',
  longueur: 30,
  actif: false,
};

export const sampleWithNewData: NewParametreCodeBarres = {
  formatParDefaut: 'EAN13',
  actif: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
