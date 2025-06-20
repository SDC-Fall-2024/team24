package com.team24.badgr;

import java.util.List;

interface BadgrCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}