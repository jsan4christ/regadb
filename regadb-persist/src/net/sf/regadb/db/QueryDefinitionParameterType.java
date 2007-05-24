package net.sf.regadb.db;

// Generated 24/05/2007 11:35:28 by Hibernate Tools 3.2.0.beta8

/**
 * QueryDefinitionParameterType generated by hbm2java
 */
public class QueryDefinitionParameterType implements java.io.Serializable {

    // Fields    

    private Integer queryDefinitionParameterTypeIi;

    private String name;

    private Integer id;

    // Constructors

    /** default constructor */
    public QueryDefinitionParameterType() {
    }

    /** minimal constructor */
    public QueryDefinitionParameterType(String name) {
        this.name = name;
    }

    /** full constructor */
    public QueryDefinitionParameterType(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    // Property accessors
    public Integer getQueryDefinitionParameterTypeIi() {
        return this.queryDefinitionParameterTypeIi;
    }

    public void setQueryDefinitionParameterTypeIi(
            Integer queryDefinitionParameterTypeIi) {
        this.queryDefinitionParameterTypeIi = queryDefinitionParameterTypeIi;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
