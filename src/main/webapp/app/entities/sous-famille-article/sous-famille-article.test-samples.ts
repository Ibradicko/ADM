import { ISousFamilleArticle, NewSousFamilleArticle } from './sous-famille-article.model';

export const sampleWithRequiredData: ISousFamilleArticle = {
  id: 13143,
  code: 'gadzooks generally',
  libelle: 'playfully',
  statut: 'SUSPENDU',
};

export const sampleWithPartialData: ISousFamilleArticle = {
  id: 10746,
  code: 'dwell forswear second',
  libelle: 'made-up steep excepting',
  statut: 'ACTIF',
};

export const sampleWithFullData: ISousFamilleArticle = {
  id: 2820,
  code: 'ick',
  libelle: 'promptly swerve pish',
  statut: 'SUSPENDU',
};

export const sampleWithNewData: NewSousFamilleArticle = {
  code: 'without',
  libelle: 'colorful furthermore dismal',
  statut: 'ACTIF',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
