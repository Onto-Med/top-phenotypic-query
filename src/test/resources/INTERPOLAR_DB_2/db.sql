CREATE SCHEMA db2dataprocessor_out;

CREATE TABLE db2dataprocessor_out.v_patient_last_version
(
    pat_id        varchar NOT NULL,
    pat_birthdate varchar NULL,
    pat_gender    varchar NULL,
    PRIMARY KEY (pat_id)
);

CREATE TABLE db2dataprocessor_out.v_encounter_last_version
(
    enc_id           	   varchar NOT NULL,
    enc_patient_ref  	   varchar NULL,
    enc_class_code   	   varchar NULL,
    enc_period_start 	   timestamp NULL,
    enc_period_end   	   timestamp NULL,
    enc_age_at_admission   numeric NULL,
    PRIMARY KEY (enc_id)
);

CREATE TABLE db2dataprocessor_out.v_observation_last_version
(
    obs_id                       varchar NOT NULL,
    obs_encounter_calculated_ref varchar NULL,
    obs_patient_ref              varchar NULL,
    analysis_loinc_code          varchar NOT NULL,
    obs_effectivedatetime        timestamp NULL,
    analysis_value      		 float8 NULL,
    PRIMARY KEY (obs_id, analysis_loinc_code)
);

CREATE TABLE db2dataprocessor_out.v_condition_last_version
(
    con_id                       varchar NOT NULL,
    con_encounter_calculated_ref varchar NULL,
    con_patient_ref              varchar NULL,
    con_identifier_value         varchar NULL,
    con_code_system              varchar NULL,
    con_code_code                varchar NULL,
    con_code_display             varchar NULL,
    con_code_text                varchar NULL,
    con_onsetperiod_start        timestamp NULL,
    con_onsetperiod_end          timestamp NULL,
    con_onsetdatetime            timestamp NULL,
    con_recordeddate             timestamp NULL,
    PRIMARY KEY (con_id)
);

CREATE TABLE db2dataprocessor_out.v_procedure_last_version
(
    proc_id                       varchar NOT NULL,
    proc_encounter_calculated_ref varchar NULL,
    proc_patient_ref              varchar NULL,
    proc_code_system              varchar NULL,
    proc_code_code                varchar NULL,
    proc_performeddatetime        timestamp NULL,
    proc_performedperiod_start    timestamp NULL,
    proc_performedperiod_end      timestamp NULL,
    PRIMARY KEY (proc_id)
);

CREATE TABLE db2dataprocessor_out.v_medicationadministration_last_version
(
    medadm_id                       varchar NOT NULL,
    medadm_encounter_calculated_ref varchar NULL,
    medadm_patient_ref              varchar NULL,
    medadm_effectivedatetime        timestamp NULL,
    medadm_effectiveperiod_start    timestamp NULL,
    medadm_effectiveperiod_end      timestamp NULL,
    medadm_medication_system        varchar NULL,
    medadm_medication_code          varchar NULL,
    PRIMARY KEY (medadm_id)
);

CREATE TABLE db2dataprocessor_out.v_medicationstatement_last_version
(
    medstat_id                       varchar NOT NULL,
    medstat_encounter_calculated_ref varchar NULL,
    medstat_patient_ref              varchar NULL,
    medstat_effectivedatetime        timestamp NULL,
    medstat_effectiveperiod_start    timestamp NULL,
    medstat_effectiveperiod_end      timestamp NULL,
    medstat_medication_system        varchar NULL,
    medstat_medication_code          varchar NULL,
    PRIMARY KEY (medstat_id)
);

CREATE TABLE db2dataprocessor_out.v_medicationrequest_last_version
(
    medreq_id                       varchar NOT NULL,
    medreq_encounter_calculated_ref varchar NULL,
    medreq_patient_ref              varchar NULL,
    medreq_authoredon               timestamp NULL,
    medreq_medication_system        varchar NULL,
    medreq_medication_code          varchar NULL,
    PRIMARY KEY (medreq_id)
);