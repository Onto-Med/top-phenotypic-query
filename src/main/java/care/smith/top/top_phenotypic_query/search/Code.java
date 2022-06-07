package care.smith.top.top_phenotypic_query.search;

import care.smith.top.simple_onto_api.util.StringUtil;
import care.smith.top.simple_onto_api.util.ToString;

public class Code {

  private String systemUri;
  private String systemName;
  private String code;
  private String codeName;
  private String codeUri;
  public static final String URI_SEPARATOR = "|";

  public Code(String systemUri, String code) {
    this.systemUri = systemUri;
    this.code = code;
    this.codeUri = systemUri + URI_SEPARATOR + code;
  }

  public Code(String codeUri) {
    String[] systemAndCode = codeUri.split("\\" + URI_SEPARATOR);
    this.systemUri = systemAndCode[0];
    this.code = systemAndCode[1];
    this.codeUri = codeUri;
  }

  public String getSystemName() {
    return systemName;
  }

  public Code systemName(String systemName) {
    this.systemName = systemName;
    return this;
  }

  public boolean hasSystemName() {
    return StringUtil.notEmpty(systemName);
  }

  public String getCodeName() {
    return codeName;
  }

  public Code codeName(String codeName) {
    this.codeName = codeName;
    return this;
  }

  public boolean hasCodeName() {
    return StringUtil.notEmpty(codeName);
  }

  public String getSystemUri() {
    return systemUri;
  }

  public String getCode() {
    return code;
  }

  public String getCodeUri() {
    return codeUri;
  }

  @Override
  public String toString() {
    return ToString.get(this)
        .add("systemUri", systemUri)
        .add("systemName", systemName)
        .add("code", code)
        .add("codeName", codeName)
        .add("codeUri", codeUri)
        .toString();
  }
}
