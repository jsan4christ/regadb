package net.sf.regadb.db;

// Generated 18/05/2007 15:38:06 by Hibernate Tools 3.2.0.beta8

/**
 * TherapyDrugs generated by hbm2java
 */
public class TherapyDrugs implements java.io.Serializable {

    // Fields    

    private TherapyDrugsId id;

    // Constructors

    /** default constructor */
    public TherapyDrugs() {
    }

    /** full constructor */
    public TherapyDrugs(TherapyDrugsId id) {
        this.id = id;
    }

    // Property accessors
    public TherapyDrugsId getId() {
        return this.id;
    }

    public void setId(TherapyDrugsId id) {
        this.id = id;
    }

}
