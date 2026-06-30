import dayjs from 'dayjs/esm';

export interface ILotEtiquettes {
  id: number;
  reference?: string | null;
  dateGeneration?: dayjs.Dayjs | null;
  formatImpression?: string | null;
  nombreEtiquettes?: number | null;
}

export type NewLotEtiquettes = Omit<ILotEtiquettes, 'id'> & { id: null };
