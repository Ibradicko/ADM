import { IGroupeArticle, NewGroupeArticle } from './groupe-article.model';

export const sampleWithRequiredData: IGroupeArticle = {
  id: 12119,
  code: 'pleased unlucky jeopardise',
  libelle: 'blue tired abnormally',
  statut: 'SUSPENDU',
  tauxRedevance: 5,
};

export const sampleWithPartialData: IGroupeArticle = {
  id: 3410,
  code: 'boss ah',
  libelle: 'rudely celsius stool',
  statut: 'SUSPENDU',
  tauxRedevance: 7.5,
};

export const sampleWithFullData: IGroupeArticle = {
  id: 27467,
  code: 'bloom and sleet',
  libelle: 'oh',
  statut: 'SUSPENDU',
  tauxRedevance: 10,
};

export const sampleWithNewData: NewGroupeArticle = {
  code: 'plus',
  libelle: 'twine above',
  statut: 'SUSPENDU',
  tauxRedevance: 3,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
