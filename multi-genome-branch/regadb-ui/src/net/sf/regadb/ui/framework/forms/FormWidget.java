package net.sf.regadb.ui.framework.forms;

import java.io.Serializable;
import java.util.ArrayList;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.FormField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.LimitedNumberField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.forms.validation.WFormValidation;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.messagebox.ConfirmMessageBox;
import net.sf.regadb.ui.framework.widgets.messagebox.MessageBox;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.i8n.WMessage;

public abstract class FormWidget extends WGroupBox implements IForm,IConfirmForm
{
    private ArrayList<IFormField> formFields_ = new ArrayList<IFormField>();
    
    private WFormValidation formValidation_ = new WFormValidation();
    
    private InteractionState interactionState_;
    
    //control buttons
    private WPushButton _okButton = new WPushButton(tr("form.general.button.ok"));
    private WPushButton _cancelButton = new WPushButton(tr("form.general.button.cancel"));
    private WPushButton _helpButton = new WPushButton(tr("form.general.button.help"));
    private WPushButton _deleteButton = new WPushButton(tr("form.general.button.delete"));
    
    public FormWidget(WMessage formName, InteractionState interactionState)
	{
        super(formName);
        interactionState_ = interactionState;
        formValidation_.init(this);
	}
    
    public String getNulled(String text)
    {
        if("".equals(text))
            return null;
        else
            return text;
    }
	
	public WContainerWidget getWContainer()
	{
		return this;
	}

	public void addFormField(IFormField field)
	{
        formFields_.add(field);
	}
    
    public void removeFormField(IFormField field)
    {
        formFields_.remove(field);
    }
	
    public int addLineToTable(WTable table, Label label, IFormField field)
    {
        int numRows = table.numRows();
        table.putElementAt(numRows, 0, label);
        table.putElementAt(numRows, 1, field.getWidget());
        label.setBuddy(field);
        return numRows;
    }
    
    public int addLineToTable(WTable table, WWidget[] widgets)
    {
        int numRows = table.numRows();
        
        for(int i=0;i<widgets.length;++i)
            table.putElementAt(numRows, i, widgets[i]);
        return numRows;
    }

	public InteractionState getInteractionState()
	{
		return interactionState_;
	}
	
	public boolean isEditable()
	{
		return interactionState_== InteractionState.Adding || interactionState_== InteractionState.Editing; 
	}
	
    protected void addControlButtons()
    {
        WContainerWidget buttonContainer = new WContainerWidget(this);
        
        if(getInteractionState()==InteractionState.Deleting)
        {
            buttonContainer.addWidget(_deleteButton);
            _deleteButton.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                public void notify(WMouseEvent a) 
                {
                    final ConfirmMessageBox cmb = new ConfirmMessageBox(tr("msg.warning.delete"));
                    cmb.yes.clicked.addListener(new SignalListener<WMouseEvent>()
                    {
                        public void notify(WMouseEvent a) 
                        {
                            deleteAction();
                            
                            cmb.hide();
                        }
                    });
                    cmb.no.clicked.addListener(new SignalListener<WMouseEvent>()
                    {
                        public void notify(WMouseEvent a) 
                        {
                            cmb.hide();
                        }
                    });
                }
                });
        }
        else
        {
            buttonContainer.addWidget(_okButton);
            _okButton.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                public void notify(WMouseEvent a) 
                {
                    confirmAction();
                }
            });
            buttonContainer.addWidget(_cancelButton);
            _cancelButton.clicked.addListener(new SignalListener<WMouseEvent>()
            {
                public void notify(WMouseEvent a) 
                {
                    cancel();
                }
            });

            if(!isEditable())
            {
                _okButton.setEnabled(false);
                _cancelButton.setEnabled(false);
            }
        }
        
        buttonContainer.addWidget(_helpButton);
        buttonContainer.setContentAlignment(WHorizontalAlignment.AlignRight);
    }
    
    private void deleteAction()
    {
    	WMessage message = deleteObject();
    	
        if(message == null)
        {
        	redirectAfterDelete();
        }
        else
        {
        	MessageBox.showWarningMessage(message);
        	
        	redirectAfterDelete();
        }
    }

    public FormField getTextField(ValueTypes type)
    {
        switch(type)
        {
        case STRING:
        	return new TextField(getInteractionState(), this);
        case NUMBER:
        	return new TextField(getInteractionState(), this, FieldType.DOUBLE);
        case LIMITED_NUMBER:
        	return new LimitedNumberField(getInteractionState(), this, FieldType.DOUBLE);
        case DATE:
            return new DateField(getInteractionState(), this);
        }
        
        return null;
    }
    
    public abstract void saveData();
    
    public abstract void cancel();
    
    public abstract WMessage deleteObject();
    
    public abstract void redirectAfterDelete();
    
    protected void update(Serializable o, Transaction t)
    {
        if(interactionState_==InteractionState.Adding)
        {
            t.save(o);
        }
        else
        {
            t.update(o);
        }
    }
    
    protected void update(Patient p, Transaction t)
    {
        if(interactionState_==InteractionState.Adding)
        {
            t.save(p);
        }
        else
        {
            t.update(p);
        }
    }
    
    public void confirmAction()
    {
        if(formValidation_.validate(formFields_))
        {
            formValidation_.setHidden(true);
            saveData();
        }
        else
        {
            formValidation_.setHidden(false);
        }
    }
    
    protected void redirectToView(TreeMenuNode expandNode, TreeMenuNode selectNode)
    {
        expandNode.expand();
        expandNode.refreshAllChildren();
        selectNode.selectNode();
    }
    
    protected void redirectToSelect(TreeMenuNode expandNode, TreeMenuNode selectNode)
    {
        expandNode.refreshAllChildren();
        selectNode.selectNode();
    }
    
    public void enableOkButton(boolean enable)
    {
        this._okButton.setEnabled(enable);
    }
    
    public WMessage leaveForm() {
        if(isEditable()) {
            return tr("form.warning.stillEditing");
        } else {
            return null;
        }
    }
}