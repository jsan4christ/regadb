package net.sf.regadb.ui.form.attributeSettings;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.i8n.WMessage;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WTable;

public class AttributeGroupForm extends FormWidget 
{
    private AttributeGroup attributeGroup_;
    
    //general group
    private WGroupBox generalGroup_;
    private WTable generalGroupTable_;
    private Label nameL;
    private TextField nameTF;
    
    public AttributeGroupForm(InteractionState interactionState, WMessage formName, AttributeGroup attributeGroup)
    {
        super(formName, interactionState);
        attributeGroup_ = attributeGroup;
        
        init();
        
        fillData();
    }
    
    private void init() 
    {
        generalGroup_ = new WGroupBox(tr("form.attributeSettings.attributeGroup.editView.general"), this);
        generalGroupTable_= new WTable(generalGroup_);
        nameL = new Label(tr("form.attributeSettings.attributeGroup.editView.groupName"));
        nameTF = new TextField(getInteractionState(), this);
        nameTF.setMandatory(true);
        addLineToTable(generalGroupTable_, nameL, nameTF);
        
        addControlButtons();
    }
    
    private void fillData() 
    {
        if(getInteractionState()==InteractionState.Adding)
        {
            attributeGroup_ = new AttributeGroup();
        }
        
        if(getInteractionState()!=InteractionState.Adding)
        {
            Transaction t = RegaDBMain.getApp().createTransaction();
            
            t.attach(attributeGroup_);
            
            nameTF.setText(attributeGroup_.getGroupName());
            
            t.commit();
        }
    }

    @Override
    public void saveData() 
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        if(!(getInteractionState()==InteractionState.Adding))
        {
            t.attach(attributeGroup_);
        }
        
        attributeGroup_.setGroupName(nameTF.text());
        
        t.save(attributeGroup_);
        t.commit();
        
        RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsSelected.setSelectedAttributeGroup(attributeGroup_);
        RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().attributeGroupsView.selectNode();
    }
}
