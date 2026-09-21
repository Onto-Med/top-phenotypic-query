module care.smith.top.top_phenotypic_query {
  requires java.base;
  requires java.sql;
  requires java.net.http;
  requires info.picocli;

  opens care.smith.top.top_phenotypic_query to
      info.picocli;
  opens care.smith.top.top_phenotypic_query.command to
      info.picocli;
  opens care.smith.top.top_phenotypic_query.adapter.config to
      com.fasterxml.jackson.databind;
  opens care.smith.top.top_phenotypic_query.analysis to
      info.picocli;

  requires org.slf4j;
  requires org.apache.logging.log4j;
  requires org.apache.commons.lang3;
  requires com.google.common;
  requires spring.core;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires hapi.fhir.base;
  requires hapi.fhir.client;
  requires hapi.fhir.structures.r4;
  requires org.hl7.fhir.r4;
  requires org.hl7.fhir.utilities;
  requires ucum;
  requires com.opencsv;
  requires jakarta.validation;
  requires org.postgresql.jdbc;
  requires com.h2database;
  requires top.api;

  exports care.smith.top.top_phenotypic_query;
  exports care.smith.top.top_phenotypic_query.adapter;
  exports care.smith.top.top_phenotypic_query.adapter.config;
  exports care.smith.top.top_phenotypic_query.search;
  exports care.smith.top.top_phenotypic_query.result;
}
