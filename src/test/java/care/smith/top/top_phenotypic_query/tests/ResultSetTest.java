package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.backend.model.DateTimeRestriction;
import care.smith.top.backend.model.RestrictionOperator;
import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.top_phenotypic_query.result.Phenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.Values;

public class ResultSetTest {

  @Test
  public void testValues() {
    Values weightVals1 = new Values("Weight");
    weightVals1.setDecimalValues(getDTR(2001), new DecimalValue(75));
    Values heightVals1 = new Values("Height");
    heightVals1.setDecimalValues(getDTR(2001), new DecimalValue(1.7));
    Values ageVals1 = new Values("Age");
    ageVals1.setDecimalValues(getDTR(2001), new DecimalValue(20));

    Values weightVals2 = new Values("Weight");
    weightVals2.setDecimalValues(getDTR(2001), new DecimalValue(80));
    Values heightVals2 = new Values("Height");
    heightVals2.setDecimalValues(getDTR(2001), new DecimalValue(1.6));
    Values ageVals2 = new Values("Age");
    ageVals2.setDecimalValues(getDTR(2001), new DecimalValue(40));

    Phenotypes phes1 = new Phenotypes("Subject1");
    phes1.setValues(weightVals1, heightVals1, ageVals1);

    Phenotypes phes2 = new Phenotypes("Subject2");
    phes2.setValues(weightVals2, heightVals2, ageVals2);

    ResultSet rs = new ResultSet();
    rs.setPhenotypes(phes1, phes2);

    System.out.println(rs);

    assertEquals(true, true);
  }

  @Test
  public void testSubtract() {
    ResultSet rs1 = new ResultSet();
    rs1.setPhenotypes(getSubject1(), getSubject2(), getSubject3());

    ResultSet rs2 = new ResultSet();
    rs2.setPhenotypes(getSubject2(), getSubject3b(), getSubject4());

    ResultSet difference = rs1.subtract(rs2);

    assertEquals(Set.of("S1"), difference.getSubjectIds());
  }

  @Test
  public void testIntersect() {
    ResultSet rs1 = new ResultSet();
    rs1.setPhenotypes(getSubject1(), getSubject2(), getSubject3());

    ResultSet rs2 = new ResultSet();
    rs2.setPhenotypes(getSubject2(), getSubject3b(), getSubject4());

    ResultSet intersection = rs1.intersect(rs2);

    assertEquals(Set.of("S2", "S3"), intersection.getSubjectIds());

    Phenotypes phesS2 = intersection.getPhenotypes("S2");
    Phenotypes phesS3 = intersection.getPhenotypes("S3");

    assertEquals(Set.of("P1", "P2"), phesS2.getPhenotypeNames());
    assertEquals(Set.of("P2", "P3", "P4"), phesS3.getPhenotypeNames());

    Values p3 = phesS3.getValues("P3");
    assertEquals(
        Set.of(getDTR(2004), getDTR(2005), getDTR(2008), getDTR(2009)),
        p3.getDateTimeRestrictions());
  }

  @Test
  public void testInsert() {
    ResultSet rs1 = new ResultSet();
    rs1.setPhenotypes(getSubject1(), getSubject2(), getSubject3());

    ResultSet rs2 = new ResultSet();
    rs2.setPhenotypes(getSubject2(), getSubject3b(), getSubject4());

    ResultSet insert = rs1.insert(rs2);

    assertEquals(Set.of("S1", "S2", "S3"), insert.getSubjectIds());

    Phenotypes phesS1 = insert.getPhenotypes("S1");
    Phenotypes phesS2 = insert.getPhenotypes("S2");
    Phenotypes phesS3 = insert.getPhenotypes("S3");

    assertEquals(Set.of("P1"), phesS1.getPhenotypeNames());
    assertEquals(Set.of("P1", "P2"), phesS2.getPhenotypeNames());
    assertEquals(Set.of("P2", "P3", "P4"), phesS3.getPhenotypeNames());

    Values p3 = phesS3.getValues("P3");
    assertEquals(
        Set.of(getDTR(2004), getDTR(2005), getDTR(2008), getDTR(2009)),
        p3.getDateTimeRestrictions());
  }

  private static Phenotypes getSubject1() {
    Phenotypes sp = new Phenotypes("S1");
    sp.setValues(getPhenotype1());
    return sp;
  }

  private static Phenotypes getSubject2() {
    Phenotypes sp = new Phenotypes("S2");
    sp.setValues(getPhenotype1(), getPhenotype2());
    return sp;
  }

  private static Phenotypes getSubject3() {
    Phenotypes sp = new Phenotypes("S3");
    sp.setValues(getPhenotype2(), getPhenotype3());
    return sp;
  }

  private static Phenotypes getSubject3b() {
    Phenotypes sp = new Phenotypes("S3");
    sp.setValues(getPhenotype3b(), getPhenotype4());
    return sp;
  }

  private static Phenotypes getSubject4() {
    Phenotypes sp = new Phenotypes("S4");
    sp.setValues(getPhenotype1());
    return sp;
  }

  private static Values getPhenotype1() {
    Values pv = new Values("P1");
    pv.setDecimalValues(
        getDTR(2000), new DecimalValue(1), new DecimalValue(2), new DecimalValue(3));
    pv.setDecimalValues(
        getDTR(2001), new DecimalValue(4), new DecimalValue(5), new DecimalValue(6));
    return pv;
  }

  private static Values getPhenotype2() {
    Values pv = new Values("P2");
    pv.setDecimalValues(
        getDTR(2002), new DecimalValue(7), new DecimalValue(8), new DecimalValue(9));
    pv.setDecimalValues(
        getDTR(2003), new DecimalValue(10), new DecimalValue(11), new DecimalValue(12));
    return pv;
  }

  private static Values getPhenotype3() {
    Values pv = new Values("P3");
    pv.setDecimalValues(
        getDTR(2004), new DecimalValue(13), new DecimalValue(14), new DecimalValue(15));
    pv.setDecimalValues(
        getDTR(2005), new DecimalValue(16), new DecimalValue(17), new DecimalValue(18));
    return pv;
  }

  private static Values getPhenotype3b() {
    Values pv = new Values("P3");
    pv.setDecimalValues(
        getDTR(2004), new DecimalValue(13), new DecimalValue(14), new DecimalValue(15));
    pv.setDecimalValues(
        getDTR(2008), new DecimalValue(25), new DecimalValue(26), new DecimalValue(27));
    pv.setDecimalValues(
        getDTR(2009), new DecimalValue(28), new DecimalValue(29), new DecimalValue(30));
    return pv;
  }

  private static Values getPhenotype4() {
    Values pv = new Values("P4");
    pv.setDecimalValues(
        getDTR(2006), new DecimalValue(19), new DecimalValue(20), new DecimalValue(21));
    pv.setDecimalValues(
        getDTR(2007), new DecimalValue(22), new DecimalValue(23), new DecimalValue(24));
    return pv;
  }

  private static DateTimeRestriction getDTR(int year) {
    return new DateTimeRestriction()
        .minOperator(RestrictionOperator.GREATER_THAN_OR_EQUAL_TO)
        .maxOperator(RestrictionOperator.LESS_THAN)
        .values(
            List.of(
                OffsetDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(year + 1, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(2))));
  }
}
