
/** Java class "Query.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.util.*;

/**
 * <p>
 * Represents a query to the database.
 * </p>
 * <p>
 * A query consists of a root WhereClause and a SelectList.
 * </p>
 * <p>
 * The root WhereClause is typically a ComposedWhereClause that is composed
 * of several subclauses. The WhereClauses can be configured, added and
 * removed, until the resulting constraints please the user. These
 * constraints define which tables from the database are relevant for this
 * Query and what rows to retrieve from these tables.
 * </p>
 * <p>
 * The SelectList defines what fields from the relevant tables to retrieve
 * from the database.
 * </p>
 * <p>
 * This class supports xml-encoding.
 * The following new properties are encoded :
 *  rootClause
 *  selectList
 *  uniqueNameContext
 * </p>
 */
public class Query {
    
    ///////////////////////////////////////
    // associations
    
    private WhereClause rootClause = new AndClause();
    private SelectionStatusList selectList = new SelectionStatusList(this);
    
    private UniqueNameContext uniqueNameContext = new UniqueNameContext();
    
    ///////////////////////////////////////
    // constructor
    
    public Query() {
    }
    
    public Query(WhereClause whereClause) {
        this.rootClause = whereClause;
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public WhereClause getRootClause() {
        return rootClause;
    }
    
    public void setRootClause(WhereClause whereClause) {
        this.rootClause = whereClause;
    }
    
    public SelectionStatusList getSelectList() {
        return selectList;
    }
    
    /**
     * For XMLdecoder only!
     */
    public void setSelectList(SelectionStatusList selectList) {
        this.selectList = selectList;
    }
    
    public UniqueNameContext getUniqueNameContext() {
        return uniqueNameContext;
    }
    
    public void setUniqueNameContext(UniqueNameContext context) {
        this.uniqueNameContext = context;
    }
    ///////////////////////////////////////
    // operations
    
    
    /**
     * <p>
     * Returns a collection of Works that have to be performed before the query clauses
     * (Hibernate query, query string, ...) can be retrieved.
     * </p>
     *
     * @return a Collection with all Works required to prepare the query.
     */
    public Collection getPreparationWorks() {
        return rootClause.getQueryPreparationWorks();
    }
    
    /**
     * <p>
     * Generates a Hibernate query according to the semantics of the current
     * Query configuration.
     * </p>
     * <p>
     *
     * @return a HibernateQuery equivalent to this Query
     * </p>
     * <p>
     * @pre isValid()
     * </p>
     * <p>
     * @throws InvalidWhereClauseException iff the Query's root clause is
     * invalid.
     * </p>
     */
    public HibernateQuery getHibernateQuery() throws InvalidWhereClauseException {
        if (!isValid()) {
            throw new InvalidWhereClauseException();
        }
        // FIXME
        return null;
    }
    
    // for testing purposes only
    public String getQueryString() throws java.sql.SQLException { //, com.pharmadm.custom.rega.chem.search.MoleculeIndexingException {
        return selectList.getSelectClause()
        + "\nFROM " + getRootClause().getHibernateFromClause()
        + "\nWHERE " + getRootClause().getHibernateWhereClause();
    }
    
    /**
     * <p>
     * Calculates whether the root WhereClause of this Query is valid. Validity
     * is a necessary and sufficient contraint for this Query to be able to
     * generate an equivalent Hibernate query that can be evaluated.
     * </p>
     * <p>
     *
     * @return whether the root WhereClause of this Query is valid
     * </p>
     */
    public boolean isValid() {
        return getRootClause().isValid();
    }
    
    public void updateSelectList() {
        selectList.update();
    }
}


