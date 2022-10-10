package care.smith.top.top_phenotypic_query.c2reasoner;

import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import care.smith.top.model.Expression;
import care.smith.top.model.NumberValue;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.ConstantEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.E;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.False;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.Now;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.Pi;
import care.smith.top.top_phenotypic_query.c2reasoner.constants.True;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.FunctionEntity;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.aggregate.Last;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Add;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Divide;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Multiply;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Power;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Subtract;
import care.smith.top.top_phenotypic_query.c2reasoner.functions.arithmetic.Sum;
import care.smith.top.top_phenotypic_query.util.RestrictionUtil;
import care.smith.top.top_phenotypic_query.util.ValueUtil;

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
    //    addFunction(Avg.get());
    //    addFunction(Count.get());
    //    addFunction(First.get());
    addFunction(Last.get());
    //    addFunction(Min.get());
    //    addFunction(Max.get());
    //    addFunction(Date.get());
    //    addFunction(DiffYears.get());
    //    addFunction(DiffMonths.get());
    //    addFunction(DiffDays.get());
    //    addFunction(PlusYears.get());
    //    addFunction(PlusMonths.get());
    //    addFunction(PlusDays.get());
    //    addFunction(And.get());
    //    addFunction(Or.get());
    //    addFunction(Not.get());
    //    addFunction(MinTrue.get());
    //    addFunction(Eq.get());
    //    addFunction(Ge.get());
    //    addFunction(Gt.get());
    //    addFunction(Le.get());
    //    addFunction(Lt.get());
    //    addFunction(Ne.get());
    //    addFunction(In.get());
    //    addFunction(Switch.get());
    //    addFunction(Li.get());
    //    addFunction(Restrict.get());
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
    String expStr = toString(exp);
    log.info("start calculating math expression: {} ...", expStr);
    Expression result = calc(exp, defaultAggregateFunction);
    log.info("result of calculating math expression: {} = {}", expStr, toString(result));
    return result;
  }

  private Expression calc(Expression exp, FunctionEntity defaultAggregateFunction) {
    if (exp.getConstantId() != null) return calcConstant(exp);
    if (exp.getEntityId() != null) return calcVariable(exp);
    if (exp.getFunctionId() != null) return calcFunction(exp, defaultAggregateFunction);
    return exp;
  }

  private Expression calcConstant(Expression exp) {
    Exceptions.checkConstantExists(exp, constants);
    Expression result = getConstant(exp.getConstantId()).getValueExpression();
    log.info("set constant: {} = {}", exp.getConstantId(), ValueUtil.toString(result.getValue()));
    return result;
  }

  private Expression calcVariable(Expression exp) {
    Exceptions.checkVariableIsSet(exp, variables);
    Expression result = variables.get(exp.getEntityId());
    log.info("set variable: {} = {}", exp.getEntityId(), ValueUtil.toString(result.getValue()));
    return result;
  }

  //  List<Expression> vals =
  //	        exp.getArguments().stream()
  //	            .map(e -> calc(e, defaultAggregateFunction))
  //	            .collect(Collectors.toList());

  public Expression calcFunction(Expression exp, FunctionEntity defaultAggregateFunction) {
    Exceptions.checkFunctionExists(exp, functions);
    log.info("start calculating function '{}': {} ...", exp.getFunctionId(), toString(exp));
    FunctionEntity func = getFunction(exp.getFunctionId());
    return func.calculate(exp.getArguments(), defaultAggregateFunction, this);
  }

  public void addFunction(FunctionEntity function) {
    functions.put(function.getFunction().getId(), function.mathContext(mc));
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
    variables.put(name, ValueUtil.toExpression(value));
  }

  public void setVariable(String name, Value... values) {
    variables.put(name, ValueUtil.toExpression(values));
  }

  public void setVariable(String name, Number value) {
    setVariable(name, ValueUtil.toExpression(value));
  }

  public void setVariable(String name, NumberValue... values) {
    setVariable(name, ValueUtil.toExpression(values));
  }

  public void setVariable(String name, Number... values) {
    setVariable(name, ValueUtil.toExpression(values));
  }

  private String toString(Expression exp) {
    if (exp.getEntityId() != null) return exp.getEntityId();
    if (exp.getConstantId() != null)
      return getConstant(exp.getConstantId()).getConstant().getTitle();
    if (exp.getValue() != null) return ValueUtil.toString(exp.getValue());
    if (exp.getValues() != null) return ValueUtil.toString(exp.getValues());
    if (exp.getRestriction() != null) return RestrictionUtil.toString(exp.getRestriction());
    return toStringFunction(exp);
  }

  private String toStringFunction(Expression exp) {
    List<String> args =
        exp.getArguments().stream().map(e -> toString(e)).collect(Collectors.toList());
    return getFunction(exp.getFunctionId()).toString(args);
  }
}
