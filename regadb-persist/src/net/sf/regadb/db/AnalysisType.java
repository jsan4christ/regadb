package net.sf.regadb.db;

// Generated 18/05/2007 15:38:06 by Hibernate Tools 3.2.0.beta8

/**
 * AnalysisType generated by hbm2java
 */
public class AnalysisType implements java.io.Serializable {

    // Fields    

    private Integer analysisTypeIi;

    private String type;

    // Constructors

    /** default constructor */
    public AnalysisType() {
    }

    /** full constructor */
    public AnalysisType(String type) {
        this.type = type;
    }

    // Property accessors
    public Integer getAnalysisTypeIi() {
        return this.analysisTypeIi;
    }

    public void setAnalysisTypeIi(Integer analysisTypeIi) {
        this.analysisTypeIi = analysisTypeIi;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
