package net.sf.regadb.db;

// Generated 18/05/2007 15:38:06 by Hibernate Tools 3.2.0.beta8

/**
 * TherapyCommercial generated by hbm2java
 */
public class TherapyCommercial implements java.io.Serializable {

    // Fields    

    private TherapyCommercialId id;

    private int version;

    private Double dayDosageUnits;

    // Constructors

    /** default constructor */
    public TherapyCommercial() {
    }

    /** minimal constructor */
    public TherapyCommercial(TherapyCommercialId id) {
        this.id = id;
    }

    /** full constructor */
    public TherapyCommercial(TherapyCommercialId id, Double dayDosageUnits) {
        this.id = id;
        this.dayDosageUnits = dayDosageUnits;
    }

    // Property accessors
    public TherapyCommercialId getId() {
        return this.id;
    }

    public void setId(TherapyCommercialId id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Double getDayDosageUnits() {
        return this.dayDosageUnits;
    }

    public void setDayDosageUnits(Double dayDosageUnits) {
        this.dayDosageUnits = dayDosageUnits;
    }

}
