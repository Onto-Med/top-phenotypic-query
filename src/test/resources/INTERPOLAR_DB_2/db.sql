CREATE SCHEMA db2dataprocessor_out;

CREATE TABLE db2dataprocessor_out.v_patient
(
    pat_id        varchar NOT NULL,
    pat_birthdate date NULL,
    pat_gender    varchar NULL,
    PRIMARY KEY (pat_id)
);

CREATE TABLE db2dataprocessor_out.v_encounter
(
    enc_id                      varchar NOT NULL,
    enc_patient_ref             varchar NULL,
    enc_class_code              varchar NULL,
    enc_period_start            timestamp NULL,
    enc_period_end              timestamp NULL,
    PRIMARY KEY (enc_id)
);

CREATE TABLE db2dataprocessor_out.v_observation
(
    obs_id                       varchar NOT NULL,
    obs_encounter_calculated_ref varchar NULL,
    obs_patient_ref              varchar NULL,
    obs_code_system              varchar NOT NULL,
    obs_code_code                varchar NOT NULL,
    obs_effectivedatetime        timestamp NULL,
    obs_valuequantity_value      float8 NULL,
    obs_valuequantity_code       varchar NULL,
    PRIMARY KEY (obs_id, obs_code_system, obs_code_code)
);

CREATE TABLE db2dataprocessor_out."v_condition"
(
    con_id                       varchar NOT NULL,
    con_encounter_calculated_ref varchar NULL,
    con_patient_ref              varchar NULL,
    con_identifier_value         varchar NULL,
    con_code_system              varchar NULL,
    con_code_code                varchar NULL,
    con_code_display             varchar NULL,
    con_code_text                varchar NULL,
    con_recordeddate             timestamp NULL,
    PRIMARY KEY (con_id)
);

CREATE TABLE db2dataprocessor_out.v_procedure
(
    proc_id                       varchar NOT NULL,
    proc_encounter_calculated_ref varchar NULL,
    proc_patient_ref              varchar NULL,
    proc_code_system              varchar NULL,
    proc_code_code                varchar NULL,
    proc_performeddatetime        timestamp NULL,
    PRIMARY KEY (proc_id)
);

CREATE TABLE db2dataprocessor_out.v_medication
(
    med_id          varchar NOT NULL,
    med_code_system varchar NULL,
    med_code_code   varchar NULL,
    PRIMARY KEY (med_id, med_code_system, med_code_code)
);

CREATE TABLE db2dataprocessor_out.v_medicationadministration
(
    medadm_id                       varchar NOT NULL,
    medadm_encounter_calculated_ref varchar NULL,
    medadm_patient_ref              varchar NULL,
    medadm_effectivedatetime        timestamp NULL,
    medadm_medicationreference_ref  varchar NULL,
    PRIMARY KEY (medadm_id)
);

CREATE TABLE db2dataprocessor_out.v_medicationstatement
(
    medstat_id                       varchar NOT NULL,
    medstat_encounter_calculated_ref varchar NULL,
    medstat_patient_ref              varchar NULL,
    medstat_medicationreference_ref  varchar NULL,
    medstat_effectivedatetime        timestamp NULL,
    PRIMARY KEY (medstat_id)
);

CREATE TABLE db2dataprocessor_out.v_medicationrequest
(
    medreq_id                       varchar NOT NULL,
    medreq_encounter_calculated_ref varchar NULL,
    medreq_patient_ref              varchar NULL,
    medreq_medicationreference_ref  varchar NULL,
    medreq_authoredon               timestamp NULL,
    PRIMARY KEY (medreq_id)
);