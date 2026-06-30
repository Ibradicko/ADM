import { IUniteMesure, NewUniteMesure } from './unite-mesure.model';

export const sampleWithRequiredData: IUniteMesure = {
  id: 32025,
  code: 'tentacle negotiation',
  libelle: 'bench authentic',
};

export const sampleWithPartialData: IUniteMesure = {
  id: 200,
  code: 'throughout scruple e',
  libelle: 'outlaw yowza yum',
};

export const sampleWithFullData: IUniteMesure = {
  id: 2349,
  code: 'gloomy inside',
  libelle: 'shocked aw',
  symbole: 'uh-huh achieve',
};

export const sampleWithNewData: NewUniteMesure = {
  code: 'better dimly because',
  libelle: 'indeed wound',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
