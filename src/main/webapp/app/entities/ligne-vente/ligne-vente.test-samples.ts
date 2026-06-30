import { ILigneVente, NewLigneVente } from './ligne-vente.model';

export const sampleWithRequiredData: ILigneVente = {
  id: 16641,
  quantite: 13917.06,
  prixUnitaire: 6454.66,
  montantLigne: 666.27,
};

export const sampleWithPartialData: ILigneVente = {
  id: 7510,
  quantite: 19324.86,
  prixUnitaire: 8229.96,
  montantLigne: 16178.96,
  codeBarresScanne: 'rewarding reorganisation aside',
};

export const sampleWithFullData: ILigneVente = {
  id: 32630,
  quantite: 3094.74,
  prixUnitaire: 14585.02,
  remise: 19342.55,
  montantLigne: 26582.3,
  codeBarresScanne: 'obligation',
};

export const sampleWithNewData: NewLigneVente = {
  quantite: 25272.64,
  prixUnitaire: 1446.8,
  montantLigne: 13936.88,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
