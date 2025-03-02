INSERT INTO db_log.patient (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0011', '2001-01-01', 'male'),
       ('HOSP-0012', '2001-01-01', 'female'),
       ('HOSP-0013', '1951-01-01', 'male'),
       ('HOSP-0014', '1951-01-01', 'female'),
       ('HOSP-0015', '1951-01-01', 'male'),

       ('HOSP-0021', '2002-01-01', 'male'),
       ('HOSP-0022', '2002-01-01', 'female'),
       ('HOSP-0023', '1952-01-01', 'male'),
       ('HOSP-0024', '1952-01-01', 'female'),
       ('HOSP-0025', '1952-01-01', 'male'),

       ('HOSP-0031', '2003-01-01', 'male'),
       ('HOSP-0032', '2003-01-01', 'female'),
       ('HOSP-0033', '1953-01-01', 'male'),
       ('HOSP-0034', '1953-01-01', 'female'),
       ('HOSP-0035', '1953-01-01', 'male'),

       ('HOSP-0004', '1940-01-03', 'male');

INSERT INTO db_log.encounter (enc_id, enc_patient_id, enc_diagnosis_condition_id, enc_class_code, enc_period_start,
                              enc_period_end)
VALUES ('HOSP-0011-E-1', 'Patient/HOSP-0011', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0012-E-1', 'Patient/HOSP-0012', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0013-E-1', 'Patient/HOSP-0013', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0014-E-1', 'Patient/HOSP-0014', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0015-E-1', 'Patient/HOSP-0015', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0021-E-1', 'Patient/HOSP-0021', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0022-E-1', 'Patient/HOSP-0022', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0023-E-1', 'Patient/HOSP-0023', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0024-E-1', 'Patient/HOSP-0024', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0025-E-1', 'Patient/HOSP-0025', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0031-E-1', 'Patient/HOSP-0031', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0032-E-1', 'Patient/HOSP-0032', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0033-E-1', 'Patient/HOSP-0033', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0034-E-1', 'Patient/HOSP-0034', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0035-E-1', 'Patient/HOSP-0035', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0004-E-1', 'Patient/HOSP-0004', '', 'IMP', '2017-01-01', '2017-02-01');

INSERT INTO db_log.medication (med_id, med_code_system, med_code_code)
VALUES ('Medication-1', 'http://fhir.de/CodeSystem/bfarm/atc', 'atc1'),
       ('Medication-2', 'http://fhir.de/CodeSystem/bfarm/atc', 'atc2');

INSERT INTO db_log.medicationadministration (medadm_id, medadm_encounter_id, medadm_patient_id,
                                             medadm_effectivedatetime, medadm_medicationreference_id)
VALUES ('HOSP-0011-E-1-MA-1', 'Encounter/HOSP-0011-E-1', 'Patient/HOSP-0011', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0012-E-1-MA-1', 'Encounter/HOSP-0012-E-1', 'Patient/HOSP-0012', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0013-E-1-MA-1', 'Encounter/HOSP-0013-E-1', 'Patient/HOSP-0013', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0014-E-1-MA-1', 'Encounter/HOSP-0014-E-1', 'Patient/HOSP-0014', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0015-E-1-MA-1', 'Encounter/HOSP-0015-E-1', 'Patient/HOSP-0015', '2019-01-01', 'Medication/Medication-1'),
       ('HOSP-0004-E-1-MA-1', 'Encounter/HOSP-0004-E-1', 'Patient/HOSP-0004', '2021-01-01', 'Medication/Medication-2');

INSERT INTO db_log.medicationrequest (medreq_id, medreq_encounter_id, medreq_patient_id,
                                      medreq_authoredon, medreq_medicationreference_id)
VALUES ('HOSP-0021-E-1-MR-1', 'Encounter/HOSP-0021-E-1', 'Patient/HOSP-0021', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0022-E-1-MR-1', 'Encounter/HOSP-0022-E-1', 'Patient/HOSP-0022', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0023-E-1-MR-1', 'Encounter/HOSP-0023-E-1', 'Patient/HOSP-0023', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0024-E-1-MR-1', 'Encounter/HOSP-0024-E-1', 'Patient/HOSP-0024', '2020-01-01', 'Medication/Medication-1'),
       ('HOSP-0025-E-1-MR-1', 'Encounter/HOSP-0025-E-1', 'Patient/HOSP-0025', '2019-01-01', 'Medication/Medication-1');

INSERT INTO db_log.medicationstatement (medstat_id, medstat_encounter_id, medstat_patient_id,
                                        medstat_effectivedatetime, medstat_medicationreference_id)
VALUES ('HOSP-0031-E-1-MS-1', 'Encounter/HOSP-0031-E-1', 'Patient/HOSP-0031', '2020-01-01',
        'Medication/Medication-1'),
       ('HOSP-0032-E-1-MS-1', 'Encounter/HOSP-0032-E-1', 'Patient/HOSP-0032', '2020-01-01',
        'Medication/Medication-1'),
       ('HOSP-0033-E-1-MS-1', 'Encounter/HOSP-0033-E-1', 'Patient/HOSP-0033', '2020-01-01',
        'Medication/Medication-1'),
       ('HOSP-0034-E-1-MS-1', 'Encounter/HOSP-0034-E-1', 'Patient/HOSP-0034', '2020-01-01',
        'Medication/Medication-1'),
       ('HOSP-0035-E-1-MS-1', 'Encounter/HOSP-0035-E-1', 'Patient/HOSP-0035', '2019-01-01',
        'Medication/Medication-1');