package net.sf.regadb.db;


/**
 * PatientDataset generated by hbm2java
 */
public class PatientDataset implements java.io.Serializable {

    // Fields    

    private PatientDatasetId id;

    // Constructors

    /** default constructor */
    public PatientDataset() {
    }

    /** full constructor */
    public PatientDataset(PatientDatasetId id) {
        this.id = id;
    }

    // Property accessors
    public PatientDatasetId getId() {
        return this.id;
    }

    public void setId(PatientDatasetId id) {
        this.id = id;
    }

}