import dayjs from 'dayjs/esm';

import { IRegularisationRedevance, NewRegularisationRedevance } from './regularisation-redevance.model';

export const sampleWithRequiredData: IRegularisationRedevance = {
  id: 25000,
  reference: 'jungle astride',
  montant: 30162.98,
  motif: 'sadly sin',
  dateRegularisation: dayjs('2026-05-05T05:36'),
};

export const sampleWithPartialData: IRegularisationRedevance = {
  id: 24832,
  reference: 'huzzah as',
  montant: 29841.11,
  motif: 'packaging density',
  dateRegularisation: dayjs('2026-05-05T01:51'),
};

export const sampleWithFullData: IRegularisationRedevance = {
  id: 9396,
  reference: 'lively',
  montant: 13209.26,
  motif: 'rally',
  dateRegularisation: dayjs('2026-05-04T23:43'),
};

export const sampleWithNewData: NewRegularisationRedevance = {
  reference: 'inasmuch broadcast gaseous',
  montant: 16831.21,
  motif: 'icebreaker signature sunbathe',
  dateRegularisation: dayjs('2026-05-04T18:31'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
