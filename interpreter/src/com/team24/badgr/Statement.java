package com.team24.badgr;

import java.util.List;

abstract class Statement {
  interface Visitor<R> {
    R visitBlockStatement(Block statement);
    R visitExprStatement(Expr statement);
    R visitIfStatement(If statement);
    R visitPrintStatement(Print statement);
    R visitVarStatement(Var statement);
  }
  static class Block extends Statement {
    Block(List<Statement> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStatement(this);
    }

    final List<Statement> statements;
  }
  static class Expr extends Statement {
    Expr(Expression expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExprStatement(this);
    }

    final Expression expression;
  }
  static class If extends Statement {
    If(Expression condition, Statement thenBranch, Statement elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStatement(this);
    }

    final Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;
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
  static class Var extends Statement {
    Var(Token name, Expression initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStatement(this);
    }

    final Token name;
    final Expression initializer;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
