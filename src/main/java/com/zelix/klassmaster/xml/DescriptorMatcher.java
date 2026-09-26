package com.zelix.klassmaster.xml;

import com.zelix.klassmaster.exceptions.ZkmException;

import java.io.IOException;

public interface DescriptorMatcher {
    boolean matchesDescriptor(String string) throws ZkmException, IOException;
}
