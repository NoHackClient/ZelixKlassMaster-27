package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingNameHolder;
import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

import java.util.ArrayList;
import java.util.List;

public class ProGuardMappingASTNameList extends ProGuardMappingSimpleNode implements ProGuardMappingNameHolder {
    public List nameParts = new ArrayList();

    @Override
    public void acceptName(Object object) {
        this.nameParts.add(object);
    }

    public ProGuardMappingASTNameList() {
        super(10);
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        ProGuardMappingNameHolder proGuardMappingNameHolder = (ProGuardMappingNameHolder) proGuardMappingNode;
        int ba = this.nameParts.size();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < ba; i++) {
            stringBuilder.append((String) this.nameParts.get(i));
            if (i < ba - 1) {
                stringBuilder.append('.');
            }
        }

        proGuardMappingNameHolder.acceptName(stringBuilder.toString());
    }

    static {
        try {
            staticInit();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void staticInit() {
    }
}
