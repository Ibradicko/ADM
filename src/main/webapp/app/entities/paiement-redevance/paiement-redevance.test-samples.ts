import dayjs from 'dayjs/esm';

import { IPaiementRedevance, NewPaiementRedevance } from './paiement-redevance.model';

export const sampleWithRequiredData: IPaiementRedevance = {
  id: 5556,
  reference: 'oh roughly',
  montant: 11620.77,
  datePaiement: dayjs('2026-05-05'),
};

export const sampleWithPartialData: IPaiementRedevance = {
  id: 25303,
  reference: 'cricket',
  montant: 21739.89,
  datePaiement: dayjs('2026-05-04'),
};

export const sampleWithFullData: IPaiementRedevance = {
  id: 13561,
  reference: 'provided',
  montant: 27660.91,
  datePaiement: dayjs('2026-05-04'),
  modePaiement: 'outdo',
  commentaire: 'off voluntarily',
};

export const sampleWithNewData: NewPaiementRedevance = {
  reference: 'as hence',
  montant: 31856.41,
  datePaiement: dayjs('2026-05-05'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
