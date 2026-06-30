import dayjs from 'dayjs/esm';

import { ITicketCaisse, NewTicketCaisse } from './ticket-caisse.model';

export const sampleWithRequiredData: ITicketCaisse = {
  id: 15107,
  numero: 'bracelet lest availability',
  dateEmission: dayjs('2026-05-05T08:50'),
  nombreImpressions: 21567,
};

export const sampleWithPartialData: ITicketCaisse = {
  id: 12663,
  numero: 'boiling meh',
  dateEmission: dayjs('2026-05-04T23:58'),
  nombreImpressions: 4379,
  contenu: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: ITicketCaisse = {
  id: 13794,
  numero: 'concerning sometimes',
  dateEmission: dayjs('2026-05-04T21:31'),
  nombreImpressions: 11989,
  contenu: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewTicketCaisse = {
  numero: 'huzzah although source',
  dateEmission: dayjs('2026-05-05T01:41'),
  nombreImpressions: 9621,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
