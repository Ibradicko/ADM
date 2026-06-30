import { ILigneTransfertStock, NewLigneTransfertStock } from './ligne-transfert-stock.model';

export const sampleWithRequiredData: ILigneTransfertStock = {
  id: 15948,
  quantite: 19187.77,
};

export const sampleWithPartialData: ILigneTransfertStock = {
  id: 23859,
  quantite: 21769.1,
};

export const sampleWithFullData: ILigneTransfertStock = {
  id: 14859,
  quantite: 32204.65,
  commentaire: 'coliseum boohoo',
};

export const sampleWithNewData: NewLigneTransfertStock = {
  quantite: 29021.95,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
