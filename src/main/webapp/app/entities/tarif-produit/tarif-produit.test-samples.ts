import dayjs from 'dayjs/esm';

import { ITarifProduit, NewTarifProduit } from './tarif-produit.model';

export const sampleWithRequiredData: ITarifProduit = {
  id: 25335,
  montant: 10064.37,
  typePrix: 'PROMOTION',
  dateDebut: dayjs('2026-05-05'),
  actif: true,
};

export const sampleWithPartialData: ITarifProduit = {
  id: 10874,
  montant: 3727.47,
  typePrix: 'STANDARD',
  dateDebut: dayjs('2026-05-04'),
  dateFin: dayjs('2026-05-05'),
  actif: true,
};

export const sampleWithFullData: ITarifProduit = {
  id: 6392,
  montant: 21485.79,
  typePrix: 'CONTRACTUEL',
  dateDebut: dayjs('2026-05-05'),
  dateFin: dayjs('2026-05-05'),
  actif: true,
};

export const sampleWithNewData: NewTarifProduit = {
  montant: 24454.58,
  typePrix: 'CONTRACTUEL',
  dateDebut: dayjs('2026-05-05'),
  actif: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
