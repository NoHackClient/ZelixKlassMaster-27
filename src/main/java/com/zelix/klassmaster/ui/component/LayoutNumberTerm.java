package com.zelix.klassmaster.ui.component;

public class LayoutNumberTerm implements LayoutExpressionNode {
    public int value;

    @Override
    public boolean isContainerSizeRelative() {
        return false;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public boolean isContainerTrailingEdge() {
        return false;
    }

    @Override
    public boolean isContainerLeadingEdge() {
        return false;
    }

    public LayoutNumberTerm(int value) {
        this.value = value;
    }

    @Override
    public boolean isContainerRelative() {
        return false;
    }

    @Override
    public void reset() {
    }

    @Override
    public boolean tryEvaluate(Object object) {
        return true;
    }

    @Override
    public String prepare() {
        return null;
    }
}
