package com.zelix.klassmaster.changelog;

import com.zelix.klassmaster.util.ZkmUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;

public class FrameCandidateSets {
    public ArrayList frameCandidates = new ArrayList();

    public void mergeWith(FrameCandidateSets frameCandidateSets1) {
        int ba = 0;
        int bb = 0;

        for (ArrayList arrayList = this.frameCandidates; bb < arrayList.size(); arrayList = this.frameCandidates) {
            ((HashSet) this.frameCandidates.get(ba)).addAll((Collection) frameCandidateSets1.frameCandidates.get(ba));
            bb = ++ba;
        }
    }

    public boolean isMergeableWith(FrameCandidateSets frameCandidateSets1) {
        ArrayList arrayList = frameCandidateSets1.frameCandidates;
        int ba = 0;
        int bb = 0;

        for (ArrayList arrayList1 = this.frameCandidates; bb < arrayList1.size(); arrayList1 = this.frameCandidates) {
            HashSet hashSet = (HashSet) this.frameCandidates.get(ba);
            HashSet hashSet1 = (HashSet) arrayList.get(ba);
            HashSet hashSet2 = null;
            HashSet hashSet3 = null;
            HashSet hashSet4 = null;
            HashSet hashSet5 = null;
            if (ba > 0) {
                hashSet2 = (HashSet) this.frameCandidates.get(ba - 1);
                hashSet3 = (HashSet) arrayList.get(ba - 1);
                bb = ba;
                arrayList1 = this.frameCandidates;
            } else {
                bb = ba;
                arrayList1 = this.frameCandidates;
            }

            if (bb < arrayList1.size() - 1) {
                hashSet4 = (HashSet) this.frameCandidates.get(ba + 1);
                hashSet5 = (HashSet) arrayList.get(ba + 1);
            }

            if (!hashSet.equals(hashSet1) && (hashSet2 != null && !hashSet2.equals(hashSet3) || hashSet4 != null && !hashSet4.equals(hashSet5))) {
                return false;
            }

            bb = ++ba;
        }

        return true;
    }

    public int getFrameCount() {
        return this.frameCandidates.size();
    }

    public Enumeration enumerateFrameCandidates() {
        return Collections.enumeration(this.frameCandidates);
    }

    public FrameCandidateSets(StackTraceCallPath stackTraceCallPath) {
        Enumeration enumeration = stackTraceCallPath.enumerateFrames();

        while (enumeration.hasMoreElements()) {
            HashSet hashSet = ZkmUtils.createHashSet();
            hashSet.add(enumeration.nextElement());
            this.frameCandidates.add(hashSet);
        }
    }
}
