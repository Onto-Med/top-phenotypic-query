package care.smith.top.top_phenotypic_query.song;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Entity;
import care.smith.top.model.EntityType;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.song.functions.And;
import care.smith.top.top_phenotypic_query.song.functions.Not;
import care.smith.top.top_phenotypic_query.song.functions.Or;
import care.smith.top.top_phenotypic_query.song.functions.SubTree;
import care.smith.top.top_phenotypic_query.song.functions.TextFunction;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class SONG {

  private Entities concepts;
  private Map<String, TextFunction> functions = new HashMap<>();
  private String lang;

  private Logger log = LoggerFactory.getLogger(SONG.class);

  public static final String EXPRESSION_TYPE_QUERY = "query";
  public static final String EXPRESSION_TYPE_TERMS_RAW = "terms_raw";
  public static final String EXPRESSION_TYPE_TERMS_READY = "terms_ready";

  protected SONG(And and, Or or, Not not) {
    addFunction(and);
    addFunction(or);
    addFunction(not);
    addFunction(SubTree.get());
  }

  public Entities getConcepts() {
    return concepts;
  }

  public Entity getConcept(String id) {
    return concepts.getEntity(id);
  }

  public void setConcepts(Entities concepts) {
    this.concepts = concepts;
  }

  public static ExpressionFunction[] getExpressionFunctions() {
    return new ExpressionFunction[] {And.FUNCTION, Or.FUNCTION, Not.FUNCTION, SubTree.FUNCTION};
  }

  private void addFunction(TextFunction f) {
    functions.put(f.getId(), f);
  }

  public Collection<TextFunction> getFunctions() {
    return functions.values();
  }

  public TextFunction getFunction(String id) {
    return functions.get(id);
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public boolean hasQuery(Entity con) {
    // if single concept
    if (con.getEntityType() == EntityType.CATEGORY) return false;
    // else composite concept
    Expression exp = ((Phenotype) con).getExpression();
    return getFunction(exp.getFunctionId()).isQueryFunction();
  }

  public boolean hasTerms(Entity con) {
    return !hasQuery(con);
  }

  public boolean hasQuery(Expression exp) {
    return EXPRESSION_TYPE_QUERY.equals(exp.getConstantId());
  }

  public boolean hasTerms(Expression exp) {
    return EXPRESSION_TYPE_TERMS.equals(exp.getConstantId());
  }

  public String getQuery(Expression exp) {
	  if (EXPRESSION_TYPE_QUERY.equals(exp.getConstantId())) return Expressions.getStringValue(exp);
	  return 
  }

  public Expression generate(Entity con) {
    log.debug("start generating query for concept: {} ...", con.getId());

    // if single concept
    if (con.getEntityType() == EntityType.CATEGORY) {
      Expression res = getTermsExpression(con, false);
      log.debug("end generating query for concept: {} = {}", con.getId(), toString(res));
      return res;
    }

    // else composite concept
    Expression res = generate(((Phenotype) con).getExpression());
    log.debug("end generating query for concept : {} = {}", con.getId(), toString(res));
    return res;
  }

  public Expression getTermsExpression(String conId, boolean includeSubTree) {
    return getTermsExpression(getConcept(conId), includeSubTree);
  }

  public Expression getTermsExpression(Entity con, boolean includeSubTree) {
    Set<String> terms = Entities.getTerms(con, lang, includeSubTree);
    if (terms.isEmpty()) return new Expression();
    return Exp.of(terms.stream().map(t -> Val.of(t)).collect(Collectors.toList()))
        .constantId(EXPRESSION_TYPE_TERMS);
  }

  public Expression getTermsQueryExpression(List<String> terms) {
    List<Expression> args = terms.stream().map(t -> Exp.of(t)).collect(Collectors.toList());
    return getFunction(Or.ID).generate(args, this);
  }

  private String getTermsQuery(Expression exp) {
	  List<Expression> args = terms.stream().map(t -> Exp.of(t)).collect(Collectors.toList());
	  return getFunction(Or.ID).generate(args, this);
  }

  //  public Expression getLabelsExpression(Entity con, boolean includeSubTree) {
  //	  Set<String> labels = Entities.getLabels(con, lang, includeSubTree);
  //	  Expression res = null;
  //	  if (labels.isEmpty()) res = Exp.of("");
  //	  else {
  //		  List<Expression> args =
  //				  labels.stream().map(t -> Exp.of(quotePhrase(t))).collect(Collectors.toList());
  //		  res = getFunction(Or.ID).generate(args, this);
  //	  }
  //	  return res;
  //  }

  public String quotePhrase(String s) {
    if (StringUtils.containsWhitespace(s)) return "\"" + s + "\"";
    return s;
  }

  public Expression generateConcept(String conId) {
    return generate(getConcept(conId));
  }

  public Expression generate(Expression exp) {
    if (exp.getEntityId() != null) return generateConcept(exp.getEntityId());
    if (exp.getFunctionId() != null) return generateFunction(exp);
    return exp;
  }

  public Expression generateFunction(Expression exp) {
    String expStr = toString(exp);
    log.debug("start generating query for function '{}': {} ...", exp.getFunctionId(), expStr);
    TextFunction func = getFunction(exp.getFunctionId());
    Expression res = func.generate(exp.getArguments(), this);
    log.debug(
        "end generating query for function '{}': {} = {}",
        exp.getFunctionId(),
        expStr,
        toString(res));
    return res;
  }

  public List<Expression> generate(List<Expression> args) {
    return args.stream()
        .map(a -> generate(a))
        .filter(a -> !Expressions.isEmpty(a))
        .collect(Collectors.toList());
  }

  public String toString(Expression exp) {
    if (exp == null) return "null";
    if (exp.getEntityId() != null) return exp.getEntityId();
    if (exp.getValues() != null) return Values.toString(exp.getValues());
    if (exp.getRestriction() != null) return Restrictions.toString(exp.getRestriction());
    return operatorToString(exp);
  }

  private String operatorToString(Expression exp) {
    List<String> args =
        exp.getArguments().stream().map(e -> toString(e)).collect(Collectors.toList());
    return getFunction(exp.getFunctionId()).toString(args);
  }
}
