package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.Values;

public class ResultSetTest extends AbstractTest {

  @Test
  public void testValues() {
    PhenotypeValues weightVals1 = new PhenotypeValues("Weight");
    weightVals1.setValues(getDTR(2001), Values.newValue(75));
    PhenotypeValues heightVals1 = new PhenotypeValues("Height");
    heightVals1.setValues(getDTR(2001), Values.newValue(1.7));
    PhenotypeValues ageVals1 = new PhenotypeValues("Age");
    ageVals1.setValues(getDTR(2001), Values.newValue(20));

    PhenotypeValues weightVals2 = new PhenotypeValues("Weight");
    weightVals2.setValues(getDTR(2001), Values.newValue(80));
    PhenotypeValues heightVals2 = new PhenotypeValues("Height");
    heightVals2.setValues(getDTR(2001), Values.newValue(1.6));
    PhenotypeValues ageVals2 = new PhenotypeValues("Age");
    ageVals2.setValues(getDTR(2001), Values.newValue(40));

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
    pv.setValues(getDTR(2000), Values.newValue(1), Values.newValue(2), Values.newValue(3));
    pv.setValues(getDTR(2001), Values.newValue(4), Values.newValue(5), Values.newValue(6));
    return pv;
  }

  private static PhenotypeValues getPhenotype2() {
    PhenotypeValues pv = new PhenotypeValues("P2");
    pv.setValues(getDTR(2002), Values.newValue(7), Values.newValue(8), Values.newValue(9));
    pv.setValues(getDTR(2003), Values.newValue(10), Values.newValue(11), Values.newValue(12));
    return pv;
  }

  private static PhenotypeValues getPhenotype3() {
    PhenotypeValues pv = new PhenotypeValues("P3");
    pv.setValues(getDTR(2004), Values.newValue(13), Values.newValue(14), Values.newValue(15));
    pv.setValues(getDTR(2005), Values.newValue(16), Values.newValue(17), Values.newValue(18));
    return pv;
  }

  private static PhenotypeValues getPhenotype3b() {
    PhenotypeValues pv = new PhenotypeValues("P3");
    pv.setValues(getDTR(2004), Values.newValue(13), Values.newValue(14), Values.newValue(15));
    pv.setValues(getDTR(2008), Values.newValue(25), Values.newValue(26), Values.newValue(27));
    pv.setValues(getDTR(2009), Values.newValue(28), Values.newValue(29), Values.newValue(30));
    return pv;
  }

  private static PhenotypeValues getPhenotype4() {
    PhenotypeValues pv = new PhenotypeValues("P4");
    pv.setValues(getDTR(2006), Values.newValue(19), Values.newValue(20), Values.newValue(21));
    pv.setValues(getDTR(2007), Values.newValue(22), Values.newValue(23), Values.newValue(24));
    return pv;
  }
}
