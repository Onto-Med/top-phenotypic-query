package care.smith.top.top_phenotypic_query.util.builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import care.smith.top.model.Expression;
import care.smith.top.model.Phenotype;
import care.smith.top.model.Restriction;
import care.smith.top.model.Value;
import care.smith.top.top_phenotypic_query.util.Phenotypes;

public class Exp {

  public static Expression of(Phenotype p) {
    return ofEntity(p.getId());
  }

  public static Expression ofEntity(String entityId) {
    return new Expression().entityId(entityId);
  }

  public static Expression ofConstant(String constantId) {
    return new Expression().constantId(constantId);
  }

  public static Expression of(Restriction r) {
    return new Expression().restriction(r);
  }

  public static Expression of(Value val) {
    return new Expression().value(val);
  }

  public static Expression of(List<Value> vals) {
    return new Expression().values(vals);
  }

  public static Expression of(Value... vals) {
    return of(List.of(vals));
  }

  public static Expression of(Number val, LocalDateTime dateTime) {
    return of(Val.of(val, dateTime));
  }

  public static Expression of(Number val) {
    return of(Val.of(val));
  }

  public static Expression of(Number... numbers) {
    return of(Val.of(numbers));
  }

  public static Expression of(Boolean val) {
    return of(Val.of(val));
  }

  public static Expression of(LocalDateTime val) {
    return of(Val.of(val));
  }

  public static List<Expression> toList(List<Value> vals) {
    return vals.stream().map(v -> of(v)).collect(Collectors.toList());
  }

  public static Expression[] toArray(Phenotype... entities) {
    return Stream.of(entities).map(e -> of(e)).toArray(Expression[]::new);
  }

  public static Expression ofTrue() {
    return of(Val.ofTrue());
  }

  public static Expression ofFalse() {
    return of(Val.ofFalse());
  }

  public static Expression ofRestriction(Phenotype p) {
    if (!Phenotypes.isRestriction(p)) return null;
    Restriction r = p.getRestriction();
    if (r == null) return null;
    return new Expression()
        .functionId("in")
        .addArgumentsItem(new Expression().entityId(p.getSuperPhenotype().getId()))
        .addArgumentsItem(new Expression().restriction(r));
  }

  public static Expression function(String functionId, List<Expression> args) {
    return new Expression().functionId(functionId).arguments(args);
  }

  public static Expression function(String functionId, Expression... args) {
    return function(functionId, List.of(args));
  }

  public static Expression and(List<Expression> args) {
    return function("and", args);
  }

  public static Expression and(Expression... args) {
    return and(List.of(args));
  }

  public static Expression or(List<Expression> args) {
    return function("or", args);
  }

  public static Expression or(Expression... args) {
    return or(List.of(args));
  }

  public static Expression not(Expression arg) {
    return function("not", arg);
  }

  public static Expression and(Phenotype... args) {
    return and(toArray(args));
  }

  public static Expression or(Phenotype... args) {
    return or(toArray(args));
  }

  public static Expression not(Phenotype arg) {
    return not(of(arg));
  }

  public static Expression notEntity(String entityId) {
    return not(ofEntity(entityId));
  }
}
