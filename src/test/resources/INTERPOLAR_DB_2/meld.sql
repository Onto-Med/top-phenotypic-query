INSERT INTO db2dataprocessor_out.patient (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0001', '1960-01-01', 'female'),
       ('HOSP-0002', '1960-01-01', 'female'),
       ('HOSP-0003', '1980-01-01', 'male'),
       ('HOSP-0004', '2000-01-01', 'male'),
       ('HOSP-0005', '2020-01-01', 'female'),
       ('HOSP-0006', '1960-01-01', 'female'),
       ('HOSP-0007', '1980-01-01', 'male'),
       ('HOSP-0008', '2000-01-01', 'male'),
       ('HOSP-0009', '2020-01-01', 'female');

INSERT INTO db2dataprocessor_out.encounter (enc_id, enc_patient_ref, enc_diagnosis_condition_ref, enc_class_code, enc_period_start,
                              enc_period_end)
VALUES ('HOSP-0001-E-11', 'Patient/HOSP-0001', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0002-E-22', 'Patient/HOSP-0002', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0003-E-33', 'Patient/HOSP-0003', '', 'IMP', '2018-01-01', '2018-02-01'),
       ('HOSP-0004-E-44', 'Patient/HOSP-0004', 'Procedure/HOSP-0004-E-44-P-441', 'IMP', '2019-01-01', '2019-02-01'),
       ('HOSP-0004-E-44', 'Patient/HOSP-0004', 'Procedure/HOSP-0004-E-44-P-442', 'IMP', '2019-01-01', '2019-02-01'),
       ('HOSP-0005-E-55', 'Patient/HOSP-0005', '', 'AMB', '2020-01-01', '2020-02-01'),
       ('HOSP-0006-E-66', 'Patient/HOSP-0006', '', 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0007-E-77', 'Patient/HOSP-0007', '', 'IMP', '2018-01-01', '2018-02-01'),
       ('HOSP-0008-E-88', 'Patient/HOSP-0008', 'Procedure/HOSP-0008-E-88-P-881', 'IMP', '2019-01-01', '2019-02-01'),
       ('HOSP-0009-E-99', 'Patient/HOSP-0009', '', 'AMB', '2020-01-01', '2020-02-01');

INSERT INTO db2dataprocessor_out.medication (med_id, med_code_system, med_code_code)
VALUES ('Medication-1', 'http://fhir.de/CodeSystem/bfarm/atc', 'B01AA'),
       ('Medication-1', 'http://fhir.de/CodeSystem/bfarm/atc', 'B01AF');

INSERT INTO db2dataprocessor_out.medicationadministration (medadm_id, medadm_encounter_ref, medadm_patient_ref,
                                             medadm_effectivedatetime, medadm_medicationreference_ref)
VALUES ('HOSP-0003-E-33-MA-1', 'Encounter/HOSP-0003-E-33', 'Patient/HOSP-0003', '2020-01-01',
        'Medication/Medication-1');

INSERT INTO db2dataprocessor_out."procedure" (proc_id, proc_patient_ref, proc_code_system, proc_code_code, proc_performeddatetime)
VALUES ('HOSP-0004-E-44-P-441', 'Patient/HOSP-0004', 'http://fhir.de/CodeSystem/bfarm/ops', '8-853', current_date - 8),
       ('HOSP-0004-E-44-P-442', 'Patient/HOSP-0004', 'http://fhir.de/CodeSystem/bfarm/ops', '8-853', current_date - 6),
       ('HOSP-0008-E-88-P-881', 'Patient/HOSP-0008', 'http://fhir.de/CodeSystem/bfarm/ops', '8-853', current_date - 6);

INSERT INTO db2dataprocessor_out.observation (obs_id, obs_encounter_ref, obs_patient_ref, obs_code_system, obs_code_code,
                                obs_effectivedatetime, obs_valuequantity_value)
VALUES ('HOSP-0001-E-11-OL-1', 'Encounter/HOSP-0001-E-11', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0',
        '2020-01-01', 1.1),
       ('HOSP-0001-E-11-OL-2', 'Encounter/HOSP-0001-E-11', 'Patient/HOSP-0001', 'http://loinc.org', '42719-5',
        '2020-01-01', 1.2),
       ('HOSP-0001-E-11-OL-3', 'Encounter/HOSP-0001-E-11', 'Patient/HOSP-0001', 'http://loinc.org', '6301-6',
        '2020-01-01', 1.3),

       ('HOSP-0002-E-22-OL-1', 'Encounter/HOSP-0002-E-22', 'Patient/HOSP-0002', 'http://loinc.org', '2160-0',
        '2020-01-01', 2.1),
       ('HOSP-0002-E-22-OL-2', 'Encounter/HOSP-0002-E-22', 'Patient/HOSP-0002', 'http://loinc.org', '42719-5',
        '2020-01-01', 2.2),
       ('HOSP-0002-E-22-OL-3', 'Encounter/HOSP-0002-E-22', 'Patient/HOSP-0002', 'http://loinc.org', '6301-6',
        '2020-01-01', 2.3),

       ('HOSP-0003-E-33-OL-1', 'Encounter/HOSP-0003-E-33', 'Patient/HOSP-0003', 'http://loinc.org', '2160-0',
        '2020-01-01', 3.1),
       ('HOSP-0003-E-33-OL-2', 'Encounter/HOSP-0003-E-33', 'Patient/HOSP-0003', 'http://loinc.org', '42719-5',
        '2020-01-01', 3.2),
       ('HOSP-0003-E-33-OL-3', 'Encounter/HOSP-0003-E-33', 'Patient/HOSP-0003', 'http://loinc.org', '6301-6',
        '2020-01-01', 3.3),

       ('HOSP-0004-E-44-OL-1', 'Encounter/HOSP-0004-E-44', 'Patient/HOSP-0004', 'http://loinc.org', '2160-0',
        '2020-01-01', 0.1),
       ('HOSP-0004-E-44-OL-2', 'Encounter/HOSP-0004-E-44', 'Patient/HOSP-0004', 'http://loinc.org', '42719-5',
        '2020-01-01', 0.2),
       ('HOSP-0004-E-44-OL-3', 'Encounter/HOSP-0004-E-44', 'Patient/HOSP-0004', 'http://loinc.org', '6301-6',
        '2020-01-01', 0.3),

       ('HOSP-0005-E-55-OL-1', 'Encounter/HOSP-0005-E-55', 'Patient/HOSP-0005', 'http://loinc.org', '2160-0',
        '2020-01-01', 2.1),
       ('HOSP-0005-E-55-OL-2', 'Encounter/HOSP-0005-E-55', 'Patient/HOSP-0005', 'http://loinc.org', '42719-5',
        '2020-01-01', 2.2),

       ('HOSP-0006-E-66-OL-1', 'Encounter/HOSP-0006-E-66', 'Patient/HOSP-0006', 'http://loinc.org', '2160-0',
        '2020-01-01', 2.1),
       ('HOSP-0006-E-66-OL-2', 'Encounter/HOSP-0006-E-66', 'Patient/HOSP-0006', 'http://loinc.org', '6301-6',
        '2020-01-01', 2.3),

       ('HOSP-0007-E-77-OL-1', 'Encounter/HOSP-0007-E-77', 'Patient/HOSP-0007', 'http://loinc.org', '42719-5',
        '2020-01-01', 2.2),
       ('HOSP-0007-E-77-OL-2', 'Encounter/HOSP-0007-E-77', 'Patient/HOSP-0007', 'http://loinc.org', '6301-6',
        '2020-01-01', 2.3),

       ('HOSP-0008-E-88-OL-1', 'Encounter/HOSP-0008-E-88', 'Patient/HOSP-0008', 'http://loinc.org', '42719-5',
        '2020-01-01', 3.2),
       ('HOSP-0008-E-88-OL-2', 'Encounter/HOSP-0008-E-88', 'Patient/HOSP-0008', 'http://loinc.org', '6301-6',
        '2020-01-01', 3.3),

       ('HOSP-0009-E-99-OL-1', 'Encounter/HOSP-0009-E-99', 'Patient/HOSP-0009', 'http://loinc.org', '2160-0',
        '2020-01-01', 0.1),
       ('HOSP-0009-E-99-OL-2', 'Encounter/HOSP-0009-E-99', 'Patient/HOSP-0009', 'http://loinc.org', '2160-0',
        '2019-01-02', 2.1),
       ('HOSP-0009-E-99-OL-3', 'Encounter/HOSP-0009-E-99', 'Patient/HOSP-0009', 'http://loinc.org', '42719-5',
        '2020-01-01', 0.2),
       ('HOSP-0009-E-99-OL-4', 'Encounter/HOSP-0009-E-99', 'Patient/HOSP-0009', 'http://loinc.org', '6301-6',
        '2020-01-01', 0.3);