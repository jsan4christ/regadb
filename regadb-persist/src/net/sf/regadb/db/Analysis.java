package net.sf.regadb.db;

// Generated 21/05/2007 10:48:42 by Hibernate Tools 3.2.0.beta8

import java.util.HashSet;
import java.util.Set;

/**
 * Analysis generated by hbm2java
 */
public class Analysis implements java.io.Serializable {

    // Fields    

    private Integer analysisIi;

    private AnalysisType analysisType;

    private Integer type;

    private String url;

    private String account;

    private String password;

    private String baseinputfile;

    private String baseoutputfile;

    private Set<AnalysisData> analysisDatas = new HashSet<AnalysisData>(0);

    // Constructors

    /** default constructor */
    public Analysis() {
    }

    /** minimal constructor */
    public Analysis(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    /** full constructor */
    public Analysis(AnalysisType analysisType, Integer type, String url,
            String account, String password, String baseinputfile,
            String baseoutputfile, Set<AnalysisData> analysisDatas) {
        this.analysisType = analysisType;
        this.type = type;
        this.url = url;
        this.account = account;
        this.password = password;
        this.baseinputfile = baseinputfile;
        this.baseoutputfile = baseoutputfile;
        this.analysisDatas = analysisDatas;
    }

    // Property accessors
    public Integer getAnalysisIi() {
        return this.analysisIi;
    }

    public void setAnalysisIi(Integer analysisIi) {
        this.analysisIi = analysisIi;
    }

    public AnalysisType getAnalysisType() {
        return this.analysisType;
    }

    public void setAnalysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseinputfile() {
        return this.baseinputfile;
    }

    public void setBaseinputfile(String baseinputfile) {
        this.baseinputfile = baseinputfile;
    }

    public String getBaseoutputfile() {
        return this.baseoutputfile;
    }

    public void setBaseoutputfile(String baseoutputfile) {
        this.baseoutputfile = baseoutputfile;
    }

    public Set<AnalysisData> getAnalysisDatas() {
        return this.analysisDatas;
    }

    public void setAnalysisDatas(Set<AnalysisData> analysisDatas) {
        this.analysisDatas = analysisDatas;
    }

}
