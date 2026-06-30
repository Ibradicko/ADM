import dayjs from 'dayjs/esm';

import { IVente, NewVente } from './vente.model';

export const sampleWithRequiredData: IVente = {
  id: 13032,
  numeroTicket: 'vast but toothbrush',
  dateHeure: dayjs('2026-05-04T23:29'),
  statut: 'RETOURNEE',
  montantBrut: 22264.79,
  montantNet: 15198.42,
};

export const sampleWithPartialData: IVente = {
  id: 15321,
  numeroTicket: 'unwieldy',
  dateHeure: dayjs('2026-05-05T01:50'),
  statut: 'ANNULEE',
  referenceCarteEmbarquement: 'but busily what',
  montantBrut: 32383.55,
  montantNet: 10254.48,
  commentaire: 'swiftly wherever faraway',
};

export const sampleWithFullData: IVente = {
  id: 2302,
  numeroTicket: 'throughout among justly',
  dateHeure: dayjs('2026-05-04T13:44'),
  statut: 'AJUSTEE',
  referencePassager: 'acquaintance once basket',
  referenceCarteEmbarquement: 'eek',
  montantBrut: 17363.42,
  montantRemise: 733.35,
  montantNet: 23984.66,
  commentaire: 'memorable pendant secrecy',
};

export const sampleWithNewData: NewVente = {
  numeroTicket: 'drowse brief ouch',
  dateHeure: dayjs('2026-05-04T16:03'),
  statut: 'BROUILLON',
  montantBrut: 7831.07,
  montantNet: 30766.64,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
