package com.team24.badgr;

class AstPrinter implements Expression.Visitor<String> {
  String print(Expression expr) {
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpression(Expression.Binary expr) {
    return parenthesize(expr.operator.getText(),
                        expr.left, expr.right);
  }

  @Override
  public String visitGroupingExpression(Expression.Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpression(Expression.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpression(Expression.Unary expr) {
    return parenthesize(expr.operator.getText(), expr.right);
  }

  private String parenthesize(String name, Expression... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    for (Expression expr : exprs) {
      builder.append(" ");
      builder.append(expr.accept(this));
    }
    builder.append(")");

    return builder.toString();
  }
  // public static void main(String[] args) {
  //   Expression expression = new Expression.Binary(
  //       new Expression.Unary(
  //           new Token(TokenType.MINUS, 1,  "-", null),
  //           new Expression.Literal(123)),
  //       new Token(TokenType.STAR, 1, "*", null),
  //       new Expression.Grouping(
  //           new Expression.Literal(45.67)));
  
  //   System.out.println(new AstPrinter().print(expression));
  // }
}
