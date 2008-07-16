package net.sf.regadb.db;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Therapy generated by hbm2java
 */
public class Therapy implements java.io.Serializable {

    // Fields    

    private Integer therapyIi;

    private int version;

    private TherapyMotivation therapyMotivation;

    private PatientImpl patient;

    private Genome genome;

    private Date startDate;

    private Date stopDate;

    private String comment;

    private Set<TherapyCommercial> therapyCommercials = new HashSet<TherapyCommercial>(
            0);

    private Set<TherapyGeneric> therapyGenerics = new HashSet<TherapyGeneric>(0);

    // Constructors

    /** default constructor */
    public Therapy() {
    }

    /** minimal constructor */
    public Therapy(PatientImpl patient, Date startDate) {
        this.patient = patient;
        this.startDate = startDate;
    }

    /** full constructor */
    public Therapy(TherapyMotivation therapyMotivation, PatientImpl patient,
            Genome genome, Date startDate, Date stopDate, String comment,
            Set<TherapyCommercial> therapyCommercials,
            Set<TherapyGeneric> therapyGenerics) {
        this.therapyMotivation = therapyMotivation;
        this.patient = patient;
        this.genome = genome;
        this.startDate = startDate;
        this.stopDate = stopDate;
        this.comment = comment;
        this.therapyCommercials = therapyCommercials;
        this.therapyGenerics = therapyGenerics;
    }

    // Property accessors
    public Integer getTherapyIi() {
        return this.therapyIi;
    }

    public void setTherapyIi(Integer therapyIi) {
        this.therapyIi = therapyIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public TherapyMotivation getTherapyMotivation() {
        return this.therapyMotivation;
    }

    public void setTherapyMotivation(TherapyMotivation therapyMotivation) {
        this.therapyMotivation = therapyMotivation;
    }

    public PatientImpl getPatient() {
        return this.patient;
    }

    public void setPatient(PatientImpl patient) {
        this.patient = patient;
    }

    public Genome getGenome() {
        return this.genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return this.stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<TherapyCommercial> getTherapyCommercials() {
        return this.therapyCommercials;
    }

    public void setTherapyCommercials(Set<TherapyCommercial> therapyCommercials) {
        this.therapyCommercials = therapyCommercials;
    }

    public Set<TherapyGeneric> getTherapyGenerics() {
        return this.therapyGenerics;
    }

    public void setTherapyGenerics(Set<TherapyGeneric> therapyGenerics) {
        this.therapyGenerics = therapyGenerics;
    }

}
