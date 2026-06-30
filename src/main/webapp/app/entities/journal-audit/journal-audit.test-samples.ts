import dayjs from 'dayjs/esm';

import { IJournalAudit, NewJournalAudit } from './journal-audit.model';

export const sampleWithRequiredData: IJournalAudit = {
  id: 9789,
  typeAction: 'MOUVEMENT_STOCK',
  dateAction: dayjs('2026-05-04T14:07'),
};

export const sampleWithPartialData: IJournalAudit = {
  id: 31792,
  typeAction: 'CREATION',
  adresseIp: 'because times phooey',
  dateAction: dayjs('2026-05-04T22:29'),
};

export const sampleWithFullData: IJournalAudit = {
  id: 31746,
  typeAction: 'INVENTAIRE',
  entiteConcernee: 'democratize baggy norm',
  identifiantEntite: 'colorfully fully pale',
  description: '../fake-data/blob/hipster.txt',
  adresseIp: 'interesting serene',
  dateAction: dayjs('2026-05-04T16:55'),
};

export const sampleWithNewData: NewJournalAudit = {
  typeAction: 'EXPORT',
  dateAction: dayjs('2026-05-04T21:32'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
