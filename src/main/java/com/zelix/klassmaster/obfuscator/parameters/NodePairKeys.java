package com.zelix.klassmaster.obfuscator.parameters;

public class NodePairKeys {
    public final LongKeyNode firstNode;
    public final LongKeyNode secondNode;
    public final Long firstNodeKey;
    public final Long firstRandomKey;
    public final Long secondRandomKey;

    public LongKeyNode getSecondNode() {
        return this.secondNode;
    }

    public LongKeyNode getFirstNode() {
        return this.firstNode;
    }

    public Long getFirstNodeKey() {
        return this.firstNodeKey;
    }

    public Long getSecondRandomKey() {
        return this.secondRandomKey;
    }

    public Long getFirstRandomKey() {
        return this.firstRandomKey;
    }

    public NodePairKeys(LongKeyNode longKeyNode, LongKeyNode longKeyNode1, Long long1, Long long2, Long long3) {
        this.firstNode = longKeyNode;
        this.secondNode = longKeyNode1;
        this.firstNodeKey = long1;
        this.firstRandomKey = long2;
        this.secondRandomKey = long3;
    }
}
