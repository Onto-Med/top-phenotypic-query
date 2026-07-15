INSERT INTO db2dataprocessor_out.v_patient_last_version (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0001', '1960-01-01', 'female'),
       ('HOSP-0002', '1960-01-01', 'female'),
       ('HOSP-0003', '1980-01-01', 'male'  ),
       ('HOSP-0004', '2000-01-01', 'male'  ),
       ('HOSP-0005', '2001-01-01', 'female'),
       ('HOSP-0006', '2002-01-01', 'female'),
       ('HOSP-0007', '2003-01-01', 'male'  ),
       ('HOSP-0008', '2004-01-01', 'male'  );

INSERT INTO db2dataprocessor_out.v_encounter_last_version (enc_id, enc_patient_ref, enc_class_code, enc_period_start, enc_period_end)
VALUES ('HOSP-0001-E1', 'Patient/HOSP-0001', 'IMP', '2026-04-01', '2026-04-11'),
       ('HOSP-0002-E2', 'Patient/HOSP-0002', 'AMB', '2026-04-02', '2026-04-12'),
       ('HOSP-0003-E3', 'Patient/HOSP-0003', 'IMP', '2026-04-03', '2026-04-13'),
       ('HOSP-0004-E4', 'Patient/HOSP-0004', 'AMB', '2026-04-04', '2026-04-14'),
       ('HOSP-0005-E5', 'Patient/HOSP-0005', 'IMP', '2026-04-05', '2026-04-15'),
       ('HOSP-0006-E6', 'Patient/HOSP-0006', 'AMB', '2026-04-06', '2026-04-16'),
       ('HOSP-0007-E7', 'Patient/HOSP-0007', 'IMP', '2026-04-07', '2026-04-17'),
       ('HOSP-0008-E8', 'Patient/HOSP-0008', 'AMB', '2026-04-08', '2026-04-18');


INSERT INTO db2dataprocessor_out.v_condition_last_version (con_id, con_encounter_calculated_ref, con_patient_ref, con_code_system, con_code_code, con_onsetperiod_start, con_onsetperiod_end, con_onsetdatetime, con_recordeddate)
VALUES ('HOSP-0001-E1-C1', 'Encounter/HOSP-0001-E1', 'Patient/HOSP-0001', 'http://snomed.info/sct', '135816001', null,         null,         null,         null        ),
       ('HOSP-0002-E2-C2', 'Encounter/HOSP-0002-E2', 'Patient/HOSP-0002', 'http://snomed.info/sct', '135816001', null,         null,         null,         '2026-04-01'),
       ('HOSP-0003-E3-C3', 'Encounter/HOSP-0003-E3', 'Patient/HOSP-0003', 'http://snomed.info/sct', '258157001', null,         null,         '2026-04-02', null        ),
       ('HOSP-0004-E4-C4', 'Encounter/HOSP-0004-E4', 'Patient/HOSP-0004', 'http://snomed.info/sct', '135816001', null,         '2026-04-03', null,         null        ),
       ('HOSP-0005-E5-C5', 'Encounter/HOSP-0005-E5', 'Patient/HOSP-0005', 'http://snomed.info/sct', '438949009', '2026-04-04', null,         null,         null        ),
       ('HOSP-0006-E6-C6', 'Encounter/HOSP-0006-E6', 'Patient/HOSP-0006', 'http://snomed.info/sct', '135816001', '2026-04-05', '2026-04-06', null,         null        ),
       ('HOSP-0007-E7-C7', 'Encounter/HOSP-0007-E7', 'Patient/HOSP-0007', 'http://snomed.info/sct', '135816001', '2026-04-07', '2026-04-08', '2026-04-09', '2026-04-05'),
       ('HOSP-0008-E8-C8', 'Encounter/HOSP-0008-E8', 'Patient/HOSP-0008', 'http://snomed.info/sct', '258157001', '2026-04-11', '2026-04-05', null,         null        );