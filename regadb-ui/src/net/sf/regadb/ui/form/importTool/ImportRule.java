package net.sf.regadb.ui.form.importTool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.form.importTool.data.DataProvider;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTableRow;
import eu.webtoolkit.jwt.WWidget;

public class ImportRule {
	private ComboBox<String> column;
	private ComboBox<Rule.Type> type;
	private TextField number;
	private ComboBox<Serializable> name;
	private WPushButton details;
	private Signal1.Listener<WMouseEvent> detailsListener;
	private WPushButton remove;
	
	private DataProvider dataProvider;
	private Rule rule;
	
	private ImportToolForm form;
	
	private DetailsForm detailsForm;

	public ImportRule(DataProvider dataProvider, final ImportToolForm form, final WTableRow row, Rule rule) {
		this.form = form;
		
		InteractionState is = form.getInteractionState();
		column = new ComboBox<String>(dataProvider!=null?is:InteractionState.Viewing, form);
		addToRow(row, 0, column);
		type = new ComboBox<Rule.Type>(is, form);
		addToRow(row, 1, type);
		name = new ComboBox<Serializable>(is, form);
		addToRow(row, 2, name);
		number = new TextField(is, form, FieldType.INTEGER);
		number.setTextSize(2);
		number.setText("1");
		addToRow(row, 3, number);
		details = new WPushButton(WString.tr("form.importTool.rule.details"));
		addToRow(row, 4, details);
		if (form.getInteractionState() == InteractionState.Editing || 
				form.getInteractionState() == InteractionState.Adding) {
			remove = new WPushButton(WString.tr("form.importTool.rule.delete"));
			addToRow(row, 5, remove);
			remove.clicked().addListener(row.getTable(), new Signal1.Listener<WMouseEvent>(){
				public void trigger(WMouseEvent arg) {
					row.getTable().deleteRow(row.getRowNum());
					form.getRules().remove(ImportRule.this);
				}
			});
		}
		
		this.dataProvider = dataProvider;
		
		fillColumnCombo(rule);
		fillTypeCombo(rule);
		fillTypeNameCombo(rule);
		fillDetails(rule, dataProvider);
		
		this.rule = rule;
		
		type.addComboChangeListener(new Signal.Listener(){
			public void trigger() {
				fillTypeNameCombo(getRule());
			}			
		});
		name.addComboChangeListener(new Signal.Listener(){
			public void trigger() {
				fillDetails(ImportRule.this.rule, ImportRule.this.dataProvider);
			}			
		});
	}

	private void fillColumnCombo(Rule rule) {
		column.clearItems();
		
		if (dataProvider == null) {
			column.addItem(new DataComboMessage<String>(rule.getColumn(), rule.getColumn()));
		} else { 
			for (String d : dataProvider.getHeaders()) {
				column.addItem(new DataComboMessage<String>(d, d));
			}
		}
		if (rule.getColumn() != null)
			column.selectItem(rule.getColumn());
	}
	
	private void fillTypeCombo(Rule rule) {
		type.clearItems();
		
		for (Rule.Type t : Rule.Type.values()) {
			type.addItem(new DataComboMessage<Rule.Type>(t, t.getName()));
		}
		if (rule.getType() != null)
			type.selectItem(rule.getType().getName());
	}
	
	private void fillTypeNameCombo(Rule rule) {
		name.clearItems();
		
		Transaction tr = RegaDBMain.getApp().createTransaction();
		name.setHidden(false);
		number.setEnabled(true);
		if (type.currentValue() == Rule.Type.AttributeValue) {
			List<Attribute> attributes = tr.getAttributes();
			for (Attribute a : attributes) {
				name.addItem(new DataComboMessage<Serializable>(a, a.getName()));
			}
			number.setEnabled(false);
		} else if (type.currentValue() == Rule.Type.EventValue) {
			List<Event> events = tr.getEvents();
			for (Event e : events) {
				name.addItem(new DataComboMessage<Serializable>(e, e.getName()));
			}
		} else if (type.currentValue() == Rule.Type.TestValue) {
			List<Test> tests = tr.getTests();
			for (Test t : tests) {
				name.addItem(new DataComboMessage<Serializable>(t, t.getDescription()));
			}
		} else {
			name.setHidden(true);
		}
		
		if (rule.getTypeName() != null)
			name.selectItem(rule.getTypeName());
	}
	
	//TODO show correct form
	private void fillDetails(Rule rule, DataProvider provider) {
		details.setDisabled(false);
		if (type.currentValue() == Rule.Type.AttributeValue) {
			Attribute attribute = (Attribute)name.currentItem().getDataValue();
			if (ValueTypes.isNominal(attribute.getValueType()))
				addDetailsListener(details, 
						new MappingDetailsForm(
								getAttributeMappings(attribute, provider), 
								WString.tr("form.importTool.details.attributeNV"), 
								this));
		} else if (type.currentValue() == Rule.Type.EventValue) {
			Event event = (Event)name.currentItem().getDataValue();
			if (ValueTypes.isNominal(event.getValueType()))
				addDetailsListener(details, 
						new MappingDetailsForm(
								getEventMappings(event, provider), 
								WString.tr("form.importTool.details.eventNV"), 
								this));
		} else if (type.currentValue() == Rule.Type.TestValue) {
			Test test = (Test)name.currentItem().getDataValue();
			if (ValueTypes.isNominal(test.getTestType().getValueType()))
				addDetailsListener(details, 
						new MappingDetailsForm(
								getTestMappings(test, provider), 
								WString.tr("form.importTool.details.testNV"), 
								this));
		} else {
			details.setDisabled(true);
		}
	}
	
	private Map<String, String> getEventMappings(Event e, DataProvider dp) {
		List<String> databaseValues = new ArrayList<String>();
		for (EventNominalValue env : e.getEventNominalValues()) {
			databaseValues.add(env.getValue());
		}
		return getMappings(databaseValues, dp);
	}
	
	private Map<String, String> getTestMappings(Test t, DataProvider dp) {
		List<String> databaseValues = new ArrayList<String>();
		for (TestNominalValue tnv : t.getTestType().getTestNominalValues()) {
			databaseValues.add(tnv.getValue());
		}
		return getMappings(databaseValues, dp);
	}
	
	private Map<String, String> getAttributeMappings(Attribute a, DataProvider dp) {
		List<String> databaseValues = new ArrayList<String>();
		for (AttributeNominalValue anv : a.getAttributeNominalValues()) {
			databaseValues.add(anv.getValue());
		}
		return getMappings(databaseValues, dp);
	}
	
	private Map<String, String> getMappings(List<String> databaseValues, DataProvider dp) {
		Map<String, String> mappings = new HashMap<String, String>();
		List<String> excelValues = dp.getValues(column.text());
		for (String ev : excelValues) {
			String evt = ev.trim();
			String mapping = "";
			for (String dv : databaseValues) {
				if (evt.equalsIgnoreCase(dv)) {
					mapping = dv;
				}
			}
			mappings.put(evt, mapping);
		}
		return mappings;
	}
	
	private void addDetailsListener(WPushButton button, final DetailsForm form) {
		this.detailsForm = form;
		if (detailsListener != null) 
			button.clicked().removeListener(detailsListener);
		
		detailsListener = new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent arg) {
				DetailsBox box = new DetailsBox(form);
			}
		};
		button.clicked().addListener(button, detailsListener);
	}
	
	private void addToRow(WTableRow row, int col, WWidget widget) {
		row.getTable().getElementAt(row.getRowNum(), col).addWidget(widget);
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public void saveRule() {
		rule.setColumn(column.currentValue());
		rule.setNumber(Integer.parseInt(number.text()));
		rule.setType(type.currentValue());
		rule.setTypeName(name.currentString());
		
		if (detailsForm != null) {
			//TODO validate
			WString error = detailsForm.validate();
			detailsForm.save(rule);
		}
	}
	
	public ImportToolForm getForm() {
		return form;
	}
}
