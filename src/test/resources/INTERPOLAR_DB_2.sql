CREATE SCHEMA db2dataprocessor_out;

CREATE TABLE db2dataprocessor_out.v_patient_all
(
    pat_id        varchar NOT NULL,
    pat_birthdate date NULL,
    pat_gender    varchar NULL,
    PRIMARY KEY (pat_id)
);

CREATE TABLE db2dataprocessor_out.v_encounter_all
(
    enc_id                      varchar NOT NULL,
    enc_patient_ref             varchar NULL,
    enc_diagnosis_condition_ref varchar NOT NULL,
    enc_class_code              varchar NULL,
    enc_period_start            timestamp NULL,
    enc_period_end              timestamp NULL,
    PRIMARY KEY (enc_id, enc_diagnosis_condition_ref)
);

CREATE TABLE db2dataprocessor_out.v_observation_all
(
    obs_id                  varchar NOT NULL,
    obs_main_encounter_calculated_ref       varchar NULL,
    obs_patient_ref         varchar NULL,
    obs_code_system         varchar NOT NULL,
    obs_code_code           varchar NOT NULL,
    obs_effectivedatetime   timestamp NULL,
    obs_valuequantity_value float8 NULL,
    obs_valuequantity_code  varchar NULL,
    PRIMARY KEY (obs_id, obs_code_system, obs_code_code)
);

CREATE TABLE db2dataprocessor_out."v_condition_all"
(
    con_id               varchar NOT NULL,
    con_patient_ref      varchar NULL,
    con_identifier_value varchar NULL,
    con_code_system      varchar NULL,
    con_code_code        varchar NULL,
    con_code_display     varchar NULL,
    con_code_text        varchar NULL,
    con_recordeddate     timestamp NULL,
    PRIMARY KEY (con_id)
);

CREATE TABLE db2dataprocessor_out."v_procedure_all"
(
    proc_id                varchar NOT NULL,
    proc_patient_ref       varchar NULL,
    proc_code_system       varchar NULL,
    proc_code_code         varchar NULL,
    proc_performeddatetime timestamp NULL,
    PRIMARY KEY (proc_id)
);

CREATE TABLE db2dataprocessor_out.v_medication_all
(
    med_id          varchar NOT NULL,
    med_code_system varchar NULL,
    med_code_code   varchar NULL,
    PRIMARY KEY (med_id, med_code_system, med_code_code)
);

CREATE TABLE db2dataprocessor_out.v_medicationadministration_all
(
    medadm_id                       varchar NOT NULL,
    medadm_main_encounter_calculated_ref            varchar NULL,
    medadm_patient_ref              varchar NULL,
    medadm_effectivedatetime        timestamp NULL,
    medadm_medicationreference_ref  varchar NULL,
    PRIMARY KEY (medadm_id)
);

CREATE TABLE db2dataprocessor_out.v_medicationstatement_all
(
    medstat_id                      varchar NOT NULL,
    medstat_main_encounter_calculated_ref           varchar NULL,
    medstat_patient_ref             varchar NULL,
    medstat_medicationreference_ref varchar NULL,
    medstat_effectivedatetime       timestamp NULL,
    PRIMARY KEY (medstat_id)
);

CREATE TABLE db2dataprocessor_out.v_medicationrequest_all
(
    medreq_id                       varchar NOT NULL,
    medreq_main_encounter_calculated_ref            varchar NULL,
    medreq_patient_ref              varchar NULL,
    medreq_medicationreference_ref  varchar NULL,
    medreq_authoredon               timestamp NULL,
    PRIMARY KEY (medreq_id)
);

INSERT INTO db2dataprocessor_out.v_patient_all
VALUES ('HOSP-0001', current_date - '85' year, 'female'),
       ('HOSP-0002', current_date - '65' year, 'female'),
       ('HOSP-0003', current_date - '45' year, 'male'),
       ('HOSP-0004', current_date - '25' year, 'male'),
       ('HOSP-0005', current_date - '5' year, 'female');

INSERT INTO db2dataprocessor_out.v_encounter_all
VALUES ('HOSP-0001-E-1', 'Patient/HOSP-0001', NULL, 'IMP', '2016-01-01', '2016-02-01'),
       ('HOSP-0002-E-1', 'Patient/HOSP-0002', NULL, 'IMP', '2017-01-01', '2017-02-01'),
       ('HOSP-0003-E-1', 'Patient/HOSP-0003', NULL, 'IMP', '2018-01-01', '2018-02-01'),
       ('HOSP-0004-E-1', 'Patient/HOSP-0004', NULL, 'IMP', '2019-01-01', '2019-02-01'),
       ('HOSP-0005-E-1', 'Patient/HOSP-0005', NULL, 'AMB', '2020-01-01', '2020-02-01');

INSERT INTO db2dataprocessor_out.v_observation_all
VALUES ('HOSP-0001-E-1-OL-1', 'Encounter/HOSP-0001-E-1', 'Patient/HOSP-0001', 'http://loinc.org', '1234-5', '2016-01-16T12:43:00', 160, 'mg/dL'),
       ('HOSP-0001-E-1-OL-1', 'Encounter/HOSP-0001-E-1', 'Patient/HOSP-0001', 'http://loinc.org', '2160-0', '2016-01-16T12:43:00', 160, 'mg/dL'),
       ('HOSP-0002-E-1-OL-1', 'Encounter/HOSP-0002-E-1', 'Patient/HOSP-0002', 'http://loinc.org', '1234-5', '2017-01-17T06:35:00', 170, 'mg/dL'),
       ('HOSP-0002-E-1-OL-1', 'Encounter/HOSP-0002-E-1', 'Patient/HOSP-0002', 'http://loinc.org', '678-9', '2017-01-17T06:35:00', 170, 'mg/dL'),
       ('HOSP-0003-E-1-OL-1', 'Encounter/HOSP-0003-E-1', 'Patient/HOSP-0003', 'http://loinc.org', '678-9', '2018-01-18T12:43:00', 180, 'mg/dL'),
       ('HOSP-0003-E-1-OL-1', 'Encounter/HOSP-0003-E-1', 'Patient/HOSP-0003', 'http://loinc.org', '38483-4', '2018-01-18T12:43:00', 180, 'mg/dL'),
       ('HOSP-0004-E-1-OL-1', 'Encounter/HOSP-0004-E-1', 'Patient/HOSP-0004', 'http://loinc.org', '1234-5', '2019-01-19T06:35:00', 190, 'mg/dL'),
       ('HOSP-0004-E-1-OL-1', 'Encounter/HOSP-0004-E-1', 'Patient/HOSP-0004', 'http://loinc.org', '678-9', '2019-01-19T06:35:00', 190, 'mg/dL'),
       ('HOSP-0005-E-1-OL-1', 'Encounter/HOSP-0005-E-1', 'Patient/HOSP-0005', 'http://loinc.org', '2160-0', '2020-01-20T12:43:00', 200, 'mg/dL'),
       ('HOSP-0005-E-1-OL-1', 'Encounter/HOSP-0005-E-1', 'Patient/HOSP-0005', 'http://loinc.org', '1234-5', '2020-01-20T12:43:00', 200, 'mg/dL');
