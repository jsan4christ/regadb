package rega.genotype.ui.forms.details;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import net.sf.witty.wt.WBreak;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WImage;
import net.sf.witty.wt.WResource;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;
import rega.genotype.ui.data.OrganismDefinition;
import rega.genotype.ui.data.SaxParser;
import rega.genotype.ui.forms.IDetailsForm;
import rega.genotype.ui.util.GenotypeLib;

public class DefaultSequenceAssignmentForm extends IDetailsForm {
	private WTable mainTable;
	private WContainerWidget text;
	private WContainerWidget motivation;
	public DefaultSequenceAssignmentForm() {
		mainTable = new WTable(this);
		text = new WContainerWidget(mainTable.elementAt(0, 0));
		motivation = new WContainerWidget(mainTable.elementAt(3, 0));
	}

	@Override
	public void fillForm(SaxParser p, final OrganismDefinition od, File jobDir) {
		String id;
		if(!p.elementExists("genotype_result.sequence.conclusion")) {
			id = "-";
		} else {
			id = p.getValue("genotype_result.sequence.conclusion.assigned.id");
		}
			
		text.clear();
		text.addWidget(new WText(tr("defaultSequenceAssignment.sequenceName")));
		text.addWidget(new WText(lt(p.getValue("genotype_result.sequence['name']")+", ")));
		text.addWidget(new WText(tr("defaultSequenceAssignment.sequenceLength")));
		text.addWidget(new WText(lt(p.getValue("genotype_result.sequence['length']"))));
		text.addWidget(new WBreak());
		text.addWidget(new WText(tr("defaultSequenceAssignment.assignment")));
		if(!p.elementExists("genotype_result.sequence.conclusion")) {
			text.addWidget(new WText(lt(" Sequence error")));
		} else {
			text.addWidget(new WText(lt(" " +p.getValue("genotype_result.sequence.conclusion.assigned.name"))));
		}
		text.addWidget(new WText(lt(", ")));
		text.addWidget(new WText(tr("defaultSequenceAssignment.bootstrap")));
		if(!p.elementExists("genotype_result.sequence.conclusion.assigned.support")) {
			text.addWidget(new WText(lt(" NA")));
		} else {
			text.addWidget(new WText(lt(" " +p.getValue("genotype_result.sequence.conclusion.assigned.support")+"%")));
		}
		
		int start = Integer.parseInt(p.getValue("genotype_result.sequence.result[blast].start"));
		int end = Integer.parseInt(p.getValue("genotype_result.sequence.result[blast].end"));
		String csvData = p.getValue("genotype_result.sequence.result[scan].data");
		try {
			WImage genome0 = GenotypeLib.getWImageFromFile(od.getGenome().getGenomePNG(jobDir, p.getSequenceIndex(), id, start, end, 1, "pure", csvData));
			WImage genome1 = GenotypeLib.getWImageFromFile(od.getGenome().getGenomePNG(jobDir, p.getSequenceIndex(), id, start, end, 0, "pure", csvData));
			WImage legend = GenotypeLib.getWImageFromResource(od, "legend.png", null);
			mainTable.putElementAt(1, 0, genome0);
			mainTable.putElementAt(2, 0, genome1);
			mainTable.putElementAt(1, 1, legend);
			mainTable.elementAt(1, 1).setRowSpan(2);
			
			motivation.clear();
			motivation.addWidget(new WBreak());
			motivation.addWidget(new WText(tr("defaultSequenceAssignment.motivation")));
			if(!p.elementExists("genotype_result.sequence.conclusion")) {
				motivation.addWidget(new WText(lt(p.getValue("genotype_result.sequence.error"))));
			} else {
				motivation.addWidget(new WText(lt(p.getValue("genotype_result.sequence.conclusion.motivation"))));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public WMessage getComment() {
		return null;
	}

	@Override
	public WMessage getTitle() {
		return tr("defaultSequenceAssignment.title");
	}

	@Override
	public WMessage getExtraComment() {
		return null;
	}
}