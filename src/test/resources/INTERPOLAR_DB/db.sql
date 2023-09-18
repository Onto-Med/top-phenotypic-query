CREATE TABLE patient (
    patient_id bigint    NOT NULL,
    birth_date timestamp,
    sex        text,
    PRIMARY KEY (patient_id)
);

CREATE TABLE encounter (
    encounter_id    bigint   NOT NULL,
    patient_id      bigint   REFERENCES patient,
    start_date_time timestamp,
    end_date_time   timestamp,
    class           text,
    PRIMARY KEY (encounter_id)
);

CREATE TABLE observation (
    observation_id  bigint    NOT NULL,
    encounter_id    bigint    REFERENCES encounter,
    patient_id      bigint    REFERENCES patient,
    date_time       timestamp,
    start_date_time timestamp,
    end_date_time   timestamp,
    unit            text,
    number_value    numeric(20, 2),
    text_value      text,
    date_time_value timestamp,
    boolean_value   boolean,
    PRIMARY KEY (observation_id)
);

CREATE TABLE observation_coding (
    observation_id bigint NOT NULL REFERENCES observation,
    system         text   NOT NULL,
    version        text,
    code           text   NOT NULL,
    display        text
);

CREATE TABLE condition (
    condition_id    bigint    NOT NULL,
    encounter_id    bigint    REFERENCES encounter,
    patient_id      bigint    REFERENCES patient,
    date_time       timestamp,
    start_date_time timestamp,
    end_date_time   timestamp,
    PRIMARY KEY (condition_id)
);

CREATE TABLE condition_coding (
    condition_id bigint NOT NULL REFERENCES condition,
    system       text   NOT NULL,
    version      text,
    code         text   NOT NULL,
    display      text
);

CREATE TABLE procedure (
    procedure_id    bigint    NOT NULL,
    encounter_id    bigint    REFERENCES encounter,
    patient_id      bigint    REFERENCES patient,
    date_time       timestamp,
    start_date_time timestamp,
    end_date_time   timestamp,
    PRIMARY KEY (procedure_id)
);

CREATE TABLE procedure_coding (
    procedure_id bigint NOT NULL REFERENCES procedure,
    system       text   NOT NULL,
    version      text,
    code         text   NOT NULL,
    display      text
);

CREATE TABLE medication (
    medication_id bigint NOT NULL,
    PRIMARY KEY (medication_id)
);

CREATE TABLE medication_coding (
    medication_id bigint NOT NULL REFERENCES medication,
    system        text   NOT NULL,
    version       text,
    code          text   NOT NULL,
    display       text
);

CREATE TABLE medication_administration (
    medication_administration_id bigint    NOT NULL,
    encounter_id                 bigint    REFERENCES encounter,
    patient_id                   bigint    REFERENCES patient,
    medication_id                bigint    NOT NULL REFERENCES medication,
    date_time                    timestamp,
    start_date_time              timestamp,
    end_date_time                timestamp,
    PRIMARY KEY (medication_administration_id)
);

CREATE TABLE medication_statement (
    medication_statement_id bigint    NOT NULL,
    encounter_id            bigint    REFERENCES encounter,
    patient_id              bigint    REFERENCES patient,
    medication_id           bigint    NOT NULL REFERENCES medication,
    date_time               timestamp,
    start_date_time         timestamp,
    end_date_time           timestamp,
    PRIMARY KEY (medication_statement_id)
);

CREATE TABLE medication_request (
    medication_request_id bigint    NOT NULL,
    encounter_id          bigint    REFERENCES encounter,
    patient_id            bigint    REFERENCES patient,
    medication_id         bigint    NOT NULL REFERENCES medication,
    date_time             timestamp,
    start_date_time       timestamp,
    end_date_time         timestamp,
    PRIMARY KEY (medication_request_id)
);