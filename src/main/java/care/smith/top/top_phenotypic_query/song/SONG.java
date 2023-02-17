package care.smith.top.top_phenotypic_query.song;

import java.util.ArrayList;
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
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.song.operator.SearchOperator;
import care.smith.top.top_phenotypic_query.util.Entities;
import care.smith.top.top_phenotypic_query.util.Phenotypes;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;
import care.smith.top.top_phenotypic_query.util.builder.Exp;
import care.smith.top.top_phenotypic_query.util.builder.Val;

public class SONG {

  private Entities concepts;
  private Map<String, SearchOperator> operators = new HashMap<>();
  private String lang;

  private Logger log = LoggerFactory.getLogger(SONG.class);

  public Entities getConcepts() {
    return concepts;
  }

  public void setConcepts(Entities concepts) {
    this.concepts = concepts;
  }

  protected void addOperator(SearchOperator operator) {
    operators.put(operator.getFunctionId(), operator);
  }

  public Set<ExpressionFunction> getExpressionFunctions() {
    return getOperators().stream().map(f -> f.getFunction()).collect(Collectors.toSet());
  }

  public Collection<SearchOperator> getOperators() {
    return operators.values();
  }

  public SearchOperator getOperator(String id) {
    return operators.get(id);
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
      List<Value> terms =
          Entities.getTitlesAndSynonyms(phe, lang).stream()
              .map(t -> Val.of(t))
              .collect(Collectors.toList());
      Expression res = Exp.of(terms);
      log.debug("end generating query for variable: {} = {}", phe.getId(), toString(res));
      return res;
    }

    Expression res = generate(phe.getExpression());
    log.debug("end generating query for variable: {} = {}", phe.getId(), toString(res));
    return res;
  }

  public Expression generateVariable(String pheId) {
    concepts.getPhenotype(pheId);
    return generate(concepts.getPhenotype(pheId));
  }

  public Expression generate(Expression exp) {
    if (exp.getEntityId() != null) return generateVariable(exp.getEntityId());
    if (exp.getFunctionId() != null) return generateOperator(exp);
    return exp;
  }

  public Expression generateOperator(Expression exp) {
    String expStr = toString(exp);
    log.debug("start generating query for operator '{}': {} ...", exp.getFunctionId(), expStr);
    SearchOperator oper = getOperator(exp.getFunctionId());
    Expression result = oper.generate(exp.getArguments(), this);
    log.debug(
        "end generating query for operator '{}': {} = {}",
        exp.getFunctionId(),
        expStr,
        toString(result));
    return result;
  }

  public List<Expression> generate(List<Expression> args) {
    List<Expression> calculated = new ArrayList<>();
    for (Expression arg : args) {
      Expression res = generate(arg);
      if (res == null) return null;
      calculated.add(res);
    }
    return calculated;
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
    return getOperator(exp.getFunctionId()).toString(args);
  }
}
