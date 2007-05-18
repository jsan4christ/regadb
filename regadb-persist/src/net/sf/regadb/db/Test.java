package net.sf.regadb.db;

// Generated 18/05/2007 15:38:06 by Hibernate Tools 3.2.0.beta8

/**
 * Test generated by hbm2java
 */
public class Test implements java.io.Serializable {

    // Fields    

    private Integer testIi;

    private int version;

    private TestType testType;

    private String description;

    private String serviceClass;

    private String serviceData;

    private String serviceConfig;

    // Constructors

    /** default constructor */
    public Test() {
    }

    /** minimal constructor */
    public Test(TestType testType, String description) {
        this.testType = testType;
        this.description = description;
    }

    /** full constructor */
    public Test(TestType testType, String description, String serviceClass,
            String serviceData, String serviceConfig) {
        this.testType = testType;
        this.description = description;
        this.serviceClass = serviceClass;
        this.serviceData = serviceData;
        this.serviceConfig = serviceConfig;
    }

    // Property accessors
    public Integer getTestIi() {
        return this.testIi;
    }

    public void setTestIi(Integer testIi) {
        this.testIi = testIi;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public TestType getTestType() {
        return this.testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceClass() {
        return this.serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getServiceData() {
        return this.serviceData;
    }

    public void setServiceData(String serviceData) {
        this.serviceData = serviceData;
    }

    public String getServiceConfig() {
        return this.serviceConfig;
    }

    public void setServiceConfig(String serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

}
