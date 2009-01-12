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
import net.sf.regadb.ui.framework.forms.fields.LimitedNumberField;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.forms.validation.WFormValidation;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.StandardButton;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMessageBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;

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
    
    public FormWidget(WString formName, InteractionState interactionState)
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
            _deleteButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
            {
                public void trigger(WMouseEvent a) 
                {
                    final WMessageBox cmb = UIUtils.createYesNoMessageBox(FormWidget.this, tr("msg.warning.delete"));
                    cmb.buttonClicked.addListener(FormWidget.this, new Signal1.Listener<StandardButton>(){
        				@Override
        				public void trigger(StandardButton sb) {
        					cmb.destroy();
        					if(sb==StandardButton.Yes) {
        						deleteAction();
        					}
        				}
                    });
                    cmb.show();
                }
                });
        }
        else
        {
            buttonContainer.addWidget(_okButton);
            _okButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
            {
                public void trigger(WMouseEvent a) 
                {
                    confirmAction();
                }
            });
            buttonContainer.addWidget(_cancelButton);
            _cancelButton.clicked.addListener(this, new Signal1.Listener<WMouseEvent>()
            {
                public void trigger(WMouseEvent a) 
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
        buttonContainer.setStyleClass("control-buttons");
    }
    
    private void deleteAction()
    {
    	WString message = deleteObject();
    	
        if(message == null)
        {
        	redirectAfterDelete();
        }
        else
        {
        	UIUtils.showWarningMessageBox(this, message);
        	
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
            return new DateField(getInteractionState(), this, RegaDBSettings.getInstance().getDateFormat());
        }
        
        return null;
    }
    
    public abstract void saveData();
    
    public abstract void cancel();
    
    public abstract WString deleteObject();
    
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
    
    public WString leaveForm() {
        if(isEditable()) {
            return tr("form.warning.stillEditing");
        } else {
            return null;
        }
    }
}
