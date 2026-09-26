package com.zelix.klassmaster.proguard.config.parser;

import com.zelix.klassmaster.exceptions.ZkmException;
import com.zelix.klassmaster.proguard.ProGuardConfigTranslator;

import java.io.IOException;

public class ProGuardConfigSimpleNode implements ProGuardConfigNode {
    private static int initMarker;
    public ProGuardConfigNode parent;
    public ProGuardConfigNode[] children;
    public int id;

    @Override
    public void jjtAddChild(ProGuardConfigNode proGuardConfigNode, int ba) {
        ProGuardConfigNode[] proGuardConfigNodes1;
        if (this.children == null) {
            this.children = new ProGuardConfigNode[ba + 1];
            proGuardConfigNodes1 = this.children;
        } else if (ba >= this.children.length) {
            ProGuardConfigNode[] proGuardConfigNodes = new ProGuardConfigNode[ba + 1];
            System.arraycopy(this.children, 0, proGuardConfigNodes, 0, this.children.length);
            this.children = proGuardConfigNodes;
            proGuardConfigNodes1 = this.children;
        } else {
            proGuardConfigNodes1 = this.children;
        }

        proGuardConfigNodes1[ba] = proGuardConfigNode;
    }

    @Override
    public void jjtOpen() {
    }

    @Override
    public void jjtSetParent(ProGuardConfigNode proGuardConfigNode) {
        this.parent = proGuardConfigNode;
    }

    public static int getMarkerSeed() {
        return 53;
    }

    public static void setInitMarker() {
        initMarker = 125;
    }

    public ProGuardConfigSimpleNode(int id) {
        this.id = id;
    }

    @Override
    public ProGuardConfigNode jjtGetChild(int ba) {
        return this.children[ba];
    }

    @Override
    public int jjtGetNumChildren() {
        return this.children == null ? 0 : this.children.length;
    }

    public static int getInitMarker() {
        return initMarker;
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public void translate(Object object1, Object object) throws ZkmException, IOException {
        ProGuardConfigTranslator proGuardConfigTranslator = (ProGuardConfigTranslator) object;
        int ba = this.jjtGetNumChildren();

        for (int i = 0; i < ba; i++) {
            this.jjtGetChild(i).translate(this, proGuardConfigTranslator);
        }
    }

    @Override
    public ProGuardConfigNode jjtGetParent() {
        return this.parent;
    }

    static {
        if (getMarkerSeed() == 0) {
            setInitMarker();
        }
    }
}
