import dayjs from 'dayjs/esm';

import { IEtiquetteProduit, NewEtiquetteProduit } from './etiquette-produit.model';

export const sampleWithRequiredData: IEtiquetteProduit = {
  id: 7188,
  quantite: 25461,
  imprimee: false,
};

export const sampleWithPartialData: IEtiquetteProduit = {
  id: 14507,
  quantite: 4465,
  imprimee: false,
  dateImpression: dayjs('2026-05-04T10:49'),
};

export const sampleWithFullData: IEtiquetteProduit = {
  id: 3498,
  quantite: 24011,
  imprimee: true,
  dateImpression: dayjs('2026-05-04T18:44'),
};

export const sampleWithNewData: NewEtiquetteProduit = {
  quantite: 986,
  imprimee: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
