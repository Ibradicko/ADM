import { ILigneReceptionProduit, NewLigneReceptionProduit } from './ligne-reception-produit.model';

export const sampleWithRequiredData: ILigneReceptionProduit = {
  id: 31793,
  quantiteRecue: 8037.86,
};

export const sampleWithPartialData: ILigneReceptionProduit = {
  id: 15418,
  quantiteRecue: 3610.39,
  ecart: 21512.73,
  codeBarresScanne: 'cantaloupe joshingly',
};

export const sampleWithFullData: ILigneReceptionProduit = {
  id: 10839,
  quantiteAttendue: 31864.81,
  quantiteRecue: 20777.12,
  ecart: 12223.37,
  codeBarresScanne: 'ugh',
};

export const sampleWithNewData: NewLigneReceptionProduit = {
  quantiteRecue: 18606.53,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
