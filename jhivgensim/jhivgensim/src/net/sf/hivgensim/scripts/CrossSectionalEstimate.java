package net.sf.hivgensim.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.hivgensim.fastatool.FastaConcat;
import net.sf.hivgensim.fastatool.FastaToNexus;
import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.RemoveMixtures;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.CleanSequences;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.GetTreatedSequences;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.hivgensim.queries.output.SequencesToFasta;
import net.sf.hivgensim.queries.output.ToObjectList;
import net.sf.hivgensim.services.BnLearner;
import net.sf.hivgensim.services.Estimate;
import net.sf.hivgensim.services.Paup;
import net.sf.hivgensim.treecluster.TreeNode;
import net.sf.hivgensim.treecluster.TreeParser;
import net.sf.hivgensim.treecluster.TreeWeights;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.tools.MutPos;

public class CrossSectionalEstimate {

	//initialized variables
	private String workDir = "/home/gbehey0/3tcd4t";
	private String snapshotFile = "/home/gbehey0/snapshot";
	private String[] naiveDrugClasses = {"NRTI","NNRTI"};
	private String[] drugs = {"3TC","D4T"};
	//	private String organismName = "HIV-1";
	//	private String orfName = "pol";
	//	private String proteinAbbreviation = "RT";
	private SelectionWindow[] smallWindows = new SelectionWindow[]{SelectionWindow.RT_WINDOW_CLEAN};
	private SelectionWindow[] fullWindows = new SelectionWindow[]{SelectionWindow.RT_WINDOW_REGION};
	private double threshold = 0.01;
	private boolean lumpValues = false;

	//bayesian parameters
//	private int ESS = 1;
//	private String ARC_OPTS ="";
//	private int sa_T0=10;
//	private double sa_mu_T=1.002;
//	private int sa_iterations=40000;
//	private double arc_cost=1.2;
	//TODO make these editable in the service
//	double ess,
//	int iterations,
//	int coolings,
//	double paramCost,
	
	public CrossSectionalEstimate(){

	}	

	public void run() throws FileNotFoundException{
		//queries

		//naive
		ToObjectList<NtSequence> tol = new ToObjectList<NtSequence>();
		QueryInput query =  new FromSnapshot(new File(snapshotFile),
				new GetDrugClassNaiveSequences(naiveDrugClasses,
						new CleanSequences(smallWindows,
								tol)));
		query.run();
		//		List<NtSequence> naive = tol.getList();
		new SequencesToFasta(new File(workDir + File.separator + "naive.fasta")).output(tol);

		//treated
		tol = new ToObjectList<NtSequence>();
		query =	new FromSnapshot(new File(snapshotFile),
				new GetTreatedSequences(drugs,
						new CleanSequences(smallWindows,
								tol)));
		query.run();
		List<NtSequence> treated = tol.getList();
		new SequencesToFasta(new File(workDir + File.separator + "treated.fasta")).output(tol);

		//both
		FastaConcat fc = new FastaConcat(
				workDir + File.separator + "naive.fasta",
				workDir + File.separator + "treated.fasta",
				workDir + File.separator + "phylo.fasta");
		fc.processFastaFile();

		//mutation table
		MutationTable mt = new MutationTable(treated,fullWindows);
		mt.exportAsCsv(new FileOutputStream(new File(workDir + File.separator + "all.mutations.csv")));

		mt.removeInsertions();
		mt.removeUnknownMutations();
		mt.removeLowPrevalenceMutations(threshold,lumpValues);

		//TODO remove certain mutations: known/transmission/... ?

		mt.exportAsCsv(new FileOutputStream(workDir + File.separator + "all.mutations.selection.csv"),',', false);

		ArrayList<String> mutations = MutPos.execute(new String[]{
				workDir + File.separator + "mut.treated.selection.csv",
				workDir + File.separator + "mutations",
				workDir + File.separator + "positions",
				workDir + File.separator + "wildtypes"});

		mt.selectColumns(mutations);
		mt.exportAsCsv(new FileOutputStream(workDir + File.separator + "mut_treated.csv"),',', false);

		RemoveMixtures rm = new RemoveMixtures(mt);
		rm.removeMixtures();
		mt.exportAsCsv(new FileOutputStream(workDir + File.separator + "mut_treated_nomix.csv"),',', false);
		mt.deleteColumn(0);
		mt.exportAsVdFiles(
				new FileOutputStream(workDir + File.separator + "mut_treated.vd"),
				new FileOutputStream(workDir + File.separator + "mut_treated.idt"));

		FastaToNexus ftn = new FastaToNexus(
				workDir + File.separator + "phylo.fasta",
				workDir + File.separator + "phylo.nex");
		ftn.convert();
		
		//TODO install paup
		Paup p = new Paup();
		p.run(
				workDir + File.separator + "phylo.nex",
				workDir + File.separator + "tree.phy");

		try{
			TreeParser tp = new TreeParser(workDir + File.separator + "tree.phy");
			TreeNode root = tp.parseTree();
			TreeWeights tw = new TreeWeights(TreeWeights.WEIGHT_FOR_RT);
			tw.calculateWeights(root);
			PrintStream out = new PrintStream(new FileOutputStream(workDir + File.separator + "weights50.csv"));
			out.println(root.printWeights());
		}catch(Exception e){
			e.printStackTrace();
			throw new Error("treeparsing or -weighting failed");
		}
		
		BnLearner bnl = new BnLearner();
		bnl.run(
				workDir + File.separator + "mut_treated.vd",
				workDir + File.separator + "mut_treated.idt",
				workDir + File.separator + "mut_treated.str");
		
		//TODO better way
		PrintStream out = new PrintStream(new File(workDir + File.separator + "doublepositions"));
		out.println("215 215\n151 151\n");
		out.flush();
		out.close();
		//TODO better way		
		out = new PrintStream(new File(workDir + File.separator + "mutagenesis"));
		out.println("");
		out.flush();
		out.close();
		
		//TODO check service script
		Estimate estimate = new Estimate();
		estimate.run(
				workDir + File.separator + "mut_treated.csv",
				workDir + File.separator + "naive.fasta",
				workDir + File.separator + "mut_treated.idt",
				workDir + File.separator + "mut_treated.str",
				workDir + File.separator + "mut_treated.vd",
				workDir + File.separator + "wildtypes",
				workDir + File.separator + "doublepositions",
				workDir + File.separator + "mutagenesis",
				workDir + File.separator + "weights50.csv",
				workDir + File.separator + "best.cft",
				workDir + File.separator + "estimate.diag");
	}




}

