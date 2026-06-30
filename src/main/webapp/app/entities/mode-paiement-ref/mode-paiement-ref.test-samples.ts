import { IModePaiementRef, NewModePaiementRef } from './mode-paiement-ref.model';

export const sampleWithRequiredData: IModePaiementRef = {
  id: 11131,
  code: 'psst',
  libelle: 'colligate the brook',
  actif: false,
};

export const sampleWithPartialData: IModePaiementRef = {
  id: 11793,
  code: 'generally er lawmaker',
  libelle: 'baa ouch',
  actif: true,
};

export const sampleWithFullData: IModePaiementRef = {
  id: 28609,
  code: 'descendant if',
  libelle: 'why monumental',
  actif: false,
};

export const sampleWithNewData: NewModePaiementRef = {
  code: 'eyeliner',
  libelle: 'apricot shyly treble',
  actif: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
