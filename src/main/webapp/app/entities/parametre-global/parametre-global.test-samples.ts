import { IParametreGlobal, NewParametreGlobal } from './parametre-global.model';

export const sampleWithRequiredData: IParametreGlobal = {
  id: 13084,
  code: 'er midwife cautiously',
  valeur: 'programme',
  actif: false,
};

export const sampleWithPartialData: IParametreGlobal = {
  id: 20461,
  code: 'popularity less',
  valeur: 'gosh',
  description: '../fake-data/blob/hipster.txt',
  actif: true,
};

export const sampleWithFullData: IParametreGlobal = {
  id: 14547,
  code: 'who very',
  valeur: 'inasmuch clumsy yum',
  description: '../fake-data/blob/hipster.txt',
  actif: false,
};

export const sampleWithNewData: NewParametreGlobal = {
  code: 'what',
  valeur: 'after developing',
  actif: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
