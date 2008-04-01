package net.sf.regadb.io.db.jerusalem;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.jerusalem.ParseDrugs.Drug;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Logging;
import net.sf.regadb.io.db.util.Utils;

public class ParseAll {
    
    public static void main(String[] args){
        ParseAll pa = new ParseAll();
        
        pa.run(args[0], args[1], args[2]);
        
    }
    
    public ParseAll(){
        
    }
    
    public void run(String importDir, String mappingDir, String outputDir){
        Logging logger = ConsoleLogger.getInstance();
        logger.logWarning("Parsing...");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
        
        ParsePatients pPatients = new ParsePatients(logger,df);
        ParseDrugs pDrugs = new ParseDrugs(logger);
        ParseTherapies pTherapies = new ParseTherapies(logger,df);
        ParseTests pTests = new ParseTests(logger,df);
        ParseSequences pSeqs = new ParseSequences(logger,df);
        
        Map<String,Drug> drugs = pDrugs.run(getFile(mappingDir,"drug.mapping"));
        
        Map<String,Patient> patients = pPatients.run(   getFile(importDir,"ID.csv"),
                                                        getFile(mappingDir,"gender.mapping"),
                                                        getFile(mappingDir,"country.mapping"),
                                                        getFile(mappingDir,"transmission_group.mapping"));
        
        pTherapies.run( patients,
                        drugs,
                        getFile(importDir,"TreatHistorySon.csv"),
                        getFile(mappingDir,"therapy_motivation.mapping"));
        
        pTests.run(     patients,
                        getFile(importDir,"Samples.csv"),
                        getFile(importDir,"ClinDataSon.csv"),
                        getFile(importDir,"VLSysNames.csv"),
                        getFile(importDir,"VLunits.csv"));
        
        //pSeqs.run(      getFile(importDir,""));
        
        Utils.exportPatientsXML(patients, outputDir + File.separatorChar + "patients.xml");
        //Utils.exportNTXML(viralisolates, outputDir + File.separatorChar + "viralisolates.xml");
        
        logger.logWarning("Done.");
    }
    
    private File getFile(String dir, String name){
        return new File(dir + File.separatorChar + name);
    }

}
