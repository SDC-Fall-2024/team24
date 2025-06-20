package com.team24.badgr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.team24.badgr.Statement.Expr;

class Resolver implements Expression.Visitor<Void>, Statement.Visitor<Void> {
  private final Interpreter interpreter;
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();

  private enum FunctionType {
    NONE,
    FUNCTION
  }

  private FunctionType currentFunction = FunctionType.NONE;

  Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  @Override
  public Void visitBlockStatement(Statement.Block stmt) {
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitExprStatement(Statement.Expr stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitIfStatement(Statement.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null) resolve(stmt.elseBranch);
    return null;
  }

  @Override
  public Void visitPrintStatement(Statement.Print stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitReturnStatement(Statement.Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      throw new RuntimeError(stmt.keyword, "Can't return from top-level code.");
    }
    
    if (stmt.value != null) {
      resolve(stmt.value);
    }

    return null;
  }

  @Override
  public Void visitWhileStatement(Statement.While stmt) {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
  }

  void resolve(List<Statement> statements) {
    for (Statement statement : statements) {
      resolve(statement);
    }
  }

  private void resolve(Statement stmt) {
    stmt.accept(this);
  }

  private void resolve(Expression expr) {
    expr.accept(this);
  }

  private void beginScope() {
    scopes.push(new HashMap<String, Boolean>());
  }

  private void endScope() {
    scopes.pop();
  }

  @Override
  public Void visitVarStatement(Statement.Var stmt) {
    declare(stmt.name);
    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }
    define(stmt.name);
    return null;
  }

  private void declare(Token name) {
    if (scopes.isEmpty()) return;
    Map<String, Boolean> scope = scopes.peek();

    if (scope.containsKey(name.getText())) {
        throw new RuntimeError(name, "Already a variable with this name in this scope.");
    }
    
    scope.put(name.getText(), false);
  }

  private void define(Token name) {
    if (scopes.isEmpty()) return;
    scopes.peek().put(name.getText(), true);
  }

  @Override
  public Void visitVariableExpression(Expression.Variable expr) {
    if (!scopes.isEmpty() && scopes.peek().get(expr.name.getText()) == Boolean.FALSE) {
        throw new RuntimeError(expr.name, "Can't read local variable in its own initializer.");
    }

    resolveLocal(expr, expr.name);
    return null;
  }

  private void resolveLocal(Expression expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.getText())) {
        interpreter.resolve(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }

  @Override
  public Void visitAssignExpression(Expression.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitFunctionStatement(Statement.Function stmt) {
    declare(stmt.name);
    define(stmt.name);

    resolveFunction(stmt, FunctionType.FUNCTION);
    return null;
  }

  private void resolveFunction(Statement.Function function, FunctionType type) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;

    beginScope();
    for (Token param : function.params) {
      declare(param);
      define(param);
    }
    resolve(function.body);
    endScope();
    currentFunction = enclosingFunction;
  }

  @Override
  public Void visitBinaryExpression(Expression.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitCallExpression(Expression.Call expr) {
    resolve(expr.callee);

    for (Expression argument : expr.arguments) {
      resolve(argument);
    }

    return null;
  }

  @Override
  public Void visitGroupingExpression(Expression.Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpression(Expression.Literal expr) {
    return null;
  }

  @Override
  public Void visitLogicalExpression(Expression.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitUnaryExpression(Expression.Unary expr) {
    resolve(expr.right);
    return null;
  }

}