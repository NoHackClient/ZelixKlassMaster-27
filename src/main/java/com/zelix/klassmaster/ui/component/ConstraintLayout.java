package com.zelix.klassmaster.ui.component;

import com.zelix.klassmaster.util.ZkmUtils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class ConstraintLayout implements LayoutManager2, LayoutConstraintMarker {
    private static int[] controlFlags;
    public static final String lineSeparator;
    public Integer defaultBottomPad;
    public Integer layoutHeight;
    public String[] constraintLines;
    public boolean layoutSizesResolved;
    public Integer minHeight;
    public Map variables;
    public boolean resolving;
    public boolean resolvePassStarted;
    public Integer layoutWidth;
    public Integer defaultRightPad;
    public Integer minWidth;
    public Map componentsByName = ZkmUtils.createHashMap();
    public Map specsByComponent = ZkmUtils.createHashMap();
    public LayoutExpression[] layoutSizeExpressions = new LayoutExpression[4];
    public Container container;

    @Override
    public void removeLayoutComponent(Component component1) {
        synchronized (component1.getTreeLock()) {
            LayoutComponentSpec layoutComponentSpec = (LayoutComponentSpec) this.specsByComponent.remove(component1);
            if (layoutComponentSpec != null) {
                this.componentsByName.remove(layoutComponentSpec.getComponentName());
            }
        }
    }

    public void setConstraints(String[] strings) {
        this.constraintLines = strings;
        this.variables = ZkmUtils.createHashMap();
        this.layoutWidth = null;
        this.layoutHeight = null;
        this.minWidth = null;
        this.minHeight = null;
        this.defaultRightPad = null;
        this.defaultBottomPad = null;
        int ba = 0;
        int bg = 0;

        for (String[] strings2 = this.constraintLines; bg < strings2.length; strings2 = this.constraintLines) {
            StringTokenizer stringTokenizer = new StringTokenizer(this.constraintLines[ba], "=");
            int bb = stringTokenizer.countTokens();
            String[] strings1 = new String[bb];

            for (int i = 0; i < bb; i++) {
                strings1[i] = stringTokenizer.nextToken().trim();
            }

            String string3 = strings1[bb - 1];

            for (int i = 0; i < bb - 1; i++) {
                String string = strings1[i];
                int be = string.indexOf(".");
                if (be == -1) {
                    this.variables.put(string, new LayoutExpression(this, string3));
                } else {
                    if (be == string.length() - 1 || string.lastIndexOf(".") != be) {
                        this.reportError("Property '" + string + "' is invalid. : '" + this.constraintLines[ba] + "'");
                    }

                    String string1 = string.substring(0, be).trim();
                    String string2 = string.substring(be + 1).trim();
                    if (string1.equals("container")) {
                        this.setContainerProperty(string2, string3);
                    } else if (string1.equals("layout")) {
                        this.setLayoutProperty(string2, string3);
                    } else {
                        Component component1 = (Component) this.componentsByName.get(string1);
                        ((LayoutComponentSpec) this.specsByComponent.get(component1)).setProperty(string2, string3);
                    }
                }
            }

            bg = ++ba;
        }

        for (int i = 0; i < this.layoutSizeExpressions.length; i++) {
            if (this.layoutSizeExpressions[i] != null) {
                this.layoutSizeExpressions[i].prepare();
            }
        }

        Iterator iterator = this.specsByComponent.values().iterator();

        while (iterator.hasNext()) {
            LayoutComponentSpec layoutComponentSpec = (LayoutComponentSpec) iterator.next();
            layoutComponentSpec.validateExpressions();
        }
    }

    @Override
    public void addLayoutComponent(String string, Component component1) {
        synchronized (component1.getTreeLock()) {
            Component component2 = ((java.awt.Component) (this.componentsByName.put(string, component1)));
            Map map1;
            if (component2 != null) {
                this.reportError("Multiple components with the name '" + string + "': " + component2 + " : " + component1);
                map1 = this.specsByComponent;
            } else {
                map1 = this.specsByComponent;
            }

            map1.put(component1, new LayoutComponentSpec(string, component1, this));
        }
    }

    @Override
    public void addLayoutComponent(Component component1, Object object) {
        synchronized (component1.getTreeLock()) {
            if (!(object instanceof String)) {
                throw new IllegalArgumentException("'addLayoutComponent' constraint must be a name String");
            }

            String string = (String) object;
            Component component2 = ((java.awt.Component) (this.componentsByName.put(string, component1)));
            Map map1;
            if (component2 != null) {
                this.reportError("Multiple components with the name '" + string + "': " + component2 + " : " + component1);
                map1 = this.specsByComponent;
            } else {
                map1 = this.specsByComponent;
            }

            map1.put(component1, new LayoutComponentSpec(string, component1, this));
        }
    }


    static {
        setControlFlags();
        lineSeparator = ZkmUtils.LINE_SEPARATOR;
    }


    public String setContainerProperty(String string, String string1) {
        int ba;
        try {
            ba = Integer.parseInt(string1);
        } catch (NumberFormatException numberFormatException) {
            return "Only an integer can be assigned to 'container." + string + "'";
        }

        if (string.equals("defaultRightPad")) {
            this.defaultRightPad = ba;
        } else {
            if (!string.equals("defaultBottomPad")) {
                return "Property '" + string + "' is not valid for the '" + "container" + "' object.";
            }

            this.defaultBottomPad = ba;
        }

        return null;
    }

    @Override
    public float getLayoutAlignmentY(Container container1) {
        return 0.5F;
    }

    public LayoutExpression getVariable(Object object) {
        return (LayoutExpression) this.variables.get(object);
    }

    public void resolveComponents(Container container1, ArrayList arrayList, boolean bl) {
        this.resolvePassStarted = true;
        this.resolving = true;
        Insets insets = container1.getInsets();
        int componentCount = container1.getComponentCount();

        for (int i = 0; i < componentCount; i++) {
            arrayList.add(container1.getComponent(i));
        }

        boolean bl1 = true;

        while (bl1) {
            bl1 = false;
            ArrayList arrayList1 = ZkmUtils.copyArrayList(arrayList);

            for (int i = 0; i < arrayList1.size(); i++) {
                Component component1 = (Component) arrayList1.get(i);
                LayoutComponentSpec layoutComponentSpec = (LayoutComponentSpec) this.specsByComponent.get(component1);
                if (layoutComponentSpec != null) {
                    if (layoutComponentSpec.resolve(bl)) {
                        bl1 = true;
                    }

                    if (layoutComponentSpec.areExpressionsResolved(bl) && layoutComponentSpec.isBoundsResolved()) {
                        arrayList.remove(component1);
                        if (bl) {
                            Dimension dimension = layoutComponentSpec.getSize();
                            Point point = layoutComponentSpec.getLocation();
                            component1.setBounds(point.x + insets.left, point.y + insets.top, dimension.width, dimension.height);
                        }
                    }
                } else {
                    arrayList.remove(component1);
                }
            }
        }

        this.resolving = false;
    }

    public void parseConstraints(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ";");
        String[] strings = new String[stringTokenizer.countTokens()];

        for (int i = 0; i < strings.length; i++) {
            strings[i] = stringTokenizer.nextToken().trim();
        }

        this.setConstraints(strings);
    }

    public static String getPropertyName(int ba) {
        switch (ba) {
            case 0:
                return "width";
            case 1:
                return "height";
            case 2:
                return "top";
            case 3:
                return "left";
            case 4:
                return "bottom";
            case 5:
                return "right";
            case 6:
                return "centerX";
            case 7:
                return "centerY";
            case 8:
                return "minWidth";
            case 9:
                return "minHeight";
            case 10:
                return "defaultWidth";
            case 11:
                return "defaultHeight";
            default:
                return null;
        }
    }

    public static void setControlFlags() {
        controlFlags = null;
    }

    @Override
    public Dimension maximumLayoutSize(Container container1) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void layoutContainer(Container container1) {
        synchronized (container1.getTreeLock()) {
            this.resolveLayoutSizes();
            if (this.constraintLines == null) {
            }

            this.resetComponentSpecs();
            ArrayList arrayList = new ArrayList(container1.getComponentCount());
            this.resolveComponents(container1, arrayList, true);
            if (arrayList.size() > 0) {
                StringBuffer stringBuffer = new StringBuffer();

                for (int i = 0; i < arrayList.size(); i++) {
                    LayoutComponentSpec layoutComponentSpec = (LayoutComponentSpec) this.specsByComponent.get(arrayList.get(i));
                    stringBuffer.append(lineSeparator + layoutComponentSpec.toDebugString());
                }

                this.reportError("Constraints could not be resolved: " + stringBuffer.toString());
            }
        }
    }

    public Dimension computeLayoutSize(Container container1, Integer integer, Integer integer1) {
        if (this.constraintLines == null) {
        }

        this.resetComponentSpecs();
        Insets insets = container1.getInsets();
        int ba = -1;
        int bb = -1;
        if (integer != null) {
            ba = integer;
        }

        if (integer1 != null) {
            bb = integer1;
        }

        int bj;
        if (ba != -1) {
            if (bb != -1) {
                return new Dimension(ba + insets.left + insets.right, bb + insets.top + insets.bottom);
            }

            bj = container1.getComponentCount();
        } else {
            bj = container1.getComponentCount();
        }

        int bc = bj;
        ArrayList arrayList = new ArrayList(bc);
        this.resolveComponents(container1, arrayList, false);

        boolean bl;
        do {
            bl = false;

            for (int i = 0; i < arrayList.size(); i++) {
                Component component1 = (Component) arrayList.get(i);
                LayoutComponentSpec layoutComponentSpec = (LayoutComponentSpec) this.specsByComponent.get(component1);
                bl = layoutComponentSpec.resolvePreferredSize() ? true : bl;
            }
        } while (bl);

        int bg = 0;
        if (this.defaultRightPad != null) {
            bg = this.defaultRightPad;
        }

        int bh = 0;
        if (this.defaultBottomPad != null) {
            bh = this.defaultBottomPad;
        }

        int bi = 0;
        int be = 0;

        for (int i = 0; i < bc; i++) {
            Component component2 = container1.getComponent(i);
            LayoutComponentSpec layoutComponentSpec1 = (LayoutComponentSpec) this.specsByComponent.get(component2);
            if (!layoutComponentSpec1.isBoundsResolved()) {
                this.reportError("Constraints could not be resolved while determining preferred size. '" + layoutComponentSpec1 + "'");
            }

            Point point = layoutComponentSpec1.getBottomRight();
            point.x = point.x + Math.max(layoutComponentSpec1.getRightPad(), bg);
            point.y = point.y + Math.max(layoutComponentSpec1.getBottomPad(), bh);
            if (point.x > bi) {
                bi = point.x;
            }

            if (point.y > be) {
                be = point.y;
            }
        }

        if (ba == -1) {
            ba = bi;
        }

        if (bb == -1) {
            bb = be;
        }

        if (this.minWidth != null) {
            ba = Math.max(ba, this.minWidth);
        }

        if (this.minHeight != null) {
            bb = Math.max(bb, this.minHeight);
        }

        return new Dimension(ba + insets.left + insets.right, bb + insets.top + insets.bottom);
    }

    @Override
    public float getLayoutAlignmentX(Container container1) {
        return 0.5F;
    }

    public String setLayoutProperty(String string, String string1) {
        int ba;
        try {
            ba = Integer.parseInt(string1);
        } catch (NumberFormatException numberFormatException) {
            if (string.equals("width")) {
                this.layoutSizeExpressions[0] = new LayoutExpression(this, string1);
            } else if (string.equals("height")) {
                this.layoutSizeExpressions[1] = new LayoutExpression(this, string1);
            } else if (string.equals("minWidth")) {
                this.layoutSizeExpressions[2] = new LayoutExpression(this, string1);
            } else {
                if (!string.equals("minHeight")) {
                    return "Property '" + string + "' is not valid for the '" + "layout" + "' object.";
                }

                this.layoutSizeExpressions[3] = new LayoutExpression(this, string1);
            }

            return null;
        }

        if (string.equals("width")) {
            this.layoutWidth = ba;
        } else if (string.equals("height")) {
            this.layoutHeight = ba;
        } else if (string.equals("minWidth")) {
            this.minWidth = ba;
        } else {
            if (!string.equals("minHeight")) {
                return "Property '" + string + "' is not valid for the '" + "layout" + "' object.";
            }

            this.minHeight = ba;
        }

        return null;
    }

    public static int getPropertyIndex(String string) {
        if (string.equals("width")) {
            return 0;
        } else if (string.equals("height")) {
            return 1;
        } else if (string.equals("top")) {
            return 2;
        } else if (string.equals("bottom")) {
            return 4;
        } else if (string.equals("left")) {
            return 3;
        } else if (string.equals("right")) {
            return 5;
        } else if (string.equals("centerX")) {
            return 6;
        } else if (string.equals("centerY")) {
            return 7;
        } else if (string.equals("minWidth")) {
            return 8;
        } else if (string.equals("minHeight")) {
            return 9;
        } else if (string.equals("defaultWidth")) {
            return 10;
        } else {
            return string.equals("defaultHeight") ? 11 : -1;
        }
    }

    public String getVersion() {
        return "1.1";
    }

    public Dimension getAvailableSize() {
        Insets insets = this.container.getInsets();
        Dimension dimension = this.container.getSize();
        dimension.width = dimension.width - (insets.left + insets.left);
        dimension.height = dimension.height - (insets.top + insets.bottom);
        if (this.minWidth != null) {
            dimension.width = Math.max(dimension.width, this.minWidth);
        }

        if (this.minHeight != null) {
            dimension.height = Math.max(dimension.height, this.minHeight);
        }

        return dimension;
    }

    @Override
    public void invalidateLayout(Container container1) {
        this.resetComponentSpecs();
    }

    public int getContainerValue(int ba) {
        Dimension dimension = this.getAvailableSize();
        switch (ba) {
            case 0:
            case 5:
                return dimension.width;
            case 1:
            case 4:
                return dimension.height;
            case 2:
            case 3:
                return 0;
            case 6:
                return dimension.width / 2;
            case 7:
                return dimension.height / 2;
            default:
                this.reportError("'container." + getPropertyName(ba) + "' is not a valid RHS property.");
                return -1;
        }
    }

    public static int[] getControlFlags() {
        return controlFlags;
    }

    public ConstraintLayout(Container container1) {
        this.container = container1;
    }

    public LayoutComponentSpec getComponentSpec(String string) {
        Object object = this.componentsByName.get(string);
        Map map1;
        if (object == null) {
            this.reportError("Component '" + string + "' has not been added to the container.");
            map1 = this.specsByComponent;
        } else {
            map1 = this.specsByComponent;
        }

        return (LayoutComponentSpec) map1.get(object);
    }

    public void resolveLayoutSizes() {
        if (!this.layoutSizesResolved) {
            int ba = 0;
            int bb = ba;

            for (LayoutExpression[] layoutExpressions = this.layoutSizeExpressions; bb < layoutExpressions.length; layoutExpressions = this.layoutSizeExpressions) {
                if (this.layoutSizeExpressions[ba] != null && this.layoutSizeExpressions[ba].tryEvaluate(false)) {
                    switch (ba) {
                        case 0:
                            this.layoutWidth = this.layoutSizeExpressions[ba].getValue();
                            break;
                        case 1:
                            this.layoutHeight = this.layoutSizeExpressions[ba].getValue();
                            break;
                        case 2:
                            this.minWidth = this.layoutSizeExpressions[ba].getValue();
                            break;
                        case 3:
                            this.minHeight = this.layoutSizeExpressions[ba].getValue();
                    }
                }

                bb = ++ba;
            }

            this.layoutSizesResolved = true;
        }
    }

    @Override
    public Dimension preferredLayoutSize(Container container1) {
        synchronized (container1.getTreeLock()) {
            this.resolveLayoutSizes();
            return this.computeLayoutSize(container1, this.layoutWidth, this.layoutHeight);
        }
    }

    public void reportError(String string) {
        String string1 = "PowerLayout " + this.getVersion() + " ERROR: " + lineSeparator + string;
        System.err.println(lineSeparator + string1);
        System.exit(1);
        throw new RuntimeException(string1);
    }

    @Override
    public Dimension minimumLayoutSize(Container container1) {
        synchronized (container1.getTreeLock()) {
            this.resolveLayoutSizes();
            Integer integer = this.minWidth != null ? this.minWidth : this.layoutWidth;
            Integer integer1 = this.minHeight != null ? this.minHeight : this.layoutHeight;
            return this.computeLayoutSize(container1, integer, integer1);
        }
    }

    public void resetComponentSpecs() {
        Iterator iterator = this.specsByComponent.values().iterator();

        while (iterator.hasNext()) {
            ((LayoutComponentSpec) iterator.next()).reset();
        }

        this.resolvePassStarted = false;
    }
}
