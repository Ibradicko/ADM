import dayjs from 'dayjs/esm';

import { IScanInconnu, NewScanInconnu } from './scan-inconnu.model';

export const sampleWithRequiredData: IScanInconnu = {
  id: 17541,
  codeScanne: 'muffled appreciate tectonics',
  dateScan: dayjs('2026-05-04T23:32'),
  resolu: true,
};

export const sampleWithPartialData: IScanInconnu = {
  id: 20147,
  codeScanne: 'failing upwardly making',
  dateScan: dayjs('2026-05-04T15:22'),
  commentaire: 'bandwidth',
  resolu: true,
};

export const sampleWithFullData: IScanInconnu = {
  id: 6200,
  codeScanne: 'fortunately likewise',
  ecranOrigine: 'vacantly',
  dateScan: dayjs('2026-05-04T11:49'),
  commentaire: 'after circulate yet',
  resolu: true,
};

export const sampleWithNewData: NewScanInconnu = {
  codeScanne: 'bogus babushka toward',
  dateScan: dayjs('2026-05-05T06:17'),
  resolu: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
