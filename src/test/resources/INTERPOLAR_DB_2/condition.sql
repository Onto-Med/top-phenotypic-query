INSERT INTO db2dataprocessor_out.v_patient (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0001', '1960-01-01', 'female'),
       ('HOSP-0002', '1960-01-01', 'female'),
       ('HOSP-0003', '1980-01-01', 'male'),
       ('HOSP-0004', '2000-01-01', 'male');

INSERT INTO db2dataprocessor_out.v_encounter (enc_id, enc_patient_ref, enc_class_code, enc_period_start,
                                              enc_period_end)
VALUES ('HOSP-0001-E-11', 'Patient/HOSP-0001', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0002-E-21', 'Patient/HOSP-0001', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0003-E-22', 'Patient/HOSP-0002', 'IMP', '2018-01-01', '2018-02-01'),
       ('HOSP-0004-E-31', 'Patient/HOSP-0003', 'IMP', '2019-01-01', '2019-02-01'),
       ('HOSP-0005-E-41', 'Patient/HOSP-0004', 'AMB', '2020-01-01', '2020-02-01'),
       ('HOSP-0006-E-42', 'Patient/HOSP-0004', 'IMP', '2017-01-01', '2017-02-01');


INSERT INTO db2dataprocessor_out.v_condition (con_id,
                                              con_encounter_calculated_ref,
                                              con_patient_ref,
                                              con_code_system,
                                              con_code_code,
                                              con_onsetperiod_start,
                                              con_onsetperiod_end,
                                              con_onsetdatetime,
                                              con_recordeddate)
VALUES ('HOSP-0001-E11-C1', 'Encounter/HOSP-0001-E11', 'Patient/HOSP-0001', 'http://snomed.info/sct', '438949009', '2000-01-01', null, null, null),
       ('HOSP-0001-E11-C2', 'Encounter/HOSP-0001-E11', 'Patient/HOSP-0001', 'http://snomed.info/sct', '135816001', null, null, '2026-04-21', null),
       ('HOSP-0002-E21-C1', 'Encounter/HOSP-0002-E21', 'Patient/HOSP-0002', 'http://snomed.info/sct', '258157001', '2026-04-21 13:00:00', '026-04-21 15:00:00', null, null),
       ('HOSP-0002-E22-C2', 'Encounter/HOSP-0002-E22', 'Patient/HOSP-0002', 'http://snomed.info/sct', '438949009', '2000-01-01', null, null, null),
       ('HOSP-0003-E31-C1', 'Encounter/HOSP-0003-E31', 'Patient/HOSP-0003', 'http://snomed.info/sct', '438949009', '2000-01-01', null, null, null),
       ('HOSP-0003-E31-C2', 'Encounter/HOSP-0003-E31', 'Patient/HOSP-0003', 'http://snomed.info/sct', '438949009', '2000-01-01', null, null, null),
       ('HOSP-0004-E41-C1', 'Encounter/HOSP-0004-E41', 'Patient/HOSP-0004', 'http://snomed.info/sct', '438949009', '2000-01-01', null, null, null),
       ('HOSP-0004-E42-C2', 'Encounter/HOSP-0004-E42', 'Patient/HOSP-0004', 'http://snomed.info/sct', '438949009', '2000-01-01', null, null, null);