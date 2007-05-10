package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.witty.wt.WCheckBox;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WFormWidget;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.validation.WValidator;

public class CheckBox extends WContainerWidget implements IFormField
{
    private WCheckBox checkBox_ = new WCheckBox();
    
    public CheckBox(InteractionState state, IForm form)
    {
        super();
        addWidget(checkBox_);
        checkBox_.setEnabled(state == InteractionState.Adding || state == InteractionState.Editing);
        
        if(form!=null)
        {
            form.addFormField(this);
        }
    }
    
    public void flagErroneous() 
    {
        
    }

    public void flagValid() 
    {
        
    }

    public String getFormText() 
    {
        return null;
    }

    public WFormWidget getFormWidget() 
    {
        return null;
    }

    public WWidget getViewWidget() 
    {
        return checkBox_;
    }

    public WWidget getWidget() 
    {
        return checkBox_;
    }

    public boolean isMandatory() 
    {
        if(checkBox_.validator()!=null)
        {
            return checkBox_.validator().isMandatory();
        }
        return false;
    }

    public void setFormText(String text) 
    {
        
    }

    public void setMandatory(boolean mandatory) 
    {
        if(checkBox_.validator()!=null)
        {
            checkBox_.setValidator(new WValidator());
        }
        checkBox_.validator().setMandatory(mandatory);
    }

    public boolean validate() 
    {
        return true;
    }
    
    public boolean isChecked()
    {
        return checkBox_.isChecked();
    }
    
    public void setChecked(boolean checked)
    {
        checkBox_.setChecked(checked);
    }
}
