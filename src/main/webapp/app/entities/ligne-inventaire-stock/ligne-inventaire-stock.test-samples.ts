import { ILigneInventaireStock, NewLigneInventaireStock } from './ligne-inventaire-stock.model';

export const sampleWithRequiredData: ILigneInventaireStock = {
  id: 26520,
  quantiteTheorique: 27583.28,
  quantiteComptee: 24210.29,
};

export const sampleWithPartialData: ILigneInventaireStock = {
  id: 20645,
  quantiteTheorique: 9086.97,
  quantiteComptee: 23798.11,
  ecart: 1437.62,
  commentaire: 'disinherit',
};

export const sampleWithFullData: ILigneInventaireStock = {
  id: 24444,
  quantiteTheorique: 24979.08,
  quantiteComptee: 3560.56,
  ecart: 22548.87,
  commentaire: 'next round',
};

export const sampleWithNewData: NewLigneInventaireStock = {
  quantiteTheorique: 24408.25,
  quantiteComptee: 1852.86,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
