package net.sf.regadb.ui.form.query.querytool.buttons;

import net.sf.regadb.ui.form.query.querytool.awceditor.WAWCEditorPanel;
import net.sf.regadb.ui.form.query.querytool.dialog.SelectClauseDialog;
import net.sf.regadb.ui.form.query.querytool.tree.QueryTreeNode;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;

public class SelectClauseButtonPanel extends ButtonPanel {
	private QueryTreeNode owner;
	private SelectClauseDialog dialog;
	
	public SelectClauseButtonPanel(QueryTreeNode owner, SelectClauseDialog dialog) {
		super(Style.Default);
		this.owner = owner;
		this.dialog = dialog;
		init();
	}
	
	private void init() {
		WPushButton okButton = new WPushButton(tr("form.general.button.ok"));
		addButton(okButton);

		WPushButton cancelButton = new WPushButton(tr("form.general.button.cancel"));
		addButton(cancelButton);

		okButton.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				WAWCEditorPanel panel = dialog.getSelectedClause();
				if (panel == null) {
					cancel();
				}
				else {
					panel.applyEditings();
					owner.getParentNode().addNode(panel.getClause());
					removeDialog();
				}
			}
		});
		
		cancelButton.clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				cancel();
			}
		});
	}
	
	private void cancel() {
		removeDialog();
	}
	
	private void removeDialog() {
		owner.showContent();
		owner.getParentNode().removeChildNode(owner);
	}

}