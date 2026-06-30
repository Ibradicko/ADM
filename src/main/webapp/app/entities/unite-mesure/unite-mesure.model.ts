export interface IUniteMesure {
  id: number;
  code?: string | null;
  libelle?: string | null;
  symbole?: string | null;
}

export type NewUniteMesure = Omit<IUniteMesure, 'id'> & { id: null };
