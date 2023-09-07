package care.smith.top.top_phenotypic_query.adapter.fhir.resource_finder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;

import com.google.common.collect.Sets;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import care.smith.top.top_phenotypic_query.adapter.fhir.FHIRClient;
import care.smith.top.top_phenotypic_query.adapter.fhir.FHIRUtil;

public class EncounterPartsFinder extends FHIRAbstractResourceFinder {

  private Map<String, String> partOfRefs = new HashMap<>();
  private Map<String, String> parts = new HashMap<>();

  private EncounterPartsFinder(IGenericClient client) {
    super(client);
  }

  public static Map<String, String> get(FHIRClient cli) {
    EncounterPartsFinder finder = new EncounterPartsFinder(cli.getClient());
    finder.findResources("Encounter?_count=100&_elements=partOf");
    return finder.getParts();
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

  public Map<String, String> getParts() {
    partOfRefs.forEach((child, parent) -> add(parent, Sets.newHashSet(child)));
    return parts;
  }

  private void add(String parent, HashSet<String> children) {
    String parentOfParent = partOfRefs.get(parent);
    if (parentOfParent == null) children.forEach(child -> parts.put(child, parent));
    else {
      children.add(parent);
      add(parentOfParent, children);
    }
  }
}
