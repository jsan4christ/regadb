package net.sf.regadb.build.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnTools 
{
    public static void checkout(String url, String projectName, String localDir, SVNRepository repository) throws Exception
    {
        long latestRevision;
        
        try
        {
            System.out.println("Checking out svn project: " + projectName);

            latestRevision = repository.getLatestRevision();
            SVNClientManager.newInstance().getUpdateClient().doCheckout(
                    SVNURL.parseURIDecoded(url + "/" + projectName),
                    new File(localDir + projectName),
                    SVNRevision.create(latestRevision),
                    SVNRevision.create(latestRevision), true);
        }
        catch(Exception e)
        {
        	throw new Exception(e.getMessage());
        }
    }
    
    public static List<String> getModules(SVNRepository repository)
    {
        List<String> modules = new ArrayList<String>(); 
        Collection entries = null;
        try 
        {
            entries = repository.getDir( "", -1 , null , (Collection) null );
        } 
        catch (SVNException e) 
        {
            e.printStackTrace();
        }
        Iterator iterator = entries.iterator( );
        while ( iterator.hasNext( ) )
        {
            SVNDirEntry entry = ( SVNDirEntry ) iterator.next( );
            modules.add(entry.getName( ));
        }
        return modules;
    }
    
    public static SVNRepository getSVNRepository(String url, String user, String password)
    {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        
        SVNRepository repository = null;
        
        try 
        {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
        } 
        catch (SVNException e) 
        {
            e.printStackTrace();
        }
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(user, password);
        repository.setAuthenticationManager(authManager);
        
        return repository;
    }
    
    public static void main(String [] args)
    {
        SVNRepository svnrepos = getSVNRepository("svn+ssh://zolder:3333/var/svn/repos", "jvsant1", "Kangoer1" );
        List<String> modules = getModules(svnrepos);
        for(String m : modules)
        {
            System.out.println(m);
        }
        try 
        {
        	checkout("svn+ssh://zolder:3333/var/svn/repos", "regadb-util", "/home/plibin0/regadb_build/", svnrepos);
        }
        catch (Exception e) 
        {
        	
        }
        // if(s.equals("regadb-sql") || s.equals("regadb-build") ||
        // s.equals("test_svn"))

    }
}
