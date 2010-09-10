package net.sf.regadb.ui.tree.items.query;

import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.action.ITreeAction;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import eu.webtoolkit.jwt.WTreeNode;

public class QueryDefinitionRunItem extends TreeMenuNode
{
    public QueryDefinitionRunItem(WTreeNode root)
    {
        super(tr("menu.query.definition.run"), root);
    }
    
    @Override
	public ITreeAction getFormAction()
	{
		return new ITreeAction()
		{
			public void performAction(TreeMenuNode node)
			{
			    getChildren().get(0).prograSelectNode();
			}
		};
	}
    
    @Override
    public boolean isDisabled()
    {
        return RegaDBMain.getApp().getLogin() == null;
    }
}
