/*
 * Created on Feb 8, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package rega.genotype.viruses.nrv;

import java.io.File;
import java.io.IOException;

import rega.genotype.AbstractSequence;
import rega.genotype.AlignmentAnalyses;
import rega.genotype.AnalysisException;
import rega.genotype.BlastAnalysis;
import rega.genotype.FileFormatException;
import rega.genotype.GenotypeTool;
import rega.genotype.ParameterProblemException;
import rega.genotype.PhyloClusterAnalysis;
import rega.genotype.ScanAnalysis;
import rega.genotype.Sequence;
import rega.genotype.SubSequence;
import rega.genotype.AlignmentAnalyses.Cluster;
import rega.genotype.AlignmentAnalyses.Taxus;

public class NRVTool extends GenotypeTool {
	enum GroupRegion {
		GroupI_ORF1,
		GroupI_ORF2,
		GroupII_ORF1,
		GroupII_ORF2
	}

    private AlignmentAnalyses nrv;
	private AlignmentAnalyses phyloAnalyses[] = new AlignmentAnalyses[4];
    private BlastAnalysis blastAnalysis;
    
    public NRVTool(File workingDir) throws IOException, ParameterProblemException, FileFormatException {
        nrv = readAnalyses("NRV/nrvblastaa.xml", workingDir);
        blastAnalysis = (BlastAnalysis) nrv.getAnalysis("blast");

        phyloAnalyses[GroupRegion.GroupI_ORF1.ordinal()] = readAnalyses("NRV/nrv-ORF1.xml", workingDir);
        phyloAnalyses[GroupRegion.GroupII_ORF1.ordinal()] = phyloAnalyses[GroupRegion.GroupI_ORF1.ordinal()];
        phyloAnalyses[GroupRegion.GroupI_ORF2.ordinal()] = readAnalyses("NRV/nrvI-ORF2.xml", workingDir);
        phyloAnalyses[GroupRegion.GroupII_ORF2.ordinal()] = readAnalyses("NRV/nrvII-ORF2.xml", workingDir);
        
    }

    public void analyze(AbstractSequence s) throws AnalysisException {
        BlastAnalysis.Result blastResult = blastAnalysis.run(s);
        
        if (blastResult.haveSupport()) {
        	Cluster c = blastResult.getConcludedCluster();

        	if (blastResult.isReverseCompliment())
        		s = s.reverseCompliment();

    		if (blastAnalysis.getRegions() != null) {
        		for (BlastAnalysis.Region region:blastAnalysis.getRegions()) {
        			if (region.overlaps(blastResult.getStart(), blastResult.getEnd(), 100)) {
        				int rs = Math.max(0, region.getBegin() - blastResult.getStart());
        				int re = Math.min(s.getLength(), s.getLength() - (blastResult.getEnd() - region.getEnd()));

        				AbstractSequence s2 = re > rs ? new SubSequence(s.getName(), s.getDescription(), s, rs, re) : s;
        				if (!phyloAnalysis(s2, c.getId(), region.getName()))
        					conclude(blastResult, "Assigned based on BLAST score &gt;= " + blastAnalysis.getCutoff(), region.getName());
        			}
        			
        			
        		}
        	}
        } else {
            conclude("Unassigned", "Unassigned because of BLAST score &lt; " + blastAnalysis.getCutoff());
        }
    }

	private boolean phyloAnalysis(AbstractSequence s, String groupId, String regionName) throws AnalysisException {
		System.err.println("Phylo analysis: GenoGroup = " + groupId + ", region = " + regionName);

		AlignmentAnalyses phylo = null;
		if (groupId.equals("I")) {
			if (regionName.equals("ORF1"))
				phylo = phyloAnalyses[GroupRegion.GroupI_ORF1.ordinal()];
			else if (regionName.equals("ORF2"))
				phylo = phyloAnalyses[GroupRegion.GroupI_ORF2.ordinal()];			
		} else if (groupId.equals("II")) {
			if (regionName.equals("ORF1"))
				phylo = phyloAnalyses[GroupRegion.GroupII_ORF1.ordinal()];
			else if (regionName.equals("ORF2"))
				phylo = phyloAnalyses[GroupRegion.GroupII_ORF2.ordinal()];			
		}
		
		if (phylo != null) {
			PhyloClusterAnalysis a = (PhyloClusterAnalysis) phylo.getAnalysis("phylo-" + regionName);
			
			PhyloClusterAnalysis.Result r = a.run(s);

			String phyloName = "phylogenetic analysis (" + regionName + ")";

			if (r.haveSupport()) {
				if (!variantPhyloAnalysis(s, phylo, regionName, r.getConcludedCluster()))
					conclude(r, "Supported with " + phyloName + " and bootstrap &gt;= 70", regionName);
			} else {
				if (r.getSupportInner() >= 95)
					conclude(r, "Supported with " + phyloName + " with bootstrap &lt; 70 but inner clustering support &gt;= 100", regionName);
				else
					conclude("Could not assign", "Not supported by " + phyloName, regionName);
			}

			return true;
		} else
			return false;
	}

	private boolean variantPhyloAnalysis(AbstractSequence s, AlignmentAnalyses phylo, String regionName, Cluster cluster) throws AnalysisException {
		String analysisName = "phylo-" + regionName + "-" + cluster.getId();

		if (!phylo.haveAnalysis(analysisName))
			return false;

		PhyloClusterAnalysis a = (PhyloClusterAnalysis) phylo.getAnalysis(analysisName);

		PhyloClusterAnalysis.Result r = a.run(s);
		
		String phyloName = "phylogenetic analysis (" + regionName + ")";

		if (r.haveSupport()) {
			conclude(r, "Supported with " + phyloName + " and bootstrap &gt;= 70", regionName);
			return true;
		} else {
			if (r.getSupportInner() >= 95) {
				conclude(r, "Supported with " + phyloName + " with bootstrap &lt; 70 but inner clustering support &gt;= 100", regionName);
				return true;
			} else
				return false;
		}
	}

	public void analyzeSelf() throws AnalysisException {
		for (int i = 0; i < 4; ++i) {
			if (phyloAnalyses[i] != null) {
				ScanAnalysis scan = (ScanAnalysis) phyloAnalyses[i].getAnalysis("scan-self");
				scan.run(null);
			}
		}
	}
}

