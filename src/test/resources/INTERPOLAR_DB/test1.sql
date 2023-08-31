INSERT INTO patient VALUES
    (1, '1940-01-01', 'female');

INSERT INTO encounter VALUES
    (11, 1, '2016-01-01', '2016-02-01', 'IMP'),
    (22, 1, '2017-01-01', '2017-02-01', 'IMP'),
    (33, 1, '2018-01-01', '2018-02-01', 'IMP'),
    (44, 1, '2019-01-01', '2019-02-01', 'IMP'),
    (55, 1, '2020-01-01', '2020-02-01', 'AMB');

INSERT INTO observation (observation_id, encounter_id, patient_id, date_time, number_value) VALUES
    (1, 11, 1, '2016-01-16T12:43:00', 160),
    (2, 22, 1, '2017-01-17T06:35:00', 170),
    (3, 33, 1, '2018-01-18T12:43:00', 180),
    (4, 44, 1, '2019-01-19T06:35:00', 190),
    (5, 55, 1, '2020-01-20T12:43:00', 200);

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