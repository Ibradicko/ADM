import dayjs from 'dayjs/esm';

import { ILocataire, NewLocataire } from './locataire.model';

export const sampleWithRequiredData: ILocataire = {
  id: 12384,
  code: 'who wilderness past',
  nom: 'pish',
  typeLocataire: 'PERSONNE_PHYSIQUE',
  statut: 'INACTIF',
  dateCreation: dayjs('2026-05-05T00:05'),
};

export const sampleWithPartialData: ILocataire = {
  id: 11367,
  code: 'finally fluffy',
  nom: 'blaring toward weep',
  typeLocataire: 'PERSONNE_MORALE',
  telephone: '310.296.6072',
  adresse: 'by scarcely',
  statut: 'ACTIF',
  dateCreation: dayjs('2026-05-04T22:47'),
};

export const sampleWithFullData: ILocataire = {
  id: 29993,
  code: 'internationalize badly if',
  nom: 'extra-large direct wherever',
  typeLocataire: 'PERSONNE_MORALE',
  numeroIdentification: 'behind warp bah',
  telephone: '(555) 521-4648 x34857',
  email: 'Yessenia_Senger27@gmail.com',
  adresse: 'near royal hmph',
  statut: 'ACTIF',
  dateCreation: dayjs('2026-05-04T16:51'),
};

export const sampleWithNewData: NewLocataire = {
  code: 'ethyl geez propound',
  nom: 'fence',
  typeLocataire: 'PERSONNE_PHYSIQUE',
  statut: 'INACTIF',
  dateCreation: dayjs('2026-05-04T13:37'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
