package net.sf.regadb.db;

// Generated 18/05/2007 15:38:06 by Hibernate Tools 3.2.0.beta8

/**
 * AaInsertionId generated by hbm2java
 */
public class AaInsertionId implements java.io.Serializable {

    // Fields    

    private short position;

    private AaSequence aaSequence;

    private short insertionOrder;

    // Constructors

    /** default constructor */
    public AaInsertionId() {
    }

    /** full constructor */
    public AaInsertionId(short position, AaSequence aaSequence,
            short insertionOrder) {
        this.position = position;
        this.aaSequence = aaSequence;
        this.insertionOrder = insertionOrder;
    }

    // Property accessors
    public short getPosition() {
        return this.position;
    }

    public void setPosition(short position) {
        this.position = position;
    }

    public AaSequence getAaSequence() {
        return this.aaSequence;
    }

    public void setAaSequence(AaSequence aaSequence) {
        this.aaSequence = aaSequence;
    }

    public short getInsertionOrder() {
        return this.insertionOrder;
    }

    public void setInsertionOrder(short insertionOrder) {
        this.insertionOrder = insertionOrder;
    }

}
