import { IFamilleArticle, NewFamilleArticle } from './famille-article.model';

export const sampleWithRequiredData: IFamilleArticle = {
  id: 29480,
  code: 'colorless',
  libelle: 'aw',
  statut: 'SUSPENDU',
};

export const sampleWithPartialData: IFamilleArticle = {
  id: 11586,
  code: 'stint whose',
  libelle: 'bob',
  statut: 'ACTIF',
};

export const sampleWithFullData: IFamilleArticle = {
  id: 25070,
  code: 'airport',
  libelle: 'trust hungry nocturnal',
  statut: 'INACTIF',
};

export const sampleWithNewData: NewFamilleArticle = {
  code: 'bolster ack yuck',
  libelle: 'amidst incidentally guilt',
  statut: 'SUSPENDU',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
