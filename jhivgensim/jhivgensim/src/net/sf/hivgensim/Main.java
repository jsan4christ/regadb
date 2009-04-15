package net.sf.hivgensim;

import java.io.FileNotFoundException;

import net.sf.hivgensim.scripts.CrossSectionalEstimate;

public class Main {
	
	public static void main(String[] args){
//		try {
//			FastaSubtype.main(args);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		//longitudinal kristof
//		Table t = new Table();
//		QueryInput q = 	new FromSnapshot(new File("/home/gbehey0/snapshot"),
//					new GetPatientsFromDatasets(new String[]{"stanford","egazmoniz","ghb"},
//				    new GetLongitudinalSequencePairs(new String[]{"AZT","3TC"},false,
//				    new CheckForRegion("HIV-1","RT",
//				    new SequencePairsTableOutput(t,new File("/home/gbehey0/long.out"),TableOutputType.CSV)
//				    )))); 
//		q.run();
		
		
		
		
		try {
			new CrossSectionalEstimate().run();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

//	private final static String workDir = "/home/gbehey0/regadb";
//
//	public static void main(String[] args){
//		System.out.println("Clearing working directory");
//		clearDirectory(new File(workDir));
//		Jarbuilder.run(
//				workDir + File.separator + "build",
//				workDir + File.separator + "report",
//				"/home/gbehey0/workspace",false);
//
//
//	}
//
//	public static void clearDirectory(File path) {
//		if(path.exists()) {
//			File[] files = path.listFiles();
//			for(int i=0; i<files.length; i++) {
//				if(files[i].isDirectory()) {
//					deleteDirectory(files[i]);
//				}
//				else {
//					files[i].delete();
//				}
//			}
//		}		
//	}
//
//	public static void deleteDirectory(File path){
//		clearDirectory(path);
//		path.delete();
//	}


}
