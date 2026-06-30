import dayjs from 'dayjs/esm';

import { ICodeBarresProduit, NewCodeBarresProduit } from './code-barres-produit.model';

export const sampleWithRequiredData: ICodeBarresProduit = {
  id: 6617,
  code: 'even since complicated',
  type: 'INTERNE',
  principal: true,
  genereParSysteme: false,
  actif: true,
  dateAffectation: dayjs('2026-05-05T07:21'),
};

export const sampleWithPartialData: ICodeBarresProduit = {
  id: 28944,
  code: 'scarcely readily',
  type: 'CODE128',
  principal: false,
  genereParSysteme: true,
  actif: true,
  dateAffectation: dayjs('2026-05-04T19:54'),
};

export const sampleWithFullData: ICodeBarresProduit = {
  id: 9044,
  code: 'tabulate junior',
  type: 'CODE128',
  principal: true,
  genereParSysteme: true,
  actif: true,
  dateAffectation: dayjs('2026-05-04T17:20'),
};

export const sampleWithNewData: NewCodeBarresProduit = {
  code: 'noted ack noteworthy',
  type: 'EAN8',
  principal: true,
  genereParSysteme: true,
  actif: true,
  dateAffectation: dayjs('2026-05-04T18:13'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
