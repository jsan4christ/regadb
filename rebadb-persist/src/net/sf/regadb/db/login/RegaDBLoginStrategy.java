package net.sf.regadb.db.login;

import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.encrypt.Encrypt;

public class RegaDBLoginStrategy implements ILoginStrategy
{	
	public boolean authenticate(String uid, String password, Login login)
	{
        Transaction t = login.createTransaction();
        
        SettingsUser settings
            = t.getSettingsUser(uid, Encrypt.encryptMD5(password));
        
        t.commit();
        
        return settings != null;
	}
}