INSERT INTO db_log.patient (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0001', '1940-01-01', 'female'),
       ('HOSP-0002', '1980-07-01', 'male');

INSERT INTO db_log.encounter (enc_id, enc_patient_ref, enc_diagnosis_condition_ref, enc_class_code, enc_period_start,
                              enc_period_end)
VALUES ('HOSP-0001-E-11', 'Patient/HOSP-0001', '', 'IMP', '2000-01-01', '2000-02-01'),
       ('HOSP-0001-E-12', 'Patient/HOSP-0001', '', 'IMP', '2010-01-01', '2010-02-01'),
       ('HOSP-0002-E-21', 'Patient/HOSP-0002', '', 'IMP', '1990-01-01', '1990-02-01'),
       ('HOSP-0002-E-22', 'Patient/HOSP-0002', '', 'IMP', '2020-01-01', '2020-02-01');