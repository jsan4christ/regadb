package net.sf.regadb.db;

import java.util.HashSet;
import java.util.Set;

public class PatientImplHelper {
    public static boolean canAccessViralIsolate(ViralIsolate vi, Set<Dataset> datasets) {
        return canAccesPI(vi.getPatient(), datasets);
    }
    
    public static boolean canAccessPatient(Patient p, Set<Dataset> datasets) {
        return canAccesPI(p.getPatient(), datasets);
    }
    
    public static boolean canAccessPatientAttributeValue(PatientAttributeValue patientAttributeValuevar, Set<Dataset> datasets) {
        return canAccesPI(patientAttributeValuevar.getId().getPatient(), datasets);
    }
    
    public static boolean canAccessTestResult(TestResult testResultvar, Set<Dataset> datasets) {
        return canAccesPI(testResultvar.getPatient(), datasets);
    }

    public static boolean canAccessTherapy(Therapy therapyvar, Set<Dataset> datasets) {
        return canAccesPI(therapyvar.getPatient(), datasets);
    }
    
    private static boolean canAccesPI(PatientImpl p, Set<Dataset> datasets) {
        Set<Dataset> pDatasets = new HashSet<Dataset>();
        for(PatientDataset pd : p.getPatientDatasets()) {
            pDatasets.add(pd.getId().getDataset());
        }
        pDatasets.retainAll(datasets);
        return pDatasets.size()>0;
    }
    
    public static boolean isInstanceOfPatientImpl(Object o) {
        return (o instanceof PatientImpl);
    }
    
    public static Patient castPatientImplToPatient(Object o, Set<Dataset> datasets) {
        //get privillege for this patient by doing a query
        //only call this method once in the getCsvLineSwitch method!!
        
        return new Patient((PatientImpl)o, 1);
    }
}
