package net.sf.regadb.ui.datatable.measurement;

import java.util.List;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.DateFilter;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.ui.framework.widgets.datatable.StringFilter;
import net.sf.regadb.ui.framework.widgets.datatable.hibernate.HibernateStringUtils;
import net.sf.regadb.util.date.DateUtils;

public class IMeasurementDataTable implements IDataTable<TestResult>
{
	private static String [] _colNames = {"dataTable.test.colName.date","dataTable.test.colName.testType","dataTable.test.colName.genome", 
		"dataTable.test.colName.testName", "dataTable.test.colName.result"};
	private static String[] filterVarNames_ = { "testResult.testDate", "testResult.test.testType.description", "testResult.test.testType.genome.organismName",
		"testResult.test.description",	null };
	
	private static boolean [] sortable_ = {true, true, true, true, false};
	private static int[] colWidths = {20,30,10,20,20};
	
	private IFilter[] filters_ = new IFilter[5];
	
	public IMeasurementDataTable()
	{
		
	}
	
	public String[] getColNames()
	{
		return _colNames;
	}

	public List<TestResult> getDataBlock(Transaction t, int startIndex, int amountOfRows, int sortIndex, boolean isAscending)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
		return t.getNonViralIsolateTestResults(pt, startIndex, amountOfRows, filterVarNames_[sortIndex], isAscending, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public long getDataSetSize(Transaction t)
	{
		Patient pt = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
		return t.getNonViralIsolateTestResultsCount(pt, HibernateStringUtils.filterConstraintsQuery(this));
	}

	public String[] getFieldNames()
	{
		return filterVarNames_;
	}

	public IFilter[] getFilters()
	{
		return filters_;
	}

	public String[] getRowData(TestResult type)
	{
		String [] row = new String[5];
		
		row[0] = DateUtils.getEuropeanFormat(type.getTestDate());
		row[1] = type.getTest().getTestType().getDescription();
		row[2] = (type.getTest().getTestType().getGenome() == null ? "":type.getTest().getTestType().getGenome().getOrganismName());
		row[3] = type.getTest().getDescription();
		if (type.getValue() == null) {
			row[4] = type.getTestNominalValue().getValue();
		}
		else {
			if (ValueTypes.getValueType(type.getTest().getTestType().getValueType()) == ValueTypes.DATE) {
				row[4] = DateUtils.getEuropeanFormat(type.getValue());
			}
			else {
				row[4] = type.getValue();
			}
		}
		
		return row;
	}

	public void init(Transaction t)
	{
		filters_[0] = new DateFilter();
		filters_[1] = new StringFilter();
		filters_[2] = new StringFilter();
		filters_[3] = new StringFilter();
		filters_[4] = null;
	}

	public void selectAction(TestResult selectedItem)
	{
        RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().measurementSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().measurementView.selectNode();
	}

    public boolean[] sortableFields()
	{
		return sortable_;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}
}
