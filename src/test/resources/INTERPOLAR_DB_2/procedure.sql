INSERT INTO db2dataprocessor_out.v_patient (pat_id, pat_birthdate, pat_gender)
VALUES ('HOSP-0001', '1960-01-01', 'female'),
       ('HOSP-0002', '1960-01-01', 'female'),
       ('HOSP-0003', '1980-01-01', 'male'  ),
       ('HOSP-0004', '2000-01-01', 'male'  ),
       ('HOSP-0005', '2001-01-01', 'female'),
       ('HOSP-0006', '2002-01-01', 'female'),
       ('HOSP-0007', '2003-01-01', 'male'  ),
       ('HOSP-0008', '2004-01-01', 'male'  );

INSERT INTO db2dataprocessor_out.v_encounter (enc_id, enc_patient_ref, enc_class_code, enc_period_start, enc_period_end)
VALUES ('HOSP-0001-E1', 'Patient/HOSP-0001', 'IMP', '2026-04-01', '2026-04-11'),
       ('HOSP-0002-E2', 'Patient/HOSP-0002', 'AMB', '2026-04-02', '2026-04-12'),
       ('HOSP-0003-E3', 'Patient/HOSP-0003', 'IMP', '2026-04-03', '2026-04-13'),
       ('HOSP-0004-E4', 'Patient/HOSP-0004', 'AMB', '2026-04-04', '2026-04-14'),
       ('HOSP-0005-E5', 'Patient/HOSP-0005', 'IMP', '2026-04-05', '2026-04-15'),
       ('HOSP-0006-E6', 'Patient/HOSP-0006', 'AMB', '2026-04-06', '2026-04-16'),
       ('HOSP-0007-E7', 'Patient/HOSP-0007', 'IMP', '2026-04-07', '2026-04-17'),
       ('HOSP-0008-E8', 'Patient/HOSP-0008', 'AMB', '2026-04-08', '2026-04-18');


INSERT INTO db2dataprocessor_out.v_procedure (proc_id, proc_encounter_calculated_ref, proc_patient_ref, proc_code_system, proc_code_code, proc_performedperiod_start, proc_performedperiod_end, proc_performeddatetime)
VALUES ('HOSP-0001-E1-C1', 'Encounter/HOSP-0001-E1', 'Patient/HOSP-0001', 'http://fhir.de/CodeSystem/bfarm/ops', '8-853.3',  null,         null,         '2026-04-01'),
       ('HOSP-0002-E2-C2', 'Encounter/HOSP-0002-E2', 'Patient/HOSP-0002', 'http://fhir.de/CodeSystem/bfarm/ops', '8-853.3',  '2026-04-01', '2026-04-02', null        ),
       ('HOSP-0003-E3-C3', 'Encounter/HOSP-0003-E3', 'Patient/HOSP-0003', 'http://fhir.de/CodeSystem/bfarm/ops', '8-855.71', '2026-04-02', null,         '2026-04-02'),
       ('HOSP-0004-E4-C4', 'Encounter/HOSP-0004-E4', 'Patient/HOSP-0004', 'http://fhir.de/CodeSystem/bfarm/ops', '8-853.3',  '2026-04-03', '2026-04-03', null        ),
       ('HOSP-0005-E5-C5', 'Encounter/HOSP-0005-E5', 'Patient/HOSP-0005', 'http://fhir.de/CodeSystem/bfarm/ops', '8-857.21', '2026-04-04', null,         null        ),
       ('HOSP-0006-E6-C6', 'Encounter/HOSP-0006-E6', 'Patient/HOSP-0006', 'http://fhir.de/CodeSystem/bfarm/ops', '8-853.3',  '2026-04-05', '2026-04-06', null        ),
       ('HOSP-0007-E7-C7', 'Encounter/HOSP-0007-E7', 'Patient/HOSP-0007', 'http://fhir.de/CodeSystem/bfarm/ops', '8-853.3',  '2026-04-07', '2026-04-08', '2026-04-09'),
       ('HOSP-0008-E8-C8', 'Encounter/HOSP-0008-E8', 'Patient/HOSP-0008', 'http://fhir.de/CodeSystem/bfarm/ops', '8-855.71', '2026-04-11', '2026-04-05', null        );