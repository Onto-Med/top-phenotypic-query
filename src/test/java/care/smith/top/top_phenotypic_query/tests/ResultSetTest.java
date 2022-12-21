package care.smith.top.top_phenotypic_query.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import care.smith.top.top_phenotypic_query.result.PhenotypeValues;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import care.smith.top.top_phenotypic_query.util.builder.ValueBuilder;

public class ResultSetTest extends AbstractTest {

  @Test
  public void testValues() {
    PhenotypeValues weightVals1 = new PhenotypeValues("Weight");
    weightVals1.setValues(getDTR(2001), ValueBuilder.of(75));
    PhenotypeValues heightVals1 = new PhenotypeValues("Height");
    heightVals1.setValues(getDTR(2001), ValueBuilder.of(1.7));
    PhenotypeValues ageVals1 = new PhenotypeValues("Age");
    ageVals1.setValues(getDTR(2001), ValueBuilder.of(20));

    PhenotypeValues weightVals2 = new PhenotypeValues("Weight");
    weightVals2.setValues(getDTR(2001), ValueBuilder.of(80));
    PhenotypeValues heightVals2 = new PhenotypeValues("Height");
    heightVals2.setValues(getDTR(2001), ValueBuilder.of(1.6));
    PhenotypeValues ageVals2 = new PhenotypeValues("Age");
    ageVals2.setValues(getDTR(2001), ValueBuilder.of(40));

    SubjectPhenotypes phes1 = new SubjectPhenotypes("Subject1");
    phes1.setValues(weightVals1, heightVals1, ageVals1);

    SubjectPhenotypes phes2 = new SubjectPhenotypes("Subject2");
    phes2.setValues(weightVals2, heightVals2, ageVals2);

    ResultSet rs = new ResultSet();
    rs.setPhenotypes(phes1, phes2);
    assertEquals(2, rs.size());
    assertTrue(
        rs.get(phes1.getSubjectId())
            .getPhenotypeNames()
            .containsAll(List.of(weight.getId(), height.getId(), age.getId())));
    assertTrue(
        rs.get(phes2.getSubjectId())
            .getPhenotypeNames()
            .containsAll(List.of(weight.getId(), height.getId(), age.getId())));
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
    pv.setValues(getDTR(2000), ValueBuilder.of(1), ValueBuilder.of(2), ValueBuilder.of(3));
    pv.setValues(getDTR(2001), ValueBuilder.of(4), ValueBuilder.of(5), ValueBuilder.of(6));
    return pv;
  }

  private static PhenotypeValues getPhenotype2() {
    PhenotypeValues pv = new PhenotypeValues("P2");
    pv.setValues(getDTR(2002), ValueBuilder.of(7), ValueBuilder.of(8), ValueBuilder.of(9));
    pv.setValues(getDTR(2003), ValueBuilder.of(10), ValueBuilder.of(11), ValueBuilder.of(12));
    return pv;
  }

  private static PhenotypeValues getPhenotype3() {
    PhenotypeValues pv = new PhenotypeValues("P3");
    pv.setValues(getDTR(2004), ValueBuilder.of(13), ValueBuilder.of(14), ValueBuilder.of(15));
    pv.setValues(getDTR(2005), ValueBuilder.of(16), ValueBuilder.of(17), ValueBuilder.of(18));
    return pv;
  }

  private static PhenotypeValues getPhenotype3b() {
    PhenotypeValues pv = new PhenotypeValues("P3");
    pv.setValues(getDTR(2004), ValueBuilder.of(13), ValueBuilder.of(14), ValueBuilder.of(15));
    pv.setValues(getDTR(2008), ValueBuilder.of(25), ValueBuilder.of(26), ValueBuilder.of(27));
    pv.setValues(getDTR(2009), ValueBuilder.of(28), ValueBuilder.of(29), ValueBuilder.of(30));
    return pv;
  }

  private static PhenotypeValues getPhenotype4() {
    PhenotypeValues pv = new PhenotypeValues("P4");
    pv.setValues(getDTR(2006), ValueBuilder.of(19), ValueBuilder.of(20), ValueBuilder.of(21));
    pv.setValues(getDTR(2007), ValueBuilder.of(22), ValueBuilder.of(23), ValueBuilder.of(24));
    return pv;
  }
}
