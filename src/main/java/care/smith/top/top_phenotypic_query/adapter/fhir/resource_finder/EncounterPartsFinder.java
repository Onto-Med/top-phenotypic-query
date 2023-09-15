package care.smith.top.top_phenotypic_query.adapter.fhir.resource_finder;

import care.smith.top.top_phenotypic_query.adapter.fhir.FHIRClient;
import care.smith.top.top_phenotypic_query.adapter.fhir.FHIRUtil;
import care.smith.top.top_phenotypic_query.result.ResultSet;
import care.smith.top.top_phenotypic_query.result.SubjectPhenotypes;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

public class EncounterPartsFinder extends FHIRAbstractResourceFinder {

  private Map<String, String> partOfRefs = new HashMap<>();
  private Map<String, String> parts = new HashMap<>();

  public EncounterPartsFinder(FHIRClient cli) {
    super(cli.getClient());
    findResources("Encounter?_count=100&_elements=partOf");
    getParts();
  }

  @Override
  protected void addResource(Resource res) {
    Encounter enc = (Encounter) res;
    Reference partOfRef = enc.getPartOf();
    if (partOfRef != null
        && partOfRef.getReferenceElement() != null
        && partOfRef.getReferenceElement().getIdPart() != null)
      partOfRefs.put(FHIRUtil.getId(enc), FHIRUtil.getId(partOfRef));
  }

  private void getParts() {
    partOfRefs.forEach((child, parent) -> add(parent, Sets.newHashSet(child)));

    //    parts.entrySet().stream()
    //        .sorted(Map.Entry.<String, String>comparingByKey())
    //        .forEach(System.out::println);
  }

  private void add(String parent, HashSet<String> children) {
    String parentOfParent = partOfRefs.get(parent);
    if (parentOfParent == null) children.forEach(child -> parts.put(child, parent));
    else {
      children.add(parent);
      add(parentOfParent, children);
    }
  }

  public void groupParts(ResultSet rs) {
    if (parts.isEmpty()) return;
    for (String childId : new HashSet<>(rs.getSubjectIds())) {
      String parentId = parts.get(childId);
      if (parentId != null) {
        SubjectPhenotypes childPhens = rs.getPhenotypes(childId);
        if (childPhens == null) continue;
        SubjectPhenotypes parentPhens = rs.getPhenotypes(parentId);
        if (parentPhens == null) {
          parentPhens = new SubjectPhenotypes(parentId);
          rs.setPhenotypes(parentPhens);
        }
        parentPhens.addValues(childPhens);
        rs.removeSubject(childId);
      }
    }
  }
}
