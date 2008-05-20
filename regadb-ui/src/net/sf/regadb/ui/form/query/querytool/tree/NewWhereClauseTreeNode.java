package net.sf.regadb.ui.form.query.querytool.tree;

import net.sf.regadb.ui.form.query.querytool.dialog.SelectClauseDialog;

public class NewWhereClauseTreeNode extends QueryTreeNode{
	
	
	
	public NewWhereClauseTreeNode(QueryEditorGroupBox editor, QueryTreeNode parent) {
		super(null, editor, parent);
		init();
	}
	
	private void init() {
		SelectClauseDialog dialog = new SelectClauseDialog(this);
		getStyleClasses().addStyle("atomictreenode");
		showDialog(dialog);
	}
	
	public void loadContent() {
		((SelectClauseDialog) getDialog()).loadContent(getParentNode().getClause());
	}
}
