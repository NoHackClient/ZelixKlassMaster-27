package com.zelix.klassmaster.obfuscator.parameters;

import com.zelix.klassmaster.util.BooleanFlag;
import com.zelix.klassmaster.util.PairMultiMap;
import com.zelix.klassmaster.util.ZkmUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RootKeyNode implements EncryptionKeyChain {
    public EncryptionKeyChain next;
    public final Random random;
    public final Map keyChainMap;
    public final ParameterKeyGenerator keyGenerator;
    public PairMultiMap keyPairMap;

    @Override
    public boolean isOrderedBefore(EncryptionKeyChain encryptionKeyChain) {
        if (this == encryptionKeyChain) {
            return true;
        } else {
            return encryptionKeyChain instanceof RootKeyNode ? System.identityHashCode(this) - System.identityHashCode(encryptionKeyChain) <= 0 : false;
        }
    }

    public Map getKeyChainMap() {
        return Collections.unmodifiableMap(this.keyChainMap);
    }

    public static RootKeyNode create(Random random1, int ba, List list1, ParameterKeyGenerator parameterKeyGenerator) {
        return new RootKeyNode(random1, ba, list1, parameterKeyGenerator);
    }

    @Override
    public long getMixedKey() {
        return 0L;
    }

    public long generateRandomKey() {
        int ba = this.random.nextInt(256);
        long bb = this.keyGenerator.nextMiddleValue();
        int bc = this.random.nextInt(256);
        return MethodParameterChanger.composeKey(ba, bb, bc, this.keyGenerator.getCurrentLayout());
    }

    @Override
    public boolean isKeyed() {
        return false;
    }

    @Override
    public long getCurrentKey() {
        return 0L;
    }

    @Override
    public void appendToChain(Object object) {
        EncryptionKeyChain encryptionKeyChain = (EncryptionKeyChain) object;
        if (this != encryptionKeyChain) {
            if (this.next == null) {
                this.next = encryptionKeyChain;
            } else {
                this.next.appendToChain(encryptionKeyChain);
            }
        }
    }

    public PairMultiMap getKeyPairMap() {
        return this.keyPairMap;
    }

    private RootKeyNode(Random random1, int ba, List list1, ParameterKeyGenerator parameterKeyGenerator) {
        this.random = random1;
        this.keyChainMap = ZkmUtils.createHashMap(ZkmUtils.getPrimeCapacity(ba));
        this.keyGenerator = parameterKeyGenerator;
        boolean bl = false;
        this.keyPairMap = new PairMultiMap(ba);
        BooleanFlag booleanFlag = new BooleanFlag(false);

        for (int i = 0; !booleanFlag.getValue(); i++) {
            long bc = this.generateRandomKey();
            LongKeyNode longKeyNode = (LongKeyNode) parameterKeyGenerator.getOrCreateNode(bc, booleanFlag);
            list1.add(bc);
            EncryptionKeyChain encryptionKeyChain;
            PairMultiMap pairMultiMap;
            if (!bl && random1.nextInt(ba / 2) == 0) {
                encryptionKeyChain = this;
                bl = true;
                list1.add(this);
                pairMultiMap = this.keyPairMap;
            } else {
                long bd = this.generateRandomKey();
                encryptionKeyChain = parameterKeyGenerator.getOrCreateNode(bd, booleanFlag);
                list1.add(bd);
                pairMultiMap = this.keyPairMap;
            }

            pairMultiMap.addPair(longKeyNode.toKeyString(), longKeyNode, encryptionKeyChain);
            this.keyChainMap.put(longKeyNode, encryptionKeyChain);
        }
    }

    @Override
    public long pushKey(long ba) {
        return ba;
    }
}
