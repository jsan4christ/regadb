package net.sf.regadb.ui.form.singlePatient;


import java.util.Collection;
import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WTable;

public class ViralIsolateResistanceForm extends WContainerWidget
{
    private ViralIsolateForm viralIsolateForm_;
    
    private ViralIsolateResistanceTable resistanceTable_;
    private ViralIsolateResistanceTableLegend resistanceTableLegend_;
    private WPushButton refreshButton_;
    private WCheckBox showMutations_;
    
    public ViralIsolateResistanceForm(ViralIsolateForm viralIsolateForm)
    {
        super();
        viralIsolateForm_ = viralIsolateForm;

        init();
    }

    public void init()
    {
        WTable wrapper = new SimpleTable(this);
        wrapper.getElementAt(0, 0).setStyleClass("navigation");
        wrapper.getElementAt(1, 0).setStyleClass("tablewrapper");
        
        resistanceTable_ = new ViralIsolateResistanceTable(wrapper.getElementAt(1, 0));
        resistanceTableLegend_ = new ViralIsolateResistanceTableLegend(wrapper.getElementAt(2,0));
        
        refreshButton_ = new WPushButton(tr("form.viralIsolate.editView.resistance.refreshButton"), wrapper.getElementAt(0, 0));
        refreshButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a) 
                    {
                        refreshTable();
                    }
                });
        
        showMutations_ = new WCheckBox(tr("form.viralIsolate.editView.resistance.showMutationsCB"), wrapper.getElementAt(0, 0));
        showMutations_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        
        
        //TODO
        //is this still required?????
        
        // delay table loading so IE doesn't get confused by the
        // massive amount of changes
        UIUtils.singleShot(this, 200, new Signal.Listener() {
            public void trigger() {
                refreshTable();
            }
        });
    }
    
	private void refreshTable() {
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.refresh(viralIsolateForm_.getViralIsolate());
        
        Genome genome = viralIsolateForm_.getViralIsolate().getGenome();
        Collection<String> drugClasses = getRelevantDrugClassIds(t, viralIsolateForm_.getViralIsolate().getViralIsolateIi());
        
        TestType gssTestType = (genome == null ? null : StandardObjects.getTestType(StandardObjects.getGssDescription(),genome));
        resistanceTable_.loadTable(drugClasses, showMutations_.isChecked(), viralIsolateForm_.getViralIsolate().getTestResults(),gssTestType);
        
        t.commit();
    }
    
    @SuppressWarnings("unchecked")
	static Collection<String> getRelevantDrugClassIds(Transaction t, int viralIsolateIi){
        List<String> proteins = t.createQuery("select distinct(p.abbreviation)" +
        		" from AaSequence aas join aas.protein p join aas.ntSequence nt" +
        		" where nt.viralIsolate.id="+ viralIsolateIi).list();
        return ViralIsolateFormUtils.getRelevantDrugClassIds(proteins);
    }
}
