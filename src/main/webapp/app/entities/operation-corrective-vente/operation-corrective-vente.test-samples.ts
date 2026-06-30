import dayjs from 'dayjs/esm';

import { IOperationCorrectiveVente, NewOperationCorrectiveVente } from './operation-corrective-vente.model';

export const sampleWithRequiredData: IOperationCorrectiveVente = {
  id: 4471,
  typeOperation: 'AJUSTEMENT',
  motif: 'till than',
  dateOperation: dayjs('2026-05-05T02:53'),
};

export const sampleWithPartialData: IOperationCorrectiveVente = {
  id: 3991,
  typeOperation: 'AJUSTEMENT',
  motif: 'ideal shudder',
  dateOperation: dayjs('2026-05-05T07:02'),
};

export const sampleWithFullData: IOperationCorrectiveVente = {
  id: 13270,
  typeOperation: 'ANNULATION',
  motif: 'until',
  montantImpact: 7008.81,
  dateOperation: dayjs('2026-05-05T09:33'),
};

export const sampleWithNewData: NewOperationCorrectiveVente = {
  typeOperation: 'RETOUR',
  motif: 'fibre',
  dateOperation: dayjs('2026-05-05T08:37'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
