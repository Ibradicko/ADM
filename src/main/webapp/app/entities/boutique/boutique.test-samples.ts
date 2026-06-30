import dayjs from 'dayjs/esm';

import { IBoutique, NewBoutique } from './boutique.model';

export const sampleWithRequiredData: IBoutique = {
  id: 9145,
  code: 'unit right so',
  nom: 'spook jump stunning',
  statut: 'SUSPENDU',
  dateCreation: dayjs('2026-05-04T20:31'),
};

export const sampleWithPartialData: IBoutique = {
  id: 13273,
  code: 'countess gah since',
  nom: 'but',
  statut: 'SUSPENDU',
  dateCreation: dayjs('2026-05-05T03:49'),
};

export const sampleWithFullData: IBoutique = {
  id: 18891,
  code: 'premium',
  nom: 'saloon',
  type: 'DUTY_FREE',
  emplacement: 'aside during',
  telephone: '1-852-992-2957 x89049',
  statut: 'SUSPENDU',
  dateCreation: dayjs('2026-05-05T04:40'),
};

export const sampleWithNewData: NewBoutique = {
  code: 'nicely',
  nom: 'sometimes if woot',
  statut: 'ACTIF',
  dateCreation: dayjs('2026-05-04T14:29'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
