import { TypeCodeBarres } from 'app/entities/enumerations/type-code-barres.model';

export interface IParametreCodeBarres {
  id: number;
  formatParDefaut?: keyof typeof TypeCodeBarres | null;
  prefixe?: string | null;
  longueur?: number | null;
  actif?: boolean | null;
}

export type NewParametreCodeBarres = Omit<IParametreCodeBarres, 'id'> & { id: null };
