export interface IModePaiementRef {
  id: number;
  code?: string | null;
  libelle?: string | null;
  actif?: boolean | null;
}

export type NewModePaiementRef = Omit<IModePaiementRef, 'id'> & { id: null };
