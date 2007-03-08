package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.witty.wt.widgets.SignalListener;
import net.sf.witty.wt.widgets.WContainerWidget;
import net.sf.witty.wt.widgets.WFont;
import net.sf.witty.wt.widgets.WFontGenericFamily;
import net.sf.witty.wt.widgets.WGroupBox;
import net.sf.witty.wt.widgets.WTable;
import net.sf.witty.wt.widgets.event.WEmptyEvent;

public class ViralIsolateProteinForm extends WContainerWidget
{
	private ViralIsolateForm viralIsolateForm_;
	
	private WGroupBox proteinGroup_;
	private Label ntSequenceComboL_;
	private ComboBox ntSequenceCombo_;
	private Label aaSequenceComboL_;
	private ComboBox aaSequenceCombo_;
	private WTable proteinGroupTable_;
	private Label proteinL;
	private TextField proteinTF;
	private Label regionL;
	private TextField regionTF;
	private Label alignmentL;
	private TextField alignmentTF;
	private Label synonymousL;
	private TextField synonymousTF;
	private Label nonSynonymousL;
	private TextField nonSynonymousTF;
	
	public ViralIsolateProteinForm(ViralIsolateForm viralIsolateForm)
	{
		super();
		viralIsolateForm_ = viralIsolateForm;

		init();
	}

	public void init()
	{
		proteinGroup_ = new WGroupBox(tr("form.viralIsolate.editView.group.protein"), this);
		proteinGroupTable_ = new WTable(proteinGroup_);
		ntSequenceComboL_ = new Label(tr("form.viralIsolate.editView.label.ntSequence"));
		ntSequenceCombo_ = new ComboBox(InteractionState.Editing, null);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, ntSequenceComboL_, ntSequenceCombo_);
		aaSequenceComboL_ = new Label(tr("form.viralIsolate.editView.label.aaSequence"));
		aaSequenceCombo_ = new ComboBox(InteractionState.Editing, null);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, aaSequenceComboL_, aaSequenceCombo_);
		proteinL = new Label(tr("form.viralIsolate.editView.label.protein"));
		proteinTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, proteinL, proteinTF);
		regionL = new Label(tr("form.viralIsolate.editView.label.region"));
		regionTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, regionL, regionTF);
		alignmentL = new Label(tr("form.viralIsolate.editView.label.alignment"));
		alignmentTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		alignmentTF.decorationStyle().setFont(new WFont(WFontGenericFamily.Monospace, "Courier"));
		viralIsolateForm_.addLineToTable(proteinGroupTable_, alignmentL, alignmentTF);
		synonymousL = new Label(tr("form.viralIsolate.editView.label.synonymous"));
		synonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, synonymousL, synonymousTF);
		nonSynonymousL = new Label(tr("form.viralIsolate.editView.label.nonSynonymous"));
		nonSynonymousTF = new TextField(viralIsolateForm_.getInteractionState(), viralIsolateForm_);
		viralIsolateForm_.addLineToTable(proteinGroupTable_, nonSynonymousL, nonSynonymousTF);
	}
	
	void fillData(ViralIsolate vi)
	{
		for(NtSequence ntseq : vi.getNtSequences())
		{
			if(ntseq.getAaSequences().size()!=0)
			{
				ntSequenceCombo_.addItem(new DataComboMessage<NtSequence>(ntseq, ntseq.getNtSequenceIi()+""));
			}
		}
		
		setAaSequenceCombo();
		setAaData();
		
		ntSequenceCombo_.addComboChangeListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
					{
						setAaSequenceCombo();
					}
				});
		
		aaSequenceCombo_.addComboChangeListener(new SignalListener<WEmptyEvent>()
				{
					public void notify(WEmptyEvent a)
					{
						setAaData();
					}
				});
			
	}
	
	private void setAaData()
	{
		AaSequence aaSequence = ((DataComboMessage<AaSequence>)aaSequenceCombo_.currentText()).getValue();
		
		proteinTF.setText(aaSequence.getProtein().getAbbreviation());
		regionTF.setText(aaSequence.getFirstAaPos() + " - " + aaSequence.getLastAaPos());
	}
	
	private void setAaSequenceCombo()
	{
		DataComboMessage<NtSequence> currentSequence = (DataComboMessage<NtSequence>)ntSequenceCombo_.currentText();
		
		for(AaSequence aaseq : currentSequence.getValue().getAaSequences())
		{
			aaSequenceCombo_.addItem(new DataComboMessage<AaSequence>(aaseq, aaseq.getAaSequenceIi()+""));
		}
	}
}
