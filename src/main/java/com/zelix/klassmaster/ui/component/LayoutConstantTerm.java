package com.zelix.klassmaster.ui.component;

public class LayoutConstantTerm implements LayoutExpressionNode, LayoutConstraintMarker {
    public ConstraintLayout layout;
    public int propertyIndex;

    @Override
    public boolean isContainerSizeRelative() {
        return this.propertyIndex == 0 || this.propertyIndex == 1 || this.propertyIndex == 6 || this.propertyIndex == 7;
    }

    @Override
    public boolean tryEvaluate(Object object) {
        boolean bl = (Boolean) object;
        switch (this.propertyIndex) {
            case 2:
            case 3:
                return true;
            default:
                return bl;
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public boolean isContainerLeadingEdge() {
        return this.propertyIndex == 3 || this.propertyIndex == 2;
    }

    public int getPropertyIndex() {
        return this.propertyIndex;
    }

    @Override
    public boolean isContainerTrailingEdge() {
        return this.propertyIndex == 5 || this.propertyIndex == 4;
    }

    @Override
    public String prepare() {
        return null;
    }

    public LayoutConstantTerm(ConstraintLayout constraintLayout1, int propertyIndex) {
        this.layout = constraintLayout1;
        this.propertyIndex = propertyIndex;
    }

    @Override
    public int getValue() {
        return this.layout.getContainerValue(this.propertyIndex);
    }

    @Override
    public boolean isContainerRelative() {
        return true;
    }
}
