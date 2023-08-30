INSERT INTO patient VALUES
    (1, '1960-01-01', 'female'),
    (2, '1960-01-01', 'female'),
    (3, '1980-01-01', 'male'),
    (4, '2000-01-01', 'male'),
    (5, '2020-01-01', 'female'),
    (6, '1960-01-01', 'female'),
    (7, '1980-01-01', 'male'),
    (8, '2000-01-01', 'male'),
    (9, '2020-01-01', 'female');

INSERT INTO encounter VALUES
    (11, 1, '2017-01-01', '2017-02-01', 'IMP'),
    (22, 2, '2017-01-01', '2017-02-01', 'IMP'),
    (33, 3, '2018-01-01', '2018-02-01', 'IMP'),
    (44, 4, '2019-01-01', '2019-02-01', 'IMP'),
    (55, 5, '2020-01-01', '2020-02-01', 'AMB'),    
    (66, 6, '2017-01-01', '2017-02-01', 'IMP'),
    (77, 7, '2018-01-01', '2018-02-01', 'IMP'),
    (88, 8, '2019-01-01', '2019-02-01', 'IMP'),
    (99, 9, '2020-01-01', '2020-02-01', 'AMB');

INSERT INTO medication VALUES (1);

INSERT INTO medication_coding (medication_id, system, code) VALUES 
	(1, 'http://fhir.de/CodeSystem/bfarm/atc', 'B01AA'),
	(1, 'http://fhir.de/CodeSystem/bfarm/atc', 'B01AF');
	
INSERT INTO medication_administration (medication_administration_id, encounter_id, patient_id, medication_id, start_date_time, end_date_time) VALUES
    (331, 33, 3, 1, '2020-01-01', '2020-01-02');	

INSERT INTO procedure (procedure_id, encounter_id, patient_id, date_time) VALUES
    (441, 44, 4, current_date - 8),	
    (442, 44, 4, current_date - 6),	
    (881, 88, 8, current_date - 6);	
    
INSERT INTO procedure_coding (procedure_id, system, code) VALUES
    (441, 'http://fhir.de/CodeSystem/bfarm/ops', '8-853'),
    (442, 'http://fhir.de/CodeSystem/bfarm/ops', '8-853'),
    (881, 'http://fhir.de/CodeSystem/bfarm/ops', '8-853');

INSERT INTO observation (observation_id, encounter_id, patient_id, date_time, number_value) VALUES
    (111, 11, 1, '2020-01-01', 1.1),
    (112, 11, 1, '2020-01-01', 1.2),
    (113, 11, 1, '2020-01-01', 1.3),
    
    (221, 22, 2, '2020-01-01', 2.1),
    (222, 22, 2, '2020-01-01', 2.2),
    (223, 22, 2, '2020-01-01', 2.3),

    (331, 33, 3, '2020-01-01', 3.1),
    (332, 33, 3, '2020-01-01', 3.2),
    (333, 33, 3, '2020-01-01', 3.3),
    
    (441, 44, 4, '2020-01-01', 0.1),
    (442, 44, 4, '2020-01-01', 0.2),
    (443, 44, 4, '2020-01-01', 0.3),
    
    (551, 55, 5, '2020-01-01', 2.1),
    (552, 55, 5, '2020-01-01', 2.2),
    
    (661, 66, 6, '2020-01-01', 2.1),
    (662, 66, 6, '2020-01-01', 2.3),

    (771, 77, 7, '2020-01-01', 2.2),
    (772, 77, 7, '2020-01-01', 2.3),

    (881, 88, 8, '2020-01-01', 3.2),
    (882, 88, 8, '2020-01-01', 3.3),
    
    (991, 99, 9, '2020-01-01', 0.1),
    (992, 99, 9, '2019-01-02', 2.1),
    (993, 99, 9, '2020-01-01', 0.2),
    (994, 99, 9, '2020-01-01', 0.3);

INSERT INTO observation_coding (observation_id, system, code) VALUES
    (111, 'http://loinc.org', '2160-0'),
    (112, 'http://loinc.org', '42719-5'),
    (113, 'http://loinc.org', '6301-6'),

    (221, 'http://loinc.org', '2160-0'),
    (222, 'http://loinc.org', '42719-5'),
    (223, 'http://loinc.org', '6301-6'),
    
    (331, 'http://loinc.org', '2160-0'),
    (332, 'http://loinc.org', '42719-5'),
    (333, 'http://loinc.org', '6301-6'),
    
    (441, 'http://loinc.org', '2160-0'),
    (442, 'http://loinc.org', '42719-5'),
    (443, 'http://loinc.org', '6301-6'),
    
    (551, 'http://loinc.org', '2160-0'),
    (552, 'http://loinc.org', '42719-5'),

    (661, 'http://loinc.org', '2160-0'),
    (662, 'http://loinc.org', '6301-6'),
    
    (771, 'http://loinc.org', '42719-5'),
    (772, 'http://loinc.org', '6301-6'),

    (881, 'http://loinc.org', '42719-5'),
    (882, 'http://loinc.org', '6301-6'),

    (991, 'http://loinc.org', '2160-0'),
    (992, 'http://loinc.org', '2160-0'),
    (993, 'http://loinc.org', '42719-5'),
    (994, 'http://loinc.org', '6301-6');