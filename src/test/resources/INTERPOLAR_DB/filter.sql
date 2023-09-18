INSERT INTO patient VALUES
    (1, '2001-01-01', 'male');

INSERT INTO encounter VALUES
    (0, 1, '2017-01-01', '2017-02-01', 'IMP'),
    (1, 1, '2017-01-01', '2017-02-01', 'IMP'),
    (2, 1, '2017-01-01', '2017-02-01', 'IMP'),
    (3, 1, '2017-01-01', '2017-02-01', 'IMP'),
    (4, 1, '2017-01-01', '2017-02-01', 'IMP'),
    (5, 1, '2017-01-01', '2017-02-01', 'IMP');

INSERT INTO medication VALUES (1);

INSERT INTO medication_coding (medication_id, system, code) VALUES 
	(1, 'http://fhir.de/CodeSystem/bfarm/atc', 'B01AE07');
	
INSERT INTO medication_administration (medication_administration_id, encounter_id, patient_id, medication_id, start_date_time, end_date_time) VALUES
    (1, 1, 1, 1, '2020-01-01', '2021-01-01'),
    (2, 2, 1, 1, '2020-01-02', '2021-01-01'),
    (3, 3, 1, 1, null, null),
    (5, 5, 1, 1, '2020-01-02', '2021-01-01');	
    
INSERT INTO observation (observation_id, encounter_id, patient_id, date_time, number_value) VALUES
    (1, 1, 1, '2020-01-01', 10),
    (2, 2, 1, '2020-01-01', 31),
    (3, 3, 1, '2020-01-01', 10),
    (4, 4, 1, '2020-01-01', 10),
    (5, 5, 1, '2020-01-01', 10);

INSERT INTO observation_coding (observation_id, system, code) VALUES
    (1, 'http://loinc.org', '2160-0'),
    (2, 'http://loinc.org', '2160-0'),
    (3, 'http://loinc.org', '2160-0'),
    (4, 'http://loinc.org', '2160-0'),
    (5, 'http://loinc.org', '2160-0');