INSERT INTO db2dataprocessor_out.patient (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0001', '2001-01-01', 'male');

INSERT INTO db2dataprocessor_out.encounter (enc_id, enc_patient_ref, enc_diagnosis_condition_ref, enc_class_code, enc_period_start,
                              enc_period_end)
VALUES ('HOSP-0001-E-11', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-01-04'),
       ('HOSP-0001-E-12', 'Patient/HOSP-0001', '', 'AMB', '2018-01-01', '2018-01-04'),
       ('HOSP-0001-E-13', 'Patient/HOSP-0001', '', 'IMP', '2019-01-01', '2019-01-04'),
       ('HOSP-0001-E-14', 'Patient/HOSP-0001', '', 'IMP', '2020-01-01', '2020-01-03');