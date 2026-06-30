import dayjs from 'dayjs/esm';

import { IMouvementStock, NewMouvementStock } from './mouvement-stock.model';

export const sampleWithRequiredData: IMouvementStock = {
  id: 16750,
  reference: 'milestone drat sprinkles',
  typeMouvement: 'AJUSTEMENT',
  statut: 'BROUILLON',
  dateMouvement: dayjs('2026-05-05T06:38'),
};

export const sampleWithPartialData: IMouvementStock = {
  id: 10687,
  reference: 'psst coagulate',
  typeMouvement: 'PERTE',
  statut: 'VALIDE',
  dateMouvement: dayjs('2026-05-05T08:13'),
  motif: 'in-joke',
};

export const sampleWithFullData: IMouvementStock = {
  id: 2077,
  reference: 'besides yum meh',
  typeMouvement: 'SORTIE',
  statut: 'BROUILLON',
  dateMouvement: dayjs('2026-05-05T05:12'),
  motif: 'stylish obsess toward',
};

export const sampleWithNewData: NewMouvementStock = {
  reference: 'rectangular enraged',
  typeMouvement: 'ENTREE',
  statut: 'ANNULE',
  dateMouvement: dayjs('2026-05-04T15:38'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
