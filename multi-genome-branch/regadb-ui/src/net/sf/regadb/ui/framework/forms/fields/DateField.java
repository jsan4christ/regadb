package net.sf.regadb.ui.framework.forms.fields;

import java.util.Date;

import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.WDateValidator;
import eu.webtoolkit.jwt.WFormWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WLength;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WTable;

public class DateField extends FormField
{
	private WLineEdit _fieldEdit;
	private WImage calendarIcon_ = new WImage("pics/calendar.png");
	
	public DateField(InteractionState state, IForm form, String dateFormat)
	{
		super();
		setStyleClass("datefield");
        if(state == InteractionState.Adding || state == InteractionState.Editing)
        {
			_fieldEdit = new WLineEdit();
            ConfirmUtils.addConfirmAction(form, _fieldEdit);
            WTable table = new WTable(this);
            table.elementAt(0, 0).addWidget(_fieldEdit);
            table.elementAt(0, 1).addWidget(calendarIcon_);
            table.elementAt(0, 1).resize(new WLength(24, WLength.Unit.Pixel), new WLength());
			addWidget(_fieldEdit);
			addWidget(calendarIcon_);
			flagValid();
			table.setStyleClass("date-field");
		}
		else
		{
		    initViewWidget();
		}
		
		form.addFormField(this);
        
		if(_fieldEdit!=null)
		{
			_fieldEdit.setValidator(new WDateValidator(dateFormat));
		}
	}
	
	public void setEchomode(WLineEdit.EchoMode mode)
	{
		_fieldEdit.setEchoMode(mode);
	}

	public WFormWidget getFormWidget()
	{
		return _fieldEdit;
	}
	
	public void flagErroneous()
	{
		_fieldEdit.setStyleClass("Wt-invalid");
	}

	public void flagValid()
	{
		_fieldEdit.setStyleClass("");
	}

    public String getFormText() 
    {
        return _fieldEdit.text();
    }
    
    public void setFormText(String text) 
    {
        _fieldEdit.setText(text);
    }
    
    public void setDate(Date date)
    {
    	if(date!=null)
    	{
    		setText(DateUtils.getEuropeanFormat(date));
    	}
        else
        {
            setText("");
        }
    }
    
    public Date getDate()
    {
    	return DateUtils.parseEuropeanDate(text());
    }
    
    public void addChangeListener(Signal.Listener listener)
    {
        if(_fieldEdit!=null)
        {
            _fieldEdit.changed.addListener(this, listener);
        }
    }
}
