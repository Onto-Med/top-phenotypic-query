package care.smith.top.top_phenotypic_query.song;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.Phenotype;
import care.smith.top.top_phenotypic_query.song.functions.And;
import care.smith.top.top_phenotypic_query.song.functions.Not;
import care.smith.top.top_phenotypic_query.song.functions.Or;
import care.smith.top.top_phenotypic_query.song.functions.TextFunction;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;

public class SONG {

  private Entities concepts;
  private Map<String, TextFunction> functions = new HashMap<>();
  private String lang;

  private Logger log = LoggerFactory.getLogger(SONG.class);

  protected SONG(And and, Or or, Not not) {
    addFunction(and);
    addFunction(or);
    addFunction(not);
  }

  public Entities getConcepts() {
    return concepts;
  }

  public void setConcepts(Entities concepts) {
    this.concepts = concepts;
  }

  private void addFunction(TextFunction f) {
    functions.put(f.getId(), f);
  }

  public static ExpressionFunction[] getExpressionFunctions() {
    return new ExpressionFunction[] {And.FUNCTION, Or.FUNCTION, Not.FUNCTION};
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

  public Expression generate(Phenotype phe) {
    log.debug("start generating query for variable: {} ...", phe.getId());

    if (Phenotypes.isSingle(phe)) {
      Set<String> terms = Entities.getTitlesAndSynonyms(phe, lang);
      Expression res = null;
      if (terms.isEmpty()) res = Exp.of("");
      else {
        List<Expression> args = terms.stream().map(t -> Exp.of(t)).collect(Collectors.toList());
        res = getFunction(Or.ID).generate(args, this);
      }
      log.debug(
          "end generating query for variable: {} = {}",
          phe.getId(),
          Expressions.getStringValue(res));
      return res;
    }

    Expression res = generate(phe.getExpression());
    log.debug(
        "end generating query for variable: {} = {}", phe.getId(), Expressions.getStringValue(res));
    return res;
  }

  public Expression generateVariable(String pheId) {
    return generate(concepts.getPhenotype(pheId));
  }

  public Expression generate(Expression exp) {
    if (exp.getEntityId() != null) return generateVariable(exp.getEntityId());
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
        Expressions.getStringValue(res));
    return res;
  }

  public List<Expression> generate(List<Expression> args) {
    return args.stream()
        .map(a -> generate(a))
        .filter(a -> !Expressions.hasBlankStringValue(a))
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
