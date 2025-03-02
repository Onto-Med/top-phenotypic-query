CREATE SCHEMA db_log;

CREATE TABLE db_log.patient
(
    pat_id        varchar NOT NULL,
    pat_birthdate date NULL,
    pat_gender    varchar NULL,
    PRIMARY KEY (pat_id)
);

CREATE TABLE db_log.encounter
(
    enc_id                     varchar NOT NULL,
    enc_patient_id             varchar NULL,
    enc_diagnosis_condition_id varchar NOT NULL,
    enc_class_code             varchar NULL,
    enc_period_start           timestamp NULL,
    enc_period_end             timestamp NULL,
    PRIMARY KEY (enc_id, enc_diagnosis_condition_id)
);

CREATE TABLE db_log.observation
(
    obs_id                  varchar NOT NULL,
    obs_encounter_id        varchar NULL,
    obs_patient_id          varchar NULL,
    obs_code_system         varchar NOT NULL,
    obs_code_code           varchar NOT NULL,
    obs_effectivedatetime   timestamp NULL,
    obs_valuequantity_value float8 NULL,
    obs_valuequantity_code  varchar NULL,
    PRIMARY KEY (obs_id, obs_code_system, obs_code_code)
);

CREATE TABLE db_log."condition"
(
    con_id               varchar NOT NULL,
    con_patient_id       varchar NULL,
    con_identifier_value varchar NULL,
    con_code_system      varchar NULL,
    con_code_code        varchar NULL,
    con_code_display     varchar NULL,
    con_code_text        varchar NULL,
    con_recordeddate     timestamp NULL,
    PRIMARY KEY (con_id)
);

CREATE TABLE db_log."procedure"
(
    proc_id                varchar NOT NULL,
    proc_patient_id        varchar NULL,
    proc_code_system       varchar NULL,
    proc_code_code         varchar NULL,
    proc_performeddatetime timestamp NULL,
    PRIMARY KEY (proc_id)
);

CREATE TABLE db_log.medication
(
    med_id          varchar NOT NULL,
    med_code_system varchar NULL,
    med_code_code   varchar NULL,
    PRIMARY KEY (med_id, med_code_system, med_code_code)
);

CREATE TABLE db_log.medicationadministration
(
    medadm_id                     varchar NOT NULL,
    medadm_encounter_id           varchar NULL,
    medadm_patient_id             varchar NULL,
    medadm_effectivedatetime      timestamp NULL,
    medadm_medicationreference_id varchar NULL,
    PRIMARY KEY (medadm_id)
);

CREATE TABLE db_log.medicationstatement
(
    medstat_id                     varchar NOT NULL,
    medstat_encounter_id           varchar NULL,
    medstat_patient_id             varchar NULL,
    medstat_medicationreference_id varchar NULL,
    medstat_effectivedatetime      timestamp NULL,
    PRIMARY KEY (medstat_id)
);

CREATE TABLE db_log.medicationrequest
(
    medreq_id                     varchar NOT NULL,
    medreq_encounter_id           varchar NULL,
    medreq_patient_id             varchar NULL,
    medreq_medicationreference_id varchar NULL,
    medreq_authoredon             timestamp NULL,
    PRIMARY KEY (medreq_id)
);