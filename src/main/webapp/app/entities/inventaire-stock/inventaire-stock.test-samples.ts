import dayjs from 'dayjs/esm';

import { IInventaireStock, NewInventaireStock } from './inventaire-stock.model';

export const sampleWithRequiredData: IInventaireStock = {
  id: 26933,
  reference: 'and',
  typeInventaire: 'COMPLET',
  statut: 'CLOTURE',
  dateDebut: dayjs('2026-05-04T17:19'),
};

export const sampleWithPartialData: IInventaireStock = {
  id: 23684,
  reference: 'supposing',
  typeInventaire: 'TOURNANT',
  statut: 'CLOTURE',
  dateDebut: dayjs('2026-05-05T08:22'),
  dateFin: dayjs('2026-05-04T13:46'),
};

export const sampleWithFullData: IInventaireStock = {
  id: 7974,
  reference: 'lean',
  typeInventaire: 'CONTROLE_PONCTUEL',
  statut: 'PLANIFIE',
  dateDebut: dayjs('2026-05-04T21:12'),
  dateFin: dayjs('2026-05-04T22:57'),
};

export const sampleWithNewData: NewInventaireStock = {
  reference: 'but pity gosh',
  typeInventaire: 'COMPLET',
  statut: 'CLOTURE',
  dateDebut: dayjs('2026-05-04T12:58'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
