package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.sf.regadb.csv.Table;
import net.sf.regadb.util.date.DateUtils;

public class WivArcDeathsForm extends WivIntervalQueryForm {
    
    public WivArcDeathsForm(){
        super(tr("menu.query.wiv.arc.deaths"),tr("form.query.wiv.label.arc.deaths"),tr("file.query.wiv.arc.deaths"));
        setQuery("select p, pav from PatientImpl as p inner join p.patientAttributeValues pav " +
        		"where p.deathDate >= :var_start_date and p.deathDate <= :var_end_date " +
        		"and pav.attribute.name = 'PatCode' " +
        		"order by p.deathDate desc");
        
        setStartDate(DateUtils.getDateOffset(getEndDate(), Calendar.YEAR, -1));
    }

    @Override
    protected File postProcess(File csvFile) {
        File outFile = new File(csvFile.getAbsolutePath()+".processed.csv");
        
        ArrayList<String> row;

        Table in = readTable(csvFile);

        Table out = new Table();
        
        int CDeathDate = in.findColumn("PatientImpl.deathDate");
        int CPatCode = in.findColumn("PatientAttributeValue.value");
        
        for(int i=1; i<in.numRows(); ++i){
            row = new ArrayList<String>();
            
            
            row.add(getCentreName());
            row.add(OriginCode.ARC.getCode()+"");
            row.add(in.valueAt(CPatCode, i));   //patcode
            row.add(getFormattedDate(getDate(in.valueAt(CDeathDate, i))));  //death date
            row.add(TypeOfInformationCode.DEATH.getCode()+"");
            row.add(CauseOfDeathCode.UNKNOWN.getCode()+"");   //cause of death n/a=9
            row.add("");

            out.addRow(row);
        }
        
        try{
            out.exportAsCsv(new FileOutputStream(outFile),';',false);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return outFile;
    }
}
