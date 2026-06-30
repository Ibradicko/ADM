import dayjs from 'dayjs/esm';

import { IProduit, NewProduit } from './produit.model';

export const sampleWithRequiredData: IProduit = {
  id: 10819,
  codeInterne: 'cap angelic huzzah',
  designation: 'passport',
  typePrix: 'PROMOTION',
  prixVente: 17199.34,
  statut: 'SUSPENDU',
  dateCreation: dayjs('2026-05-05T09:28'),
};

export const sampleWithPartialData: IProduit = {
  id: 6650,
  codeInterne: 'within hoof broken',
  designation: 'absent what',
  description: '../fake-data/blob/hipster.txt',
  typePrix: 'PROMOTION',
  prixVente: 2979.84,
  tauxRedevanceApplicable: 27.07,
  statut: 'SUSPENDU',
  dateCreation: dayjs('2026-05-04T13:33'),
};

export const sampleWithFullData: IProduit = {
  id: 2644,
  codeInterne: 'towards always oh',
  designation: 'aside busily solution',
  description: '../fake-data/blob/hipster.txt',
  typePrix: 'CONTRACTUEL',
  prixVente: 8354.39,
  tauxRedevanceApplicable: 49.41,
  statut: 'ACTIF',
  dateCreation: dayjs('2026-05-04T10:24'),
};

export const sampleWithNewData: NewProduit = {
  codeInterne: 'altruistic mean',
  designation: 'save woot',
  typePrix: 'STANDARD',
  prixVente: 20783.05,
  statut: 'SUSPENDU',
  dateCreation: dayjs('2026-05-04T19:59'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
