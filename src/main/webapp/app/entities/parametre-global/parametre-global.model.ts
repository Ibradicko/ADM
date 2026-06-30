export interface IParametreGlobal {
  id: number;
  code?: string | null;
  valeur?: string | null;
  description?: string | null;
  actif?: boolean | null;
}

export type NewParametreGlobal = Omit<IParametreGlobal, 'id'> & { id: null };
