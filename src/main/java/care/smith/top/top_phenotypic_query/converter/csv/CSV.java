package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.Entities;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSV {

  private Charset charset = StandardCharsets.UTF_8;
  private String entriesDelimiter = ";";
  private String entryPartsDelimiter = ",";

  public CSV() {}

  public CSV(DataAdapterConfig config) {
    if (config != null && config.getCsvSettings() != null) {
      String charset = config.getCsvSettings().getCharset();
      if (charset != null && !charset.isBlank()) charset(charset);

      String entriesDelimiter = config.getCsvSettings().getEntriesDelimiter();
      if (entriesDelimiter != null && !entriesDelimiter.isBlank())
        entriesDelimiter(entriesDelimiter);

      String entryPartsDelimiter = config.getCsvSettings().getEntryPartsDelimiter();
      if (entryPartsDelimiter != null && !entryPartsDelimiter.isBlank())
        entryPartsDelimiter(entryPartsDelimiter);
    }
  }

  public CSV charset(Charset charset) {
    this.charset = charset;
    return this;
  }

  public CSV charset(String charset) {
    return charset(Charset.forName(charset));
  }

  public CSV entriesDelimiter(String entriesDelimiter) {
    this.entriesDelimiter = entriesDelimiter;
    return this;
  }

  public CSV entryPartsDelimiter(String entryPartsDelimiter) {
    this.entryPartsDelimiter = entryPartsDelimiter;
    return this;
  }

  public void writeMetadata(Entity[] phenotypes, OutputStream out) {
    CSVWriter writer = new CSVWriter(out, entriesDelimiter, charset);
    writer.write(CSVMetadataRecord.FIELDS);
    for (Phenotype phe : Entities.of(phenotypes).deriveAdditionalProperties().getPhenotypes())
      writer.write(new CSVMetadataRecord(phe, entryPartsDelimiter));
    writer.flush();
  }

  public String toStringMetadata(Entity[] phenotypes) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writeMetadata(phenotypes, out);
    return out.toString(charset);
  }

  public void writePhenotypes(ResultSet rs, Entity[] phenotypes, OutputStream out) {
    CSVWriter writer = new CSVWriter(out, entriesDelimiter, charset);
    writer.write(PhenotypesCSVDataRecord.FIELDS);
    Entities entities = Entities.of(phenotypes).deriveAdditionalProperties();
    for (SubjectPhenotypes sbjPhes : rs.values()) {
      for (PhenotypeValues pheVals : sbjPhes.values()) {
        for (List<Value> vals : pheVals.values()) {
          for (Value val : vals) {
            Phenotype phe = entities.getPhenotype(pheVals.getPhenotypeName());
            String title =
                (phe == null) ? pheVals.getPhenotypeName() : Entities.getDefaultTitleFull(phe);
            writer.write(
                new PhenotypesCSVDataRecord(
                    sbjPhes.getSubjectId(), pheVals.getPhenotypeName(), title, val));
          }
        }
      }
    }
    writer.flush();
  }

  public String toStringPhenotypes(ResultSet rs, Entity[] phenotypes) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writePhenotypes(rs, phenotypes, out);
    return out.toString(charset);
  }

  public void writeSubjects(
      ResultSet rs, Entity[] entities, PhenotypeQuery query, OutputStream out) {
    SubjectsCSVHeader header =
        new SubjectsCSVHeader(Entities.of(entities).deriveAdditionalProperties(), query);
    CSVWriter writer = new CSVWriter(out, entriesDelimiter, charset);
    writer.write(header.getTitles());
    for (SubjectPhenotypes values : rs.getPhenotypes())
      writer.write(new SubjectsCSVDataRecord(values, header.getHeader(), entryPartsDelimiter));
    writer.flush();
  }

  public String toStringSubjects(ResultSet rs, Entity[] entities, PhenotypeQuery query) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writeSubjects(rs, entities, query, out);
    return out.toString(charset);
  }
}
