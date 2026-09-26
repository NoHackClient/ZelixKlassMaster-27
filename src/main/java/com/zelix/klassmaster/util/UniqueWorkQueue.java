package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmRuntimeException;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;

public class UniqueWorkQueue {
    private int head = 0;
    private int tail = 0;
    private int count = 0;
    private Object[] buffer;
    private Set members;

    public boolean contains(Object object) {
        return this.members.contains(object);
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public boolean enqueue(Object object) {
        if (!this.members.add(object)) {
            return false;
        }

        int ba = this.buffer.length;
        if (this.count == ba) {
            this.grow();
            ba = this.buffer.length;
        }

        this.buffer[this.tail] = object;
        this.tail = (this.tail + 1) % ba;
        this.count++;
        return true;
    }

    public UniqueWorkQueue() {
        this(10);
    }

    public void enqueueAllElements(Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            this.enqueue(enumeration.nextElement());
        }
    }

    public void grow() {
        int ba = this.buffer.length;
        Object[] objects = new Object[ba * 2];

        for (int i = 0; i < this.count; i++) {
            int bc = (this.head + i) % ba;
            objects[i] = this.buffer[bc];
        }

        this.head = 0;
        this.tail = this.count;
        this.buffer = objects;
    }

    @Override
    public int hashCode() {
        int ba = 1;
        int bb = this.buffer.length;

        for (int i = 0; i < this.count; i++) {
            int bd = (this.head + i) % bb;
            ba = 31 * ba + this.buffer[bd].hashCode();
        }

        return ba;
    }

    public void enqueueAll(Collection collection1) {
        for (Object object : collection1) {
            this.enqueue(object);
        }
    }

    public UniqueWorkQueue(int ba) {
        if (ba <= 1) {
            throw new IllegalArgumentException("Queue size must be greater than 1 : " + ba);
        }

        this.buffer = new Object[ba];
        this.members = ZkmUtils.createHashSet(ZkmUtils.getPrimeCapacity(ba));
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof UniqueWorkQueue) {
            UniqueWorkQueue uniqueWorkQueue1 = (UniqueWorkQueue) object;
            if (this.count != uniqueWorkQueue1.count) {
                return false;
            }

            int ba = this.buffer.length;
            int bb = uniqueWorkQueue1.buffer.length;

            for (int i = 0; i < this.count; i++) {
                int bd = (this.head + i) % ba;
                int be = (uniqueWorkQueue1.head + i) % bb;
                if (!this.buffer[bd].equals(uniqueWorkQueue1.buffer[be])) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public Object dequeue() {
        if (this.count == 0) {
            throw new ZkmRuntimeException("Empty Queue");
        }

        int ba = this.buffer.length;
        Object object = this.buffer[this.head];
        this.head = (this.head + 1) % ba;
        this.count--;
        this.members.remove(object);
        return object;
    }

    public int size() {
        return this.count;
    }
}
