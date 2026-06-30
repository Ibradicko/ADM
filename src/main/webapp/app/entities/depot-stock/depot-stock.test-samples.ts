import { IDepotStock, NewDepotStock } from './depot-stock.model';

export const sampleWithRequiredData: IDepotStock = {
  id: 25175,
  code: 'corrupt',
  libelle: 'forceful',
  actif: true,
};

export const sampleWithPartialData: IDepotStock = {
  id: 27919,
  code: 'qua',
  libelle: 'joshingly',
  emplacement: 'until',
  actif: true,
};

export const sampleWithFullData: IDepotStock = {
  id: 14161,
  code: 'flimsy',
  libelle: 'ramp',
  emplacement: 'recklessly on',
  actif: false,
};

export const sampleWithNewData: NewDepotStock = {
  code: 'notwithstanding worthless othe',
  libelle: 'retract giving disk',
  actif: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
