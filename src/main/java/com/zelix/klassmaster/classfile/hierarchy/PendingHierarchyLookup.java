package com.zelix.klassmaster.classfile.hierarchy;

public class PendingHierarchyLookup {
    public final ClassHierarchyNode node;
    public final NameReservationMark previousMark;
    public final NameReservationMark requestedMark;

    public PendingHierarchyLookup(ClassHierarchyNode classHierarchyNode, NameReservationMark nameReservationMark, NameReservationMark nameReservationMark1) {
        this.node = classHierarchyNode;
        this.previousMark = nameReservationMark;
        this.requestedMark = nameReservationMark1;
    }
}
