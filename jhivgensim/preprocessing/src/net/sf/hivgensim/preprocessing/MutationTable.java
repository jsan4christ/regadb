package net.sf.hivgensim.preprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import net.sf.hivgensim.fastatool.FastaScanner;
import net.sf.hivgensim.fastatool.FastaSequence;
import net.sf.hivgensim.fastatool.SelectionWindow;
import net.sf.regadb.align.Mutation;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.csv.Table;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

/**
 * needs to be changed to use the regadb algorithm fully
 * 
 * 
 * @author gbehey0
 * 
 */
public class MutationTable extends Table {

	SelectionWindow[] windows;
	File fastafile;

	public MutationTable(String fastaFilename, SelectionWindow[] windows) {
		super();
		this.windows = windows;
		this.fastafile = new File(fastaFilename);
		init();
	}
	
	private void init() {
		try {
			SymbolTokenization aatok = ProteinTools.getTAlphabet().getTokenization("token"); 
			FastaScanner scan = new FastaScanner(fastafile);
			if (!scan.hasNextSequence()) {
				return; // empty file
			}
			SymbolList alignedRef = DNATools.createDNA(scan.nextSequence().getSequence());
			SymbolList alignedTarget;
			FastaSequence fs;
			
			//create id column
			ArrayList<String> ids = new ArrayList<String>();
			ids.add("SeqId");
			addColumn(ids, 0);
			
			LocalAlignmentService las = new LocalAlignmentService();
			while (scan.hasNextSequence()) {
				fs = scan.nextSequence();
				alignedTarget = DNATools.createDNA(fs.getSequence());
				System.out.println(fs.getId());
				addRow(createNewRow(fs.getId()));
				for(Mutation m : las.getAlignmentResult(alignedTarget, alignedRef, true).getMutations()){
					for(SelectionWindow sw : windows){
						if(mutationInWindow(m,sw)){
							String protein = sw.getProtein().getAbbreviation();
							int position = m.getAaPos()-(sw.getProtein().getStartPosition()/3);
							for(Symbol aa : m.getTargetAminoAcids()){
								String mutString = protein + position + aatok.tokenizeSymbol(aa);
								int nbCol = findInRow(0, mutString);
								if(nbCol == -1){ //new mut
									addColumn(createNewColumn(mutString,numRows()-1),findOrderedPosition(mutString));
								}else{ //adjust mut
									setValue(nbCol, numRows()-1, "y");									
								}
							}
						}
					}
				}
			}
		} catch (IllegalSymbolException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BioException e) {
			e.printStackTrace();
		}
	}
	
	private int findOrderedPosition(String mutString){
		ArrayList<String> header = getRow(0);
		for(int i = 1 ; i<numColumns();i++){
			if(mutString.compareTo(header.get(i)) == -1){
				return i;
			}
		}
		return numColumns();
	}
	
	private ArrayList<String> createNewColumn(String mut, int index){
		ArrayList<String> newcol = new ArrayList<String>(numRows());
		newcol.add(mut);
		for(int i = 1;i<numRows();i++){
			if(i != index){
				newcol.add("n");
			}else{
				newcol.add("y");
			}			
		}
		return newcol;		
	}
	
	private ArrayList<String> createNewRow(String seqid){
		ArrayList<String> newrow = new ArrayList<String>(numColumns());
		newrow.add(seqid);
		for(int i = 1;i<numColumns();i++){
			newrow.add("n");						
		}
		return newrow;		
	}
	
	private boolean mutationInWindow(Mutation m, SelectionWindow sw){
		return m.getAaPos() >= (sw.getStartCheck()/3)+1
		&& m.getAaPos() <= (sw.getStopCheck()/3)+1;
	}
}
