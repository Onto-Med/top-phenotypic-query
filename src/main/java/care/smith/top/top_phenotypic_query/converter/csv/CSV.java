package care.smith.top.top_phenotypic_query.converter.csv;

import care.smith.top.model.Entity;
import care.smith.top.model.Phenotype;
import care.smith.top.model.PhenotypeQuery;
import care.smith.top.model.ProjectionEntry;
import care.smith.top.model.QueryCriterion;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.adapter.config.DataAdapterConfig;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public void write(Entity[] phenotypes, OutputStream out) {
    CSVWriter writer = new CSVWriter(out, entriesDelimiter, charset);
    writer.write(CSVMetadataRecord.FIELDS);
    for (Phenotype phe : Entities.of(phenotypes).deriveAdditionalProperties().getPhenotypes())
      writer.write(new CSVMetadataRecord(phe, entryPartsDelimiter));
    writer.flush();
  }

  public String toString(Entity[] phenotypes) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    write(phenotypes, out);
    return out.toString(charset);
  }

  public void write(ResultSet rs, OutputStream out) {
    CSVWriter writer = new CSVWriter(out, entriesDelimiter, charset);
    writer.write(CSVDataRecord.FIELDS);
    for (SubjectPhenotypes sbjPhes : rs.values()) {
      for (PhenotypeValues pheVals : sbjPhes.values()) {
        for (List<Value> vals : pheVals.values()) {
          for (Value val : vals)
            writer.write(
                new CSVDataRecord(sbjPhes.getSubjectId(), pheVals.getPhenotypeName(), val));
        }
      }
    }
    writer.flush();
  }

  public String toString(ResultSet rs) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    write(rs, out);
    return out.toString(charset);
  }

  public void writeWideTable(
      ResultSet rs, Entity[] entities, PhenotypeQuery query, OutputStream out) {
    LinkedHashMap<String, WideCSVHead> header =
        getHeader(Entities.of(entities).deriveAdditionalProperties(), query);
    CSVWriter writer = new CSVWriter(out, entriesDelimiter, charset);
    writer.write(
        Stream.concat(Stream.of("Id"), header.values().stream().map(h -> h.getTitle()))
            .collect(Collectors.toList()));
    Collection<WideCSVHead> headerCol = header.values();
    for (SubjectPhenotypes values : rs.getPhenotypes())
      writer.write(new WideCSVDataRecord(values, headerCol, entryPartsDelimiter));
    writer.flush();
  }

  public String toStringWideTable(ResultSet rs, Entity[] entities, PhenotypeQuery query) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writeWideTable(rs, entities, query, out);
    return out.toString(charset);
  }

  private LinkedHashMap<String, WideCSVHead> getHeader(Entities phenotypes, PhenotypeQuery query) {

    LinkedHashMap<String, WideCSVHead> header = new LinkedHashMap<>();

    if (query.getProjection() != null) {
      for (ProjectionEntry pro : query.getProjection())
        putHeader(header, pro.getSubjectId(), phenotypes);
      for (ProjectionEntry pro : query.getProjection())
        putHeaderComposite(header, pro.getSubjectId(), phenotypes);
    }

    if (query.getCriteria() != null) {
      for (QueryCriterion cri : query.getCriteria())
        putHeader(header, cri.getSubjectId(), phenotypes);
      for (QueryCriterion cri : query.getCriteria())
        putHeaderComposite(header, cri.getSubjectId(), phenotypes);
    }

    return header;
  }

  private void putHeader(LinkedHashMap<String, WideCSVHead> header, String pheId, Phenotype phe) {
    header.put(
        pheId,
        new WideCSVHead(pheId, Entities.getTitleInLangOrFirst(phe, "en"), phe.getDataType()));

    if (Phenotypes.isSingleRestriction(phe))
      putHeader(header, phe.getSuperPhenotype().getId(), phe.getSuperPhenotype());
  }

  private void putHeader(
      LinkedHashMap<String, WideCSVHead> header, String pheId, Entities phenotypes) {
    putHeader(header, pheId, phenotypes.getPhenotype(pheId));
  }

  private void putHeaderComposite(
      LinkedHashMap<String, WideCSVHead> header, String pheId, Entities phenotypes) {
    Phenotype phe = phenotypes.getPhenotype(pheId);
    if (phe.getExpression() != null)
      for (String var : Expressions.getVariables(phe.getExpression(), phenotypes))
        putHeader(header, var, phenotypes.getPhenotype(var));
  }
}
