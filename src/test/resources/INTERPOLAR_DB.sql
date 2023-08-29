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
    number_value    numeric,
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

INSERT INTO patient VALUES
    (1, '1940-01-01', 'female'),
    (2, '1960-01-01', 'female'),
    (3, '1980-01-01', 'male'),
    (4, '2000-01-01', 'male'),
    (5, '2020-01-01', 'female');

INSERT INTO encounter VALUES
    (11, 1, '2016-01-01', '2016-02-01', 'IMP'),
    (22, 2, '2017-01-01', '2017-02-01', 'IMP'),
    (33, 3, '2018-01-01', '2018-02-01', 'IMP'),
    (44, 4, '2019-01-01', '2019-02-01', 'IMP'),
    (55, 5, '2020-01-01', '2020-02-01', 'AMB');

INSERT INTO observation (observation_id, encounter_id, patient_id, date_time, number_value) VALUES
    (1, 11, 1, '2016-01-16T12:43:00', 160),
    (2, 22, 2, '2017-01-17T06:35:00', 170),
    (3, 33, 3, '2018-01-18T12:43:00', 180),
    (4, 44, 4, '2019-01-19T06:35:00', 190),
    (5, 55, 5, '2020-01-20T12:43:00', 200);

INSERT INTO observation_coding (observation_id, system, code) VALUES
    (1, 'http://loinc.org', '1234-5'),
    (1, 'http://loinc.org', '2160-0'),
    (2, 'http://loinc.org', '1234-5'),
    (2, 'http://loinc.org', '678-9'),
    (3, 'http://loinc.org', '678-9'),
    (3, 'http://loinc.org', '38483-4'),
    (4, 'http://loinc.org', '1234-5'),
    (4, 'http://loinc.org', '678-9'),
    (5, 'http://loinc.org', '2160-0'),
    (5, 'http://loinc.org', '1234-5');

