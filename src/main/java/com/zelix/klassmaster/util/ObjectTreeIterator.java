package com.zelix.klassmaster.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectTreeIterator implements Iterator {
    private ObjectTreeNode pendingNext;
    private ObjectTreeNode lastReturned;
    private boolean removedLast;
    public final GenericTree outerTree;
    private GenericTree tree;

    @Override
    public void remove() {
        if (this.lastReturned != null && !this.removedLast) {
            this.removedLast = true;
            this.pendingNext = null;
            this.pendingNext = this.findNextAfterSubtree(this.lastReturned);
            GenericTree.removeNodeFrom(this.tree, this.lastReturned);
            this.lastReturned = null;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Object next() {
        return this.nextNode();
    }

    public ObjectTreeIterator(GenericTree genericTree, GenericTree genericTree1) {
        this.outerTree = genericTree;
        this.tree = genericTree1;
    }

    public ObjectTreeNode nextNode() {
        this.lastReturned = this.computeNext();
        this.removedLast = false;
        this.pendingNext = null;
        if (this.lastReturned == null) {
            throw new NoSuchElementException();
        } else {
            return this.lastReturned;
        }
    }

    private ObjectTreeNode findNextAfterSubtree(ObjectTreeNode objectTreeNode) {
        ObjectTreeNode objectTreeNode1 = ObjectTreeNode.accessParent(objectTreeNode);
        if (objectTreeNode1 == null) {
            return null;
        }

        int ba = ObjectTreeNode.getChildrenOf(objectTreeNode1).indexOf(objectTreeNode);
        return ba < ObjectTreeNode.getChildrenOf(objectTreeNode1).size() - 1
                ? (ObjectTreeNode) ObjectTreeNode.getChildrenOf(objectTreeNode1).get(ba + 1)
                : this.findNextAfterSubtree(objectTreeNode1);
    }

    private ObjectTreeNode computeNext() {
        if (this.removedLast || this.pendingNext != null) {
            return this.pendingNext;
        } else if (this.lastReturned == null) {
            return GenericTree.getRootOf(this.tree) != null ? GenericTree.getRootOf(this.tree) : null;
        } else {
            return ObjectTreeNode.getChildrenOf(this.lastReturned).size() > 0
                    ? (ObjectTreeNode) ObjectTreeNode.getChildrenOf(this.lastReturned).get(0)
                    : this.findNextAfterSubtree(this.lastReturned);
        }
    }

    @Override
    public boolean hasNext() {
        this.pendingNext = this.computeNext();
        return this.pendingNext != null;
    }
}
