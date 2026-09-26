package com.zelix.klassmaster.obfuscator.exclude;

import com.zelix.klassmaster.classfile.ClassFileBase;
import com.zelix.klassmaster.classfile.hierarchy.ClassHierarchyQuery;
import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface MemberSpecifierMatcher extends DescribableSpec {
    boolean hasMatchingMember(ClassFileBase classFileBase, ClassHierarchyQuery classHierarchyQuery) throws ZkmException, IOException;
}
