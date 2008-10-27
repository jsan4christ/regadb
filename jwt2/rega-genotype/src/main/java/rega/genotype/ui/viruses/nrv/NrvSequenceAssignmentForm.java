package rega.genotype.ui.viruses.nrv;

import java.io.File;
import java.io.IOException;

import rega.genotype.ui.data.OrganismDefinition;
import rega.genotype.ui.data.SaxParser;
import rega.genotype.ui.forms.IDetailsForm;
import rega.genotype.ui.util.GenotypeLib;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WBreak;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class NrvSequenceAssignmentForm extends IDetailsForm {
	public NrvSequenceAssignmentForm() {
	}

	@Override
	public void fillForm(SaxParser p, final OrganismDefinition od, File jobDir) {
		WContainerWidget block = new WContainerWidget(this);
		block.setStyleClass("dsa-text");

		block.addWidget(new WText(tr("defaultSequenceAssignment.sequenceName")));
		block.addWidget(new WText(lt(p.getEscapedValue("genotype_result.sequence[name]")+", ")));
		block.addWidget(new WText(tr("defaultSequenceAssignment.sequenceLength")));
		block.addWidget(new WText(lt(p.getEscapedValue("genotype_result.sequence[length]"))));
		block.addWidget(new WBreak());

		block = new WContainerWidget(this);

		String blastConclusion = NrvResults.getBlastConclusion(p);
		if (!blastConclusion.equals(NrvResults.NA)) {
			block.addWidget(new WText(lt("Assignment: " + blastConclusion + "<br />")));
			block.addWidget(new WText(lt("Motivation: " + NrvResults.getBlastMotivation(p))));
		} else {
			block.addWidget(new WText(lt("ORF1 assignment: " + NrvResults.getConclusion(p, "ORF1") + "<br />")));
			block.addWidget(new WText(lt("Motivation: " + NrvResults.getMotivation(p, "ORF1"))));

			block = new WContainerWidget(this);

			block.addWidget(new WText(lt("ORF2 assignment: " + NrvResults.getConclusion(p, "ORF2") + "<br />")));
			block.addWidget(new WText(lt("Motivation: " + NrvResults.getMotivation(p, "ORF2"))));
		}
		
		int start = 0;
		int end = 0;
		try {
			start = Integer.parseInt(p.getValue("genotype_result.sequence.result['blast'].start"));
			end = Integer.parseInt(p.getValue("genotype_result.sequence.result['blast'].end"));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			WImage genome = GenotypeLib.getWImageFromFile(od.getGenome().getGenomePNG(jobDir, p.getSequenceIndex(), "-", start, end, 0, "nrv", null));
			block.addWidget(genome);
		} catch (IOException e) {
			e.printStackTrace();
		}

		block = new WContainerWidget(this);

		WString refSeq = tr("defaultSequenceAssignment.referenceSequence");
		refSeq.arg(start);
		refSeq.arg(end);
		refSeq.arg(p.getEscapedValue("genotype_result.sequence.result['blast'].refseq"));
		WText refSeqWidget = new WText(refSeq);
		refSeqWidget.setStyleClass("refseq");
		block.addWidget(refSeqWidget);
	}

	@Override
	public WString getComment() {
		return null;
	}

	@Override
	public WString getTitle() {
		return tr("defaultSequenceAssignment.title");
	}

	@Override
	public WString getExtraComment() {
		return null;
	}
}