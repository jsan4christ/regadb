package net.sf.regadb.io.db.uzbrussel;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.NominalTestMapper;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.StandardObjects;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ParseConsultDB {
    private static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    
    private String baseDir_;
    private Map<Integer, Patient> patients_;
    private ParseIds parseIds_;
    
    private NominalAttribute genderNominal_ = new NominalAttribute("Gender", -1, new String[] { "M", "F" },
            new String[] { "male", "female" } );
    private NominalAttribute countryOfOriginA;
    private NominalAttribute geographicOriginA;
    private NominalAttribute transmissionA;
    
    private NominalTestMapper therapyAdherenceT;
    
    private List<Attribute> regadbAttributes_;
    
    private Set<String> setset = new HashSet<String>();
    
    private Map<Integer, String> codepat_;
    
    public ParseConsultDB(String baseDir, Map<Integer, Patient> patients, ParseIds parseIds, String mappingBasePath, Map<Integer, String> codepat) {
        baseDir_ = baseDir;
        patients_ = patients;
        parseIds_ = parseIds;
        
        ParseMedication.mappingPath = mappingBasePath;
        ParseMedication.init();
    
        regadbAttributes_ = Utils.prepareRegaDBAttributes();
            
        genderNominal_.attribute.setAttributeGroup(Items.getRegadbAttributeGroup());
        
        Table countryTable = Utils.readTable(mappingBasePath + File.separatorChar + "countryOfOrigin.mapping");
        countryOfOriginA = new NominalAttribute("Country of origin", countryTable, Items.getRegadbAttributeGroup(), Utils.selectAttribute("Country of origin", regadbAttributes_), false);
        
        Table geoTable = Utils.readTable(mappingBasePath + File.separatorChar + "geographicOrigin.mapping");
        geographicOriginA = new NominalAttribute("Geographic origin", geoTable, Items.getRegadbAttributeGroup(), Utils.selectAttribute("Geographic origin", regadbAttributes_), false);
        
        Table transmissionTable = Utils.readTable(mappingBasePath + File.separatorChar + "transmissionRisk.mapping");
        transmissionA = new NominalAttribute("Transmission group", transmissionTable, Items.getRegadbAttributeGroup(), Utils.selectAttribute("Transmission group", regadbAttributes_));
        
        therapyAdherenceT = new NominalTestMapper(mappingBasePath + File.separatorChar + "therapyAdherence.mapping", Items.getGenerichivTherapyAdherence());
    
        codepat_ = codepat;
    }
    
    public void exec() {
        File consultDBXml = new File(baseDir_+"emd" + File.separatorChar + "patientdb.xml");
        
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(consultDBXml);
        } catch (JDOMException e) {
            ConsoleLogger.getInstance().logError("Problem loading consult db xml file:" + consultDBXml.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            ConsoleLogger.getInstance().logError("Problem loading consult db xml file:" + consultDBXml.getAbsolutePath());
            e.printStackTrace();
        }

        Element root = doc.getRootElement();

        List patientEls = root.getChildren("Patient");
        for(Object patientEl : patientEls) {
            parsePatient((Element)patientEl);
        }
        
        for(String s : setset) {
            ConsoleLogger.getInstance().logError("Setset: " + s);
        }
        
        for(String s : ParseMedication.notParsableMeds) {
            ConsoleLogger.getInstance().logError("Cannot parse drug: " + s);
        }
    }
    
    private void parsePatient(Element patientEl) {
        String consultId = text(patientEl, "PatientNo");
        Integer id = parseIds_.getPatientId(consultId);
        if(id != null) {
            Patient p = new Patient();
            p.setPatientId(id+"");
            patients_.put(id, p);
            
            p.createPatientAttributeValue(Items.getPatCodeAttribute()).setValue(codepat_.get(id));
            
            String birthDate = text(patientEl, "BirthDate");
            String deathDate = text(patientEl, "DeathDate");
            String sex = text(patientEl, "Sex");
            String origin = text(patientEl, "Origin");
            String nationality = text(patientEl, "Nationality");
            String transmission = text(patientEl, "Transmission");
            String adherence = text(patientEl, "TherapyAdherence");
            
            if(birthDate!=null) {
                try {
                    p.setBirthDate(dateFormatter.parse(birthDate));
                } catch (ParseException e) {
                    ConsoleLogger.getInstance().logError("Cannot parse patient's birthdate: " + birthDate);
                }
            }
            if(deathDate!=null) {
                try {
                    p.setDeathDate(dateFormatter.parse(deathDate));
                } catch (ParseException e) {
                    ConsoleLogger.getInstance().logError("Cannot parse patient's deathdate: " + deathDate);
                }
            }
            if(sex!=null) {
                sex = sex.toUpperCase().trim();
                Utils.createPAV(genderNominal_, sex, p);
            }
            if(origin!=null) {
                Utils.addCountryOrGeographicOrigin(countryOfOriginA, geographicOriginA, origin.replaceAll("\"",""), p);
            }
            if(nationality!=null) {
            	//TODO
                //setset.add(nationality);
            }
            if(transmission!=null) {
                Utils.createPAV(transmissionA, transmission, p);
            }
            if(adherence!=null) {
                if(therapyAdherenceT.createTestResult(p, adherence)==null) {
                    ConsoleLogger.getInstance().logError("Cannot map adherence value: " + adherence);
                }
            }
            
            Element testsEl = patientEl.getChild("Tests");
            if(testsEl!=null) {
                for(Object analysis :  testsEl.getChildren("Analysis")) {
                    Element analysisEl = (Element)analysis;
                    parseAnalysis(analysisEl, p);
                }
            }
            
            Element therapyEl = patientEl.getChild("Therapy");
            if(therapyEl!=null)
                ParseMedication.parseTherapy(therapyEl, p);
        } else {
            ConsoleLogger.getInstance().logError("No patient id found for:" + consultId);
        }
    }
    
    private void parseAnalysis(Element analysisEl, Patient p) {
        String collectionDate = text(analysisEl, "CollectionDate");
        String type = analysisEl.getAttributeValue("Parameter");
        String value = text(analysisEl, "Value");
        String unit = text(analysisEl, "Unit");
        
        TestResult tr = getTest(type, unit, value, p);
        if(tr!=null) {
            try {
                tr.setTestDate(dateFormatter.parse(collectionDate));
            } catch (ParseException e) {
                ConsoleLogger.getInstance().logError("Cannot parse test date: " + collectionDate);
            }
        }
    }
    
    private TestResult getTest(String type, String unit, String value, Patient p) {
        type = type.trim();
        if(unit!=null)
            unit = unit.trim();
        else
            unit = "";
        
        if(type.equals("ABSCD4")) {
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse CD4 value: " + value);
                return null;
            }
            TestResult tr = p.createTestResult(StandardObjects.getGenericCD4Test());
            tr.setValue(value);
            return tr;
        } if(type.equals("CD4")) {
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse CD4% value: " + value);
                return null;
            }
            TestResult tr = p.createTestResult(StandardObjects.getGenericCD4PercentageTest());
            tr.setValue(value);
            return tr;
        } else if(type.equals("ABSCD8")) {
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse CD8 value: " + value);
                return null;
            }
            TestResult tr = p.createTestResult(StandardObjects.getGenericCD8Test());
            tr.setValue(value);
            return tr;
        } else if(type.equals("CD8")) {
            try {
                Double.parseDouble(value);
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse CD8% value: " + value);
                return null;
            }
            TestResult tr = p.createTestResult(StandardObjects.getGenericCD8PercentageTest());
            tr.setValue(value);
            return tr;
        } else if(type.equals("H2VL") || type.equals("HIVVL")) {
            String val = parseViralLoad(value);
            if(val==null)
            	return null;
            TestResult tr = p.createTestResult(StandardObjects.getGenericViralLoadTest());
            tr.setValue(val);
            return tr;
        } else if(type.equals("VLLOGlog10") || type.equals("H2VLLlog10") || type.equals("VLLOG")) {
            String val = parseViralLoad(value);
            if(val==null)
            	return null;
            TestResult tr = p.createTestResult(StandardObjects.getGenericViralLoadLog10Test());
            tr.setValue(val);
            return tr;
        }
        
        setset.add(type);
        
        return null;
    }
    
    public static String parseViralLoad(String value_orig) {
        String value = value_orig;
        
        if(value==null || "".equals(value)) {
            return null;
        }
        
        String val = null;
        
        value = value.replace(',', '.');
        
        if(Character.isDigit(value.charAt(0))) {
            value = "=" + value;
        }
        if(value.charAt(0)=='>' || value.charAt(0)=='<' || value.charAt(0)=='=') {
            try {
                Double.parseDouble(value.substring(1));
                val = value;
            } catch(NumberFormatException nfe) {
                ConsoleLogger.getInstance().logError("Cannot parse Viral Load value: " + value);
                return null;
            }
        }
        
        return val;
    }
    
    private String text(Element el, String name) {
        if(el.getChild(name)==null)
            return null;
        String toReturn = el.getChild(name).getText();
        el.getChild(name).detach();
        return toReturn;
    }
}
