package net.sf.hivgensim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.sf.hivgensim.fastatool.FastaClean;
import net.sf.hivgensim.fastatool.FastaRegion;
import net.sf.hivgensim.fastatool.SelectionWindow;
import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.Utils;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.GetTreatedSequences;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.hivgensim.queries.output.SequencesToFasta;
import net.sf.hivgensim.services.SequenceTool;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

import org.biojava.bio.BioException;

public class Hivgensim {
	
	public static void main(String[] args) {
		long startTime;
		long stopTime;
		
		String workDir = "/home/gbehey0/hivgensim";
		String[] naiveDrugClasses = {"NRTI"};
		String[] drugs = {"AZT","3TC"};
		String organismName = "HIV-1";
		String orfName = "pol";
		String proteinAbbreviation = "RT";
		double threshold = 0.01;
		boolean lumpValues = false;
		
		Login login = null;
		try {
			login = Login.authenticate("gbehey0", "bla123");
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
		
		SelectionWindow[] windows = new SelectionWindow[]{
				new SelectionWindow(Utils.getProtein(login, organismName, orfName, proteinAbbreviation),44,200)
		};
		
		//queries
		//input and output
		startTime = System.currentTimeMillis();
		QueryInput input = new FromSnapshot(new File("/home/gbehey0/snapshot"));
		input.getOutputList();
		stopTime = System.currentTimeMillis();
		System.out.println("time to read in snapshot: "+(stopTime-startTime)+" ms");
		
		//naive
		startTime = System.currentTimeMillis();
		Query<NtSequence> qn = new GetDrugClassNaiveSequences(input,naiveDrugClasses);
		QueryOutput<NtSequence> output = new SequencesToFasta(new File(workDir + File.separator + "naive.seqs.fasta"));
		output.generateOutput(qn);
		stopTime = System.currentTimeMillis();
		System.out.println("found "+ qn.getOutputList().size() + " naive seqs in "+(stopTime-startTime)+" ms");
		
		//experienced
		startTime = System.currentTimeMillis();
		Query<NtSequence> qe = new GetTreatedSequences(input,drugs);
		output = new SequencesToFasta(new File(workDir + File.separator + "treated.seqs.fasta"));
		output.generateOutput(qe);
		stopTime = System.currentTimeMillis();
		System.out.println("found "+ qe.getOutputList().size() + " treated seqs in "+(stopTime-startTime)+" ms");
		
		//align
		SequenceTool st = new SequenceTool();
		Utils.createReferenceSequenceFile(login, organismName, orfName, workDir + File.separator + "reference.fasta");
		st.align(workDir + File.separator + "reference.fasta",
				 workDir + File.separator + "naive.seqs.fasta",
				 workDir + File.separator + "aligned.naive.fasta");
		
		st.align(workDir + File.separator + "reference.fasta",
				 workDir + File.separator + "treated.seqs.fasta",
				 workDir + File.separator + "aligned.treated.fasta");
		
		//clean: remove too short seqs
		try {
			FastaClean fc = new FastaClean(
					workDir + File.separator + "aligned.treated.fasta",
					workDir + File.separator + "clean.treated.fasta",
					windows);
			fc.processFastaFile();
			
			fc = new FastaClean(
					workDir + File.separator + "aligned.naive.fasta",
					workDir + File.separator + "clean.naive.fasta",
					windows);
			fc.processFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		try {
			FastaRegion fr = new FastaRegion(
					workDir + File.separator + "clean.naive.fasta",
					workDir + File.separator + "dna_naive.fasta",
					windows);
			fr.processFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		try {
			MutationTable mt = new MutationTable(workDir + File.separator + "clean.treated.fasta",windows);
			mt.exportAsCsv(new FileOutputStream(new File(workDir + File.separator + "all.mut.treated.csv")));
			
			mt.removeInsertions();
			mt.removeUnknownMutations();
			mt.removeLowPrevalenceMutations(threshold,lumpValues);
						
			mt.exportAsCsv(new FileOutputStream(workDir + File.separator + "mut.treated.selection.csv"),',', false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BioException e) {
			e.printStackTrace();
		}
	}

}