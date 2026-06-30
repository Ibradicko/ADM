import { IPermissionMetier, NewPermissionMetier } from './permission-metier.model';

export const sampleWithRequiredData: IPermissionMetier = {
  id: 6189,
  code: 'while solution',
  libelle: 'through ouch',
  module: 'unaccountably except',
};

export const sampleWithPartialData: IPermissionMetier = {
  id: 32119,
  code: 'curly',
  libelle: 'incidentally deplore',
  module: 'concentration',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IPermissionMetier = {
  id: 29054,
  code: 'vista failing finally',
  libelle: 'jaggedly',
  module: 'where',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewPermissionMetier = {
  code: 'when dark postbox',
  libelle: 'celsius',
  module: 'valuable antelope',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
