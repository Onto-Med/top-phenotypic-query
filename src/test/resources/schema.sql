CREATE TABLE subject (
    subject_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    birth_date date   NOT NULL,
    sex        text   NOT NULL,
    PRIMARY KEY (subject_id)
);

CREATE TABLE assessment1 (
    assessment_id bigint                   NOT NULL GENERATED ALWAYS AS IDENTITY,
    subject_id    bigint                   NOT NULL REFERENCES subject,
    created_at    timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    height        numeric,
    weight        numeric,
    PRIMARY KEY (assessment_id)
);