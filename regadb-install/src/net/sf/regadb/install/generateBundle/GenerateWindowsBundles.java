package net.sf.regadb.install.generateBundle;

import java.io.File;

import org.apache.catalina.ant.DeployTask;
import org.apache.tools.ant.taskdefs.GUnzip;
import org.apache.tools.ant.taskdefs.Untar;

public class GenerateWindowsBundles {
    public void unpackJavaTomcat(String buildPath, String bundlePath) {
        String javaTomcatLocation = buildPath + replaceByPS("/regadb-install/src/net/sf/regadb/install/generateBundle/winResources/");
        tarxzvf(javaTomcatLocation + "jre.tgz", bundlePath);
        tarxzvf(javaTomcatLocation + "tomcat.tgz", bundlePath);
    }
    
    public void deployRegaDB(String buildPath) {
        DeployTask deploy = new DeployTask();
        deploy.setDescription("Install RegaDB");
        deploy.setUrl("http://localhost:8080/manager");
        deploy.setUsername("regadb_user");
        deploy.setPassword("regadb_password");
        deploy.setPath("/regadb");
        deploy.setWar(buildPath + replaceByPS("/regadb-ui/dist/regadb-ui-0.9.war"));
    }
    
    private void tarxzvf(String tarGzFile, String destPath) {
        GUnzip gunzip = new GUnzip();
        gunzip.setSrc(new File(tarGzFile));
        File tarFile = new File(tarGzFile.substring(0, tarGzFile.lastIndexOf('.'))+".tar");
        gunzip.setDest(tarFile);
        gunzip.execute();
        
        Untar untar = new Untar();
        untar.setSrc(tarFile);
        untar.setDest(new File(destPath+File.separatorChar));
        untar.execute();
        
        tarFile.delete();
    }
    
    private String replaceByPS(String path) {
        return path.replaceAll("/", File.separatorChar+"");
    }
    
    public static void main(String [] args) {
        GenerateWindowsBundles gen = new GenerateWindowsBundles();
        gen.unpackJavaTomcat("/home/plibin0/regadb_build", "/home/plibin0/regadb_bundle");
    }
}
