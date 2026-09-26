package com.zelix.klassmaster.util;

import java.util.Stack;

public class ObjectStack {
    public Stack stack = new Stack();

    public Object peekAt(int ba) {
        return this.stack.get(this.stack.size() - 1 - ba);
    }

    public int size() {
        return this.stack.size();
    }

    public int search(Object object) {
        return this.stack.search(object);
    }

    public Object push(Object object) {
        this.stack.push(object);
        return object;
    }

    public boolean isEmpty() {
        return this.stack.empty();
    }

    public Object pop() {
        return this.stack.pop();
    }

    public Object peek() {
        return this.stack.peek();
    }
}
