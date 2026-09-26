package com.zelix.klassmaster.ui.component;

public class LayoutConstraintRef implements LayoutExpressionNode, LayoutConstraintMarker {
    public LayoutComponentSpec componentSpec;
    public int propertyIndex;

    @Override
    public boolean isContainerRelative() {
        return false;
    }

    @Override
    public boolean isContainerTrailingEdge() {
        return false;
    }

    @Override
    public int getValue() {
        return this.componentSpec.getPropertyValue(this.propertyIndex);
    }

    @Override
    public boolean isContainerSizeRelative() {
        return false;
    }

    @Override
    public String prepare() {
        return null;
    }

    @Override
    public boolean tryEvaluate(Object object) {
        return this.componentSpec.isPropertyResolved(this.propertyIndex);
    }

    public LayoutConstraintRef(LayoutComponentSpec layoutComponentSpec, int propertyIndex) {
        this.componentSpec = layoutComponentSpec;
        this.propertyIndex = propertyIndex;
    }

    @Override
    public boolean isContainerLeadingEdge() {
        return false;
    }

    @Override
    public void reset() {
    }
}
