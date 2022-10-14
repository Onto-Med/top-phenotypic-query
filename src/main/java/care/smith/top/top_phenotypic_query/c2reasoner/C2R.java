package care.smith.top.top_phenotypic_query.c2reasoner;

import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Constant;
import care.smith.top.model.Expression;
import care.smith.top.model.ExpressionFunction;
import care.smith.top.model.NumberValue;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.ConstantEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.E;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.False;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.Now;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.Pi;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.True;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Empty;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.In;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Restrict;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.advanced.Switch;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Avg;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Count;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.First;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Last;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Max;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Min;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Add;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Multiply;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Power;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Subtract;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Sum;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.And;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.MinTrue;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Not;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.bool.Or;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Eq;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ge;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Gt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Le;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Lt;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.comparison.Ne;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.Date;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.DiffDays;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.DiffMonths;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.DiffYears;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.PlusDays;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.PlusMonths;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.date_time.PlusYears;
import care.smith.top.top_phenotypic_query.util.Expressions;
import care.smith.top.top_phenotypic_query.util.Restrictions;
import care.smith.top.top_phenotypic_query.util.Values;

public class C2R {

  private MathContext mc;
  private Map<String, FunctionEntity> functions = new HashMap<>();
  private Map<String, ConstantEntity> constants = new HashMap<>();
  private Map<String, Expression> variables = new HashMap<>();

  private Logger log = LoggerFactory.getLogger(C2R.class);

  public C2R() {
    this(MathContext.DECIMAL64);
  }

  public C2R(MathContext mc) {
    this.mc = mc;
    addConstant(Pi.get());
    addConstant(E.get());
    addConstant(True.get());
    addConstant(False.get());
    addConstant(Now.get());
    addFunction(Add.get());
    addFunction(Subtract.get());
    addFunction(Multiply.get());
    addFunction(Divide.get());
    addFunction(Power.get());
    addFunction(Sum.get());
    addFunction(Avg.get());
    addFunction(Count.get());
    addFunction(First.get());
    addFunction(Last.get());
    addFunction(Min.get());
    addFunction(Max.get());
    addFunction(Date.get());
    addFunction(DiffYears.get());
    addFunction(DiffMonths.get());
    addFunction(DiffDays.get());
    addFunction(PlusYears.get());
    addFunction(PlusMonths.get());
    addFunction(PlusDays.get());
    addFunction(And.get());
    addFunction(Or.get());
    addFunction(Not.get());
    addFunction(MinTrue.get());
    addFunction(Eq.get());
    addFunction(Ge.get());
    addFunction(Gt.get());
    addFunction(Le.get());
    addFunction(Lt.get());
    addFunction(Ne.get());
    addFunction(In.get());
    addFunction(Empty.get());
    addFunction(Switch.get());
    addFunction(Restrict.get());
  }

  public MathContext getMathContext() {
    return mc;
  }

  public Expression calculate(Expression exp) {
    return calculate(exp, Last.get());
  }

  public List<Expression> calculate(
      List<Expression> args, FunctionEntity defaultAggregateFunction) {
    return args.stream()
        .map(a -> calculate(a, defaultAggregateFunction))
        .collect(Collectors.toList());
  }

  public Expression calculate(Expression exp, FunctionEntity defaultAggregateFunction) {
    if (exp.getConstantId() != null) return calcConstant(exp);
    if (exp.getEntityId() != null) return calcVariable(exp);
    if (exp.getFunctionId() != null) return calcFunction(exp, defaultAggregateFunction);
    return exp;
  }

  private Expression calcConstant(Expression exp) {
    log.info("start setting constant: {} ...", exp.getConstantId());
    Exceptions.checkConstantExists(exp, constants);
    Expression result = getConstant(exp.getConstantId()).getValueExpression();
    log.info("end setting constant: {} = {}", exp.getConstantId(), toString(result));
    return result;
  }

  private Expression calcVariable(Expression exp) {
    log.info("start setting variable: {} ...", exp.getEntityId());
    Exceptions.checkVariableIsSet(exp, variables);
    Expression result = variables.get(exp.getEntityId());
    log.info("end setting variable: {} = {}", exp.getEntityId(), toString(result));
    return result;
  }

  public Expression calcFunction(Expression exp, FunctionEntity defaultAggregateFunction) {
    String expStr = toString(exp);
    log.info("start calculating function '{}': {} ...", exp.getFunctionId(), expStr);
    Exceptions.checkFunctionExists(exp, functions);
    FunctionEntity func = getFunction(exp.getFunctionId());
    Expression result = func.calculate(exp.getArguments(), defaultAggregateFunction, this);
    log.info(
        "end calculating function '{}': {} = {}", exp.getFunctionId(), expStr, toString(result));
    return result;
  }

  public void addFunction(FunctionEntity function) {
    functions.put(function.getFunction().getId(), function.mathContext(mc));
  }

  public Set<ExpressionFunction> getExpressionFunctions() {
    return getFunctions().stream().map(f -> f.getFunction()).collect(Collectors.toSet());
  }

  public Collection<FunctionEntity> getFunctions() {
    return functions.values();
  }

  public FunctionEntity getFunction(String id) {
    return functions.get(id);
  }

  public void addConstant(ConstantEntity constant) {
    constants.put(constant.getConstant().getId(), constant);
  }

  public Set<Constant> getExpressionConstants() {
    return getConstants().stream().map(c -> c.getConstant()).collect(Collectors.toSet());
  }

  public Collection<ConstantEntity> getConstants() {
    return constants.values();
  }

  public ConstantEntity getConstant(String id) {
    return constants.get(id);
  }

  public Map<String, Expression> getVariables() {
    return variables;
  }

  public void setVariable(String name, Expression value) {
    variables.put(name, value);
  }

  public void setVariable(String name, Value value) {
    variables.put(name, Expressions.newExpression(value));
  }

  public void setVariable(String name, Value... values) {
    variables.put(name, Expressions.newExpression(values));
  }

  public void setVariable(String name, List<Value> values) {
    variables.put(name, Expressions.newExpression(values));
  }

  public void setVariable(String name, Number value) {
    setVariable(name, Expressions.newExpression(value));
  }

  public void setVariable(String name, NumberValue... values) {
    setVariable(name, Expressions.newExpression(values));
  }

  public void setVariable(String name, Number... values) {
    setVariable(name, Expressions.newExpression(values));
  }

  private String toString(Expression exp) {
    if (exp.getEntityId() != null) return exp.getEntityId();
    if (exp.getConstantId() != null)
      return getConstant(exp.getConstantId()).getConstant().getTitle();
    if (exp.getValue() != null) return Values.toString(exp.getValue());
    if (exp.getValues() != null) return Values.toString(exp.getValues());
    if (exp.getRestriction() != null) return Restrictions.toString(exp.getRestriction());
    return toStringFunction(exp);
  }

  private String toStringFunction(Expression exp) {
    List<String> args =
        exp.getArguments().stream().map(e -> toString(e)).collect(Collectors.toList());
    return getFunction(exp.getFunctionId()).toString(args);
  }
}
