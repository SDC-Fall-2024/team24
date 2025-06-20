package com.team24.badgr;

import java.util.List;

public class BadgrFunction implements BadgrCallable {
    private final Environment closure;
    private final Statement.Function declaration;
    BadgrFunction(Statement.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter,
                        List<Object> arguments) {
        Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < declaration.params.size(); i++) {
        environment.define(declaration.params.get(i).getText(),
            arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            return returnValue.value;
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.getText() + ">";
    }
}

