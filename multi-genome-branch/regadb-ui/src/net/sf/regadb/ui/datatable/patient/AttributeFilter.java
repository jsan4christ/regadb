package net.sf.regadb.ui.datatable.patient;

import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.widgets.datatable.FilterTools;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.ListFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.TimestampFilter;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.i8n.WMessage;

public class AttributeFilter extends WContainerWidget implements IFilter 
{
    private static WMessage noAttribute = lt("No attribute");
    
    private Attribute attribute_=null;
    private IFilter filter_=null;
    private Transaction transaction_;
    
    private WComboBox attributeCombo_;
    
    public AttributeFilter(Transaction transaction, Attribute attribute)
    {
        super();
        transaction_ = transaction;

        setAttribute(attribute);
        init();
    }
    
    protected void init(){
        Transaction t = getTransaction();
        List<Attribute> l = t.getAttributes();
        
        setAttributeCombo(new WComboBox());
        addWidget(getAttributeCombo());
        
        for(Attribute a : l){
            getAttributeCombo().addItem(lt(a.getName()));
        }
        getAttributeCombo().sort();
        getAttributeCombo().insertItem(0, noAttribute);
        
        if(getAttribute() != null)
            getAttributeCombo().setCurrentItem(lt(getAttribute().getName()));
        else
            getAttributeCombo().setCurrentItem(noAttribute);
        
        getAttributeCombo().changed.addListener(new SignalListener<WEmptyEvent>()
                {
            public void notify(WEmptyEvent a)
            {
                changeAttribute(getAttributeCombo().currentText().value());
                FilterTools.findDataTable(getAttributeCombo()).applyFilter();
            }
        });
        
        changeAttribute(getAttribute());
    }
    
    public IFilter getFilter(){
        return filter_;
    }
    
    public Attribute getAttribute(){
        return attribute_;
    }
    
    public void setAttribute(Attribute attribute){
        attribute_ = attribute;
    }
    
    public void changeAttribute(String attributeName){
        Attribute a = null;
        
        if(!attributeName.equals(noAttribute.value())){
            List<Attribute> l = getTransaction().getAttributes(attributeName);
            if(l != null || l.size() > 0)
                a = l.get(0);
        }
        changeAttribute(a);
    }
    
    public void changeAttribute(Attribute attribute){
        setAttribute(attribute);
        
        if(filter_ != null){
            removeWidget(filter_.getFilterWidget());
        }
        
        if(attribute != null){
            ValueTypes vt = ValueTypes.getValueType(attribute_.getValueType());
                
            if(vt == ValueTypes.DATE){
                filter_ = new TimestampFilter();
            }
            if(vt == ValueTypes.LIMITED_NUMBER || vt == ValueTypes.STRING){
                filter_ = new StringFilter();
            }
            if(vt == ValueTypes.NOMINAL_VALUE){
                filter_ = new AttributeNominalValueFilter(getTransaction(),attribute_);
            }
            
            addWidget(filter_.getFilterWidget());
        }
        else{
            filter_ = null;
        }
            
    }

    public WContainerWidget getFilterWidget()
    {
        return this;
    }

    public Transaction getTransaction()
    {
        return transaction_;
    }
    
    private void setAttributeCombo(WComboBox attributeCombo_) {
        this.attributeCombo_ = attributeCombo_;
    }

    private WComboBox getAttributeCombo() {
        return attributeCombo_;
    }

    public class AttributeNominalValueFilter extends ListFilter
    {
        private Attribute attribute_;
        
        public AttributeNominalValueFilter(Transaction transaction, Attribute attribute)
        {
            super();
            setTransaction(transaction);
            attribute_ = attribute;
            init();
        }
        
        public Attribute getAttribute(){
            return attribute_;
        }
        
        @Override
        public void setComboBox(WComboBox combo)
        {
            for(AttributeNominalValue anv : getAttribute().getAttributeNominalValues())
            {
                combo.addItem(lt(anv.getValue()));
            }
        }
    }
}