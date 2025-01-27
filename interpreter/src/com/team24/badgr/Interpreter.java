package com.team24.badgr;

import java.util.List;

class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void>{
    @Override
    public Object visitLiteralExpression(Expression.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expr) {
     return evaluate(expr.expression);
    }

    private Object evaluate(Expression expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.getType()) {
        case MINUS:
            checkNumberOperand(expr.operator, right);
            return -(double)right;
        case NOT:
            return !isTruthy(right);
        default:
            break;
        }

        // Unreachable.
        return null;
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right); 

        switch (expr.operator.getType()) {
        case PLUS:
            if (left instanceof Double && right instanceof Double) {
                return (double)left + (double)right;
            }

            if (left instanceof String && right instanceof String) {
                return (String)left + (String)right;
            }
            throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
        case MINUS:
            checkNumberOperands(expr.operator, left, right);
            return (double)left - (double)right;
        case FSLASH:
            checkNumberOperands(expr.operator, left, right);   
            return (double)left / (double)right;
        case STAR:
            checkNumberOperands(expr.operator, left, right);
            return (double)left * (double)right;
        case GT:
            checkNumberOperands(expr.operator, left, right);
            return (double)left > (double)right;
        case GEQ:
            checkNumberOperands(expr.operator, left, right);
            return (double)left >= (double)right;
        case LT:
            checkNumberOperands(expr.operator, left, right);
            return (double)left < (double)right;
        case LEQ:
            checkNumberOperands(expr.operator, left, right);
            return (double)left <= (double)right;
        case EQ:
            checkNumberOperands(expr.operator, left, right);
            return isEqual(left, right);
        case NEQ:
            checkNumberOperands(expr.operator, left, right);
            return !isEqual(left, right);
        default:
            break;
        }

        // Unreachable.
        return null;
    }

    @Override
    public Void visitExprStatement(Statement.Expr stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
    
        return a.equals(b);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    void interpret(List<Statement> statements) { 
        try {
          for(Statement stmt : statements) {
            Object value = stmt.accept(this);
            System.out.println(stringify(value));
         }
        } catch (RuntimeError error) {
          App.runtimeError(error);
        }
      }

      private String stringify(Object object) {
        if (object == null) return "nil";
    
        if (object instanceof Double) {
          String text = object.toString();
          if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
          }
          return text;
        }
    
        return object.toString();
    }

}