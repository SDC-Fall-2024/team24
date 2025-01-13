package com.team24.badgr;

abstract class Statement {
  interface Visitor<R> {
    R visitExpressionStatement(Expr statement);
    R visitPrintStatement(Print statement);
  }
  static class Expr extends Statement {
    Expr(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStatement(this);
    }

    final Expression expression;
  }
  static class Print extends Statement {
    Print(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStatement(this);
    }

    final Expression expression;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
