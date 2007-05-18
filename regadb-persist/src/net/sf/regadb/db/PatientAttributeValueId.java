package net.sf.regadb.db;

// Generated 18/05/2007 15:38:06 by Hibernate Tools 3.2.0.beta8

/**
 * PatientAttributeValueId generated by hbm2java
 */
public class PatientAttributeValueId implements java.io.Serializable {

    // Fields    

    private PatientImpl patient;

    private Attribute attribute;

    // Constructors

    /** default constructor */
    public PatientAttributeValueId() {
    }

    /** full constructor */
    public PatientAttributeValueId(PatientImpl patient, Attribute attribute) {
        this.patient = patient;
        this.attribute = attribute;
    }

    // Property accessors
    public PatientImpl getPatient() {
        return this.patient;
    }

    public void setPatient(PatientImpl patient) {
        this.patient = patient;
    }

    public Attribute getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

}
