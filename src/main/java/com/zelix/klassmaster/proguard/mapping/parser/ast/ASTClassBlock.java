package com.zelix.klassmaster.proguard.mapping.parser.ast;

import com.zelix.klassmaster.proguard.ProGuardMappingTranslator;
import com.zelix.klassmaster.proguard.ProGuardMemberChangeApplier;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingNode;
import com.zelix.klassmaster.proguard.mapping.parser.ProGuardMappingSimpleNode;

public class ASTClassBlock extends ProGuardMappingSimpleNode {
    public ASTClassBlock() {
        super(1);
    }

    @Override
    public void translate(Object object, ProGuardMappingTranslator proGuardMappingTranslator) {
        ProGuardMappingNode proGuardMappingNode = (ProGuardMappingNode) object;
        super.translate(proGuardMappingNode, proGuardMappingTranslator);
        int ba = this.jjtGetNumChildren();
        ASTClassNameChange aSTClassNameChange = (ASTClassNameChange) this.jjtGetChild(0);
        String string = aSTClassNameChange.getOldName();
        proGuardMappingTranslator.addClassMapping(aSTClassNameChange.getOldName(), aSTClassNameChange.getNewName());

        for (int i = 1; i < ba; i++) {
            ((ProGuardMemberChangeApplier) this.jjtGetChild(i)).applyMemberChange(string, proGuardMappingTranslator);
        }
    }
}
