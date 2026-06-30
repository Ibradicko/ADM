import dayjs from 'dayjs/esm';

import { IPaiementVente, NewPaiementVente } from './paiement-vente.model';

export const sampleWithRequiredData: IPaiementVente = {
  id: 16210,
  montant: 22260.55,
  statut: 'PAYE',
  datePaiement: dayjs('2026-05-05T05:34'),
};

export const sampleWithPartialData: IPaiementVente = {
  id: 8030,
  montant: 27439.63,
  statut: 'PARTIEL',
  referencePaiement: 'boo regarding',
  datePaiement: dayjs('2026-05-05T03:50'),
};

export const sampleWithFullData: IPaiementVente = {
  id: 12363,
  montant: 6196.15,
  statut: 'EN_ATTENTE',
  referencePaiement: 'when',
  datePaiement: dayjs('2026-05-04T15:08'),
};

export const sampleWithNewData: NewPaiementVente = {
  montant: 9280.48,
  statut: 'PAYE',
  datePaiement: dayjs('2026-05-04T23:00'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
