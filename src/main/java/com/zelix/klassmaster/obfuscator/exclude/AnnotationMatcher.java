package com.zelix.klassmaster.obfuscator.exclude;

import java.util.Set;

public interface AnnotationMatcher extends DescribableSpec {
    boolean matchesAnyAnnotation(Set set1);
}
