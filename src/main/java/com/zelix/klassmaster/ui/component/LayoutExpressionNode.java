package com.zelix.klassmaster.ui.component;

public interface LayoutExpressionNode {
    boolean tryEvaluate(Object object);

    boolean isContainerRelative();

    String prepare();

    boolean isContainerLeadingEdge();

    void reset();

    boolean isContainerTrailingEdge();

    int getValue();

    boolean isContainerSizeRelative();
}
