import { ILigneMouvementStock, NewLigneMouvementStock } from './ligne-mouvement-stock.model';

export const sampleWithRequiredData: ILigneMouvementStock = {
  id: 7303,
  quantite: 32671.8,
};

export const sampleWithPartialData: ILigneMouvementStock = {
  id: 24875,
  quantite: 9830.4,
  stockApres: 11840.61,
  commentaire: 'amidst lone',
};

export const sampleWithFullData: ILigneMouvementStock = {
  id: 3538,
  quantite: 27809.52,
  stockAvant: 9292.99,
  stockApres: 12974.57,
  commentaire: 'unless fluffy',
};

export const sampleWithNewData: NewLigneMouvementStock = {
  quantite: 4722.68,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
