import dayjs from 'dayjs/esm';

import { ITransfertStock, NewTransfertStock } from './transfert-stock.model';

export const sampleWithRequiredData: ITransfertStock = {
  id: 32563,
  reference: 'fooey yet qua',
  dateTransfert: dayjs('2026-05-04T15:22'),
  statut: 'BROUILLON',
};

export const sampleWithPartialData: ITransfertStock = {
  id: 14663,
  reference: 'anenst afraid sans',
  dateTransfert: dayjs('2026-05-04T12:54'),
  statut: 'VALIDE',
};

export const sampleWithFullData: ITransfertStock = {
  id: 13827,
  reference: 'impressive abnormally',
  dateTransfert: dayjs('2026-05-05T09:59'),
  statut: 'VALIDE',
  motif: 'meanwhile gosh graffiti',
};

export const sampleWithNewData: NewTransfertStock = {
  reference: 'what wear reborn',
  dateTransfert: dayjs('2026-05-05T08:36'),
  statut: 'VALIDE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
