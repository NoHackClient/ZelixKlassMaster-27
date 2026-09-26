package com.zelix.klassmaster.classfile.attribute;

import com.zelix.klassmaster.classfile.constpool.ConstantPoolEntry;
import com.zelix.klassmaster.classfile.constpool.ResolvedStringConstant;

import java.util.ArrayList;
import java.util.List;

public class BootstrapMethodInfo {
    public String rewrittenRecipe;
    public final List recipeParts = new ArrayList();
    public final BootstrapMethodEntry bootstrapEntry;
    public final List recipeTokens;
    private final int placeholderCount;
    public final ResolvedStringConstant recipeConstant;
    private final String originalRecipe;
    public final ConstantPoolEntry[] constantArguments;

    public List getRecipeTokens() {
        return this.recipeTokens;
    }

    public List getRecipeParts() {
        return this.recipeParts;
    }

    public BootstrapMethodEntry getBootstrapEntry() {
        return this.bootstrapEntry;
    }

    public ConstantPoolEntry[] getConstantArguments() {
        return this.constantArguments;
    }

    public String getRewrittenRecipe() {
        return this.rewrittenRecipe;
    }

    public BootstrapMethodInfo(BootstrapMethodEntry bootstrapMethodEntry, List list1, int placeholderCount) {
        this.bootstrapEntry = bootstrapMethodEntry;
        this.recipeTokens = list1;
        this.placeholderCount = placeholderCount;
        ConstantPoolEntry[] constantPoolEntrys = this.bootstrapEntry.copyArguments();
        this.recipeConstant = (ResolvedStringConstant) constantPoolEntrys[0];
        this.originalRecipe = this.recipeConstant.getValueString();
        this.constantArguments = new ConstantPoolEntry[constantPoolEntrys.length - 1];
        System.arraycopy(constantPoolEntrys, 1, this.constantArguments, 0, this.constantArguments.length);
    }

    public void setRewrittenRecipe(String string) {
        this.rewrittenRecipe = string;
    }

    public void addRecipePart(Object object) {
        this.recipeParts.add(object);
    }

    public ResolvedStringConstant getRecipeConstant() {
        return this.recipeConstant;
    }
}
