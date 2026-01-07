package care.smith.top.top_phenotypic_query.tests.intern.run;

import static org.junit.jupiter.api.Assertions.assertEquals;

import care.smith.top.model.ItemType;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.ForEach;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.Duration;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Client;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Enc;
import care.smith.top.top_phenotypic_query.tests.intern.fhir.Pat;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Phe;
import care.smith.top.top_phenotypic_query.util.builder.Que;
import care.smith.top.top_phenotypic_query.util.builder.Res;
import java.util.Set;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Disabled
public class DurationTestIntern {

  private static final Logger LOGGER = LoggerFactory.getLogger(DurationTestIntern.class);

  private static Client client = new Client();

  private static Phenotype enc = new Phe("encounter").itemType(ItemType.ENCOUNTER).string().get();
  private static Phenotype imp = new Phe("inpatient").restriction(enc, Res.of("IMP")).get();
  private static Phenotype forEach =
      new Phe("forEach")
          .expression(ForEach.of(Exp.of(imp), Ge.of(Duration.of(imp), Exp.of(3))))
          .get();
  private static Phenotype check = new Phe("check").expression(Or.of(forEach)).get();

  @Test
  void test() throws InstantiationException {
    client.clean();

    Reference pat1 = client.add(new Pat("p1").birthDate("2001-01-01").gender("male"));
    Reference pat2 = client.add(new Pat("p2").birthDate("2001-01-01").gender("female"));
    Reference pat3 = client.add(new Pat("p3").birthDate("1951-01-01").gender("male"));
    Reference pat4 = client.add(new Pat("p4").birthDate("1951-01-01").gender("female"));
    Reference pat5 = client.add(new Pat("p5").birthDate("2001-01-01").gender("male"));
    Reference pat6 = client.add(new Pat("p6").birthDate("1951-01-01").gender("female"));

    client.add(
        new Enc("e1a", pat1)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .period("2000-01-01", "2000-01-03"));
    client.add(
        new Enc("e1b", pat1)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .period("2001-01-01", "2001-01-03"));

    client.add(
        new Enc("e2a", pat2)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB")
            .period("2000-01-01", "2000-01-04"));
    client.add(
        new Enc("e2b", pat2)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .period("2000-01-01", "2000-01-03"));

    client.add(
        new Enc("e3a", pat3)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .period("2000-01-01", "2000-01-04"));
    client.add(
        new Enc("e3b", pat3)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .period("2001-01-01", "2001-01-03"));

    client.add(
        new Enc("e4a", pat4)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB")
            .period("2000-01-01", "2000-01-04"));
    client.add(
        new Enc("e4b", pat4)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB")
            .period("2001-01-01", "2001-01-04"));

    client.add(
        new Enc("e5a", pat5)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .period("2000-01-01", "2000-01-03"));
    client.add(new Enc("e5b", pat5).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));

    client.add(
        new Enc("e6a", pat6)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "AMB")
            .period("2000-01-01", "2000-01-04"));
    client.add(
        new Enc("e6b", pat6)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .period("2000-01-01", "2000-01-04"));
    client.add(
        new Enc("e6c", pat6)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .start("2020-01-01"));
    client.add(new Enc("e6c", pat6).cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP"));
    client.add(
        new Enc("e6d", pat6)
            .cls("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP")
            .period("2000-01-01", "2000-01-03"));

    ResultSet rs =
        new Que("config/Default_FHIR_Adapter_Test.yml", enc, imp, forEach, check)
            .inc(check)
            .execute();

    LOGGER.trace(rs.toString());

    assertEquals(Set.of(pat3.getReference(), pat6.getReference()), rs.getSubjectIds());
  }
}
