package com.zelix.klassmaster.util;

import com.zelix.klassmaster.exceptions.ZkmRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class GenericTree {
    private Map nodeByValue = ZkmUtils.createHashMap();
    private ObjectTreeNode root;
    private ObjectTreeNode currentNode;

    public void addChild(Object object) {
        this.currentNode = ObjectTreeNode.addChildTo(this.currentNode, object);
        this.nodeByValue.put(object, this.currentNode);
    }

    public GenericTree(Object object) {
        this.root = new ObjectTreeNode(this, object);
        this.currentNode = this.root;
        this.nodeByValue.put(object, this.currentNode);
    }

    public Iterator nodeIterator() {
        return new ObjectTreeIterator(this, this);
    }

    public boolean isAtRoot() {
        return this.currentNode == this.root;
    }

    public void removeNode(ObjectTreeNode objectTreeNode) {
        if (objectTreeNode == null) {
            throw new IllegalArgumentException("Null node");
        }

        if (objectTreeNode == this.root) {
            throw new ZkmRuntimeException("Cannot remove root");
        }

        ObjectTreeNode objectTreeNode1 = objectTreeNode;

        while (objectTreeNode1 != null && objectTreeNode1 != this.root) {
            objectTreeNode1 = ObjectTreeNode.getParentOf(objectTreeNode1);
        }

        if (objectTreeNode1 != this.root) {
            throw new ZkmRuntimeException("Node not a member of Tree");
        }

        ObjectTreeNode.removeChildOf(ObjectTreeNode.getParentOf(objectTreeNode), objectTreeNode);
    }

    public void resetToRoot() {
        this.currentNode = this.root;
    }

    public static ObjectTreeNode getRootOf(GenericTree genericTree) {
        return genericTree.root;
    }

    public static void removeNodeFrom(GenericTree genericTree, ObjectTreeNode objectTreeNode) {
        genericTree.removeNode(objectTreeNode);
    }

    public void addSibling(Object object) {
        if (this.currentNode != this.root) {
            this.currentNode = ObjectTreeNode.addChildTo(ObjectTreeNode.getParentOf(this.currentNode), object);
            this.nodeByValue.put(object, this.currentNode);
        } else {
            throw new ZkmRuntimeException("Cannot add sibling to root");
        }
    }

    public void moveToParent() {
        ObjectTreeNode objectTreeNode = ObjectTreeNode.getParentOf(this.currentNode);
        if (objectTreeNode != null) {
            this.currentNode = objectTreeNode;
        } else {
            throw new ZkmRuntimeException("Root node has no parent");
        }
    }

    public boolean moveToValue(Object object) {
        ObjectTreeNode objectTreeNode = (ObjectTreeNode) this.nodeByValue.get(object);
        if (objectTreeNode != null) {
            this.currentNode = objectTreeNode;
            return true;
        } else {
            return false;
        }
    }

    public Enumeration values() {
        ArrayList arrayList = new ArrayList();
        Iterator iterator = this.nodeIterator();

        while (iterator.hasNext()) {
            arrayList.add(((ObjectTreeNode) iterator.next()).getValue());
        }

        return Collections.enumeration(arrayList);
    }
}
