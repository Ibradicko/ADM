import dayjs from 'dayjs/esm';

import { StatutGeneral } from 'app/entities/enumerations/statut-general.model';
import { TypeLocataire } from 'app/entities/enumerations/type-locataire.model';

export interface ILocataire {
  id: number;
  code?: string | null;
  nom?: string | null;
  typeLocataire?: keyof typeof TypeLocataire | null;
  numeroIdentification?: string | null;
  telephone?: string | null;
  email?: string | null;
  adresse?: string | null;
  statut?: keyof typeof StatutGeneral | null;
  dateCreation?: dayjs.Dayjs | null;
  loginGenere?: string | null;
}

export type NewLocataire = Omit<ILocataire, 'id'> & { id: null };
