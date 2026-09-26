package com.zelix.klassmaster.ui.component;

import java.util.StringTokenizer;
import java.util.Vector;

public class LayoutFunctionTerm implements LayoutExpressionNode, LayoutConstraintMarker {
    public boolean resolved;
    public boolean parsed;
    public String functionName;
    public int value;
    public Vector arguments = new Vector();
    public ConstraintLayout layout;
    public String functionText;

    @Override
    public boolean isContainerLeadingEdge() {
        return false;
    }

    @Override
    public String prepare() {
        this.reset();
        String string = null;
        if (!this.parsed) {
            string = this.parseArguments();
        }

        if (string != null) {
            return string;
        }

        if (this.functionName.equals("max")) {
            this.value = Integer.MIN_VALUE;
        } else {
            this.value = Integer.MAX_VALUE;
        }

        return null;
    }

    public LayoutFunctionTerm(ConstraintLayout constraintLayout1, String string) {
        this.layout = constraintLayout1;
        this.functionText = string.trim();
    }

    @Override
    public void reset() {
        this.resolved = false;
        int ba = 0;
        int bb = 0;

        for (Vector vector = this.arguments; bb < vector.size(); vector = this.arguments) {
            ((LayoutExpression) this.arguments.elementAt(ba)).reset();
            bb = ++ba;
        }
    }

    @Override
    public boolean isContainerTrailingEdge() {
        return false;
    }

    public String parseArguments() {
        this.parsed = true;
        StringTokenizer stringTokenizer = new StringTokenizer(this.functionText, "()");
        this.functionName = stringTokenizer.nextToken().trim();
        String string = stringTokenizer.nextToken();
        StringTokenizer stringTokenizer1 = new StringTokenizer(string, ",");

        while (stringTokenizer1.hasMoreTokens()) {
            String string1 = stringTokenizer1.nextToken();
            LayoutExpression layoutExpression = new LayoutExpression(this.layout, string1);
            String string2 = layoutExpression.prepare();
            if (string2 != null) {
                return string2;
            }

            if (layoutExpression.isContainerRelative()) {
                return "Parse Error: Functions cannot take container properties as operands '" + this.functionText + "'";
            }

            this.arguments.addElement(layoutExpression);
        }

        return null;
    }

    @Override
    public boolean isContainerRelative() {
        return false;
    }

    public static boolean isFunctionCall(String string) {
        if (!string.startsWith("max") && !string.startsWith("min")) {
            return false;
        }

        char ba = string.charAt(3);
        return ba == '(' || ba == ' ';
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public boolean isContainerSizeRelative() {
        return false;
    }

    @Override
    public boolean tryEvaluate(Object object) {
        boolean bl = (Boolean) object;
        if (this.resolved) {
            return true;
        }

        int ba = 0;
        int bc = ba;

        for (Vector vector1 = this.arguments; bc < vector1.size(); vector1 = this.arguments) {
            LayoutExpression layoutExpression = (LayoutExpression) this.arguments.elementAt(ba);
            if (!layoutExpression.tryEvaluate(bl)) {
                return false;
            }

            bc = ++ba;
        }

        ba = 0;
        bc = 0;

        for (Vector vector = this.arguments; bc < vector.size(); vector = this.arguments) {
            LayoutExpression layoutExpression1 = (LayoutExpression) this.arguments.elementAt(ba);
            int value = layoutExpression1.getValue();
            if (this.functionName.equals("max")) {
                this.value = Math.max(value, this.value);
            } else {
                this.value = Math.min(value, this.value);
            }

            bc = ++ba;
        }

        this.resolved = true;
        return true;
    }
}
