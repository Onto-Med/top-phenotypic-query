package care.smith.top.top_phenotypic_query.tests;

import care.smith.top.simple_onto_api.model.property.data.value.DecimalValue;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResultSetTest extends AbstractTest {

  @Test
  public void testValues() {
    PhenotypeValues weightVals1 = new PhenotypeValues("Weight");
    weightVals1.setDecimalValues(getDTR(2001), new DecimalValue(75));
    PhenotypeValues heightVals1 = new PhenotypeValues("Height");
    heightVals1.setDecimalValues(getDTR(2001), new DecimalValue(1.7));
    PhenotypeValues ageVals1 = new PhenotypeValues("Age");
    ageVals1.setDecimalValues(getDTR(2001), new DecimalValue(20));

    PhenotypeValues weightVals2 = new PhenotypeValues("Weight");
    weightVals2.setDecimalValues(getDTR(2001), new DecimalValue(80));
    PhenotypeValues heightVals2 = new PhenotypeValues("Height");
    heightVals2.setDecimalValues(getDTR(2001), new DecimalValue(1.6));
    PhenotypeValues ageVals2 = new PhenotypeValues("Age");
    ageVals2.setDecimalValues(getDTR(2001), new DecimalValue(40));

    SubjectPhenotypes phes1 = new SubjectPhenotypes("Subject1");
    phes1.setValues(weightVals1, heightVals1, ageVals1);

    SubjectPhenotypes phes2 = new SubjectPhenotypes("Subject2");
    phes2.setValues(weightVals2, heightVals2, ageVals2);

    ResultSet rs = new ResultSet();
    rs.setPhenotypes(phes1, phes2);
    assertEquals(2, rs.size());

    System.out.println(rs);
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

    SubjectPhenotypes phesS2 = intersection.getPhenotypes("S2");
    SubjectPhenotypes phesS3 = intersection.getPhenotypes("S3");

    assertEquals(Set.of("P1", "P2"), phesS2.getPhenotypeNames());
    assertEquals(Set.of("P2", "P3", "P4"), phesS3.getPhenotypeNames());

    PhenotypeValues p3 = phesS3.getValues("P3");
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

    SubjectPhenotypes phesS1 = insert.getPhenotypes("S1");
    SubjectPhenotypes phesS2 = insert.getPhenotypes("S2");
    SubjectPhenotypes phesS3 = insert.getPhenotypes("S3");

    assertEquals(Set.of("P1"), phesS1.getPhenotypeNames());
    assertEquals(Set.of("P1", "P2"), phesS2.getPhenotypeNames());
    assertEquals(Set.of("P2", "P3", "P4"), phesS3.getPhenotypeNames());

    PhenotypeValues p3 = phesS3.getValues("P3");
    assertEquals(
        Set.of(getDTR(2004), getDTR(2005), getDTR(2008), getDTR(2009)),
        p3.getDateTimeRestrictions());
  }

  private static SubjectPhenotypes getSubject1() {
    SubjectPhenotypes sp = new SubjectPhenotypes("S1");
    sp.setValues(getPhenotype1());
    return sp;
  }

  private static SubjectPhenotypes getSubject2() {
    SubjectPhenotypes sp = new SubjectPhenotypes("S2");
    sp.setValues(getPhenotype1(), getPhenotype2());
    return sp;
  }

  private static SubjectPhenotypes getSubject3() {
    SubjectPhenotypes sp = new SubjectPhenotypes("S3");
    sp.setValues(getPhenotype2(), getPhenotype3());
    return sp;
  }

  private static SubjectPhenotypes getSubject3b() {
    SubjectPhenotypes sp = new SubjectPhenotypes("S3");
    sp.setValues(getPhenotype3b(), getPhenotype4());
    return sp;
  }

  private static SubjectPhenotypes getSubject4() {
    SubjectPhenotypes sp = new SubjectPhenotypes("S4");
    sp.setValues(getPhenotype1());
    return sp;
  }

  private static PhenotypeValues getPhenotype1() {
    PhenotypeValues pv = new PhenotypeValues("P1");
    pv.setDecimalValues(
        getDTR(2000), new DecimalValue(1), new DecimalValue(2), new DecimalValue(3));
    pv.setDecimalValues(
        getDTR(2001), new DecimalValue(4), new DecimalValue(5), new DecimalValue(6));
    return pv;
  }

  private static PhenotypeValues getPhenotype2() {
    PhenotypeValues pv = new PhenotypeValues("P2");
    pv.setDecimalValues(
        getDTR(2002), new DecimalValue(7), new DecimalValue(8), new DecimalValue(9));
    pv.setDecimalValues(
        getDTR(2003), new DecimalValue(10), new DecimalValue(11), new DecimalValue(12));
    return pv;
  }

  private static PhenotypeValues getPhenotype3() {
    PhenotypeValues pv = new PhenotypeValues("P3");
    pv.setDecimalValues(
        getDTR(2004), new DecimalValue(13), new DecimalValue(14), new DecimalValue(15));
    pv.setDecimalValues(
        getDTR(2005), new DecimalValue(16), new DecimalValue(17), new DecimalValue(18));
    return pv;
  }

  private static PhenotypeValues getPhenotype3b() {
    PhenotypeValues pv = new PhenotypeValues("P3");
    pv.setDecimalValues(
        getDTR(2004), new DecimalValue(13), new DecimalValue(14), new DecimalValue(15));
    pv.setDecimalValues(
        getDTR(2008), new DecimalValue(25), new DecimalValue(26), new DecimalValue(27));
    pv.setDecimalValues(
        getDTR(2009), new DecimalValue(28), new DecimalValue(29), new DecimalValue(30));
    return pv;
  }

  private static PhenotypeValues getPhenotype4() {
    PhenotypeValues pv = new PhenotypeValues("P4");
    pv.setDecimalValues(
        getDTR(2006), new DecimalValue(19), new DecimalValue(20), new DecimalValue(21));
    pv.setDecimalValues(
        getDTR(2007), new DecimalValue(22), new DecimalValue(23), new DecimalValue(24));
    return pv;
  }
}
