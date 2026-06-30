import dayjs from 'dayjs/esm';

import { IReceptionProduit, NewReceptionProduit } from './reception-produit.model';

export const sampleWithRequiredData: IReceptionProduit = {
  id: 17516,
  reference: 'wobbly regularly',
  dateReception: dayjs('2026-05-04T16:38'),
};

export const sampleWithPartialData: IReceptionProduit = {
  id: 7048,
  reference: 'gleefully',
  dateReception: dayjs('2026-05-04T15:45'),
  fournisseur: 'er',
  commentaire: 'thoroughly yieldingly determined',
};

export const sampleWithFullData: IReceptionProduit = {
  id: 9366,
  reference: 'scarper illusion after',
  dateReception: dayjs('2026-05-04T12:54'),
  fournisseur: 'since',
  commentaire: 'reword anxiously',
};

export const sampleWithNewData: NewReceptionProduit = {
  reference: 'geez around about',
  dateReception: dayjs('2026-05-05T04:37'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
