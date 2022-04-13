package care.smith.top.top_phenotypic_query;

public class Test {

  public static void main(String[] args) {
    FHIRDataConfig conf = FHIRDataConfig.getInstance("SMITH_FHIR_Adapter.yaml");
    System.out.println(conf);

    PatientQuery q = conf.getPatientQuery();
    PatientQueryParameters params = q.getParameters();
    String ge = conf.getOperators().getGe();
    String lt = conf.getOperators().getLt();

    System.out.println(
        q.getParameterQuery(
            conf,
            params.getGender("male"),
            params.getBirthdate(ge, "1990-01-01"),
            params.getBirthdate(lt, "2000-31-12")));
  }
}
