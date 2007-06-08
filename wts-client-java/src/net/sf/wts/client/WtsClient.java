package net.sf.wts.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import net.sf.wts.client.util.AxisClient;
import net.sf.wts.client.util.Encrypt;

import org.apache.commons.io.FileUtils;

public class WtsClient 
{
    private String url_;
    private AxisClient axisService = new AxisClient();
    
    public WtsClient(String url)
    {
        url_ = url;
    }
    
    public String getChallenge(String userName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "GetChallenge");
        
        axisService.addParameter(userName);
        
        String challenge = "";
        
        challenge = axisService.callAndGetStringResult();
        
        return challenge;
    }
    
    public String login(String userName, String challenge, String password, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Login");
        
        axisService.addParameter(userName);
        axisService.addParameter(challenge);
        axisService.addParameter(Encrypt.encryptMD5(Encrypt.encryptMD5(password)+challenge));
        axisService.addParameter(serviceName);
        
        String sessionTicket = "";
        
        sessionTicket = axisService.callAndGetStringResult();
        
        return sessionTicket;
    }
    
    public void upload(String sessionTicket, String serviceName, String fileName, byte[] file) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Upload");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(fileName);
        axisService.addParameter(file);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
    }
    
    public byte[] download(String sessionTicket, String serviceName, String fileName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Download");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(fileName);
        
        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public void download(String sessionTicket, String serviceName, String fileName, File toWrite) throws RemoteException, MalformedURLException
    {
        byte[] array = download(sessionTicket, serviceName, fileName);
        if(array!=null)
        {
            try 
            {
                FileUtils.writeByteArrayToFile(toWrite, array);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    public void upload(String sessionTicket, String serviceName, String fileName, File localLocation) throws RemoteException, MalformedURLException
    {
        byte[] array = null;
        try 
        {
            array = FileUtils.readFileToByteArray(localLocation);;
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        upload(sessionTicket, serviceName, fileName, array);
    }
    
    public byte[] monitorLogFile(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorLogFile");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);

        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public byte[] monitorLogTail(String sessionTicket, String serviceName, int numberOfLines) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorLogTail");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        axisService.addParameter(numberOfLines);

        byte[] array = null;
        try 
        {
            array = axisService.callAndGetByteArrayResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        return array;
    }
    
    public String monitorStatus(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "MonitorStatus");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        String status = "";
        
        try 
        {
            status = axisService.callAndGetStringResult();
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }

        return status;
    }
    
    public void start(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Start");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    
    public void closeSession(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "CloseSession");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }
    
    public void stop(String sessionTicket, String serviceName) throws RemoteException, MalformedURLException
    {
        axisService.removeParameters();
        axisService.setServiceUrl(url_, "Stop");
        
        axisService.addParameter(sessionTicket);
        axisService.addParameter(serviceName);
        
        try 
        {
            axisService.call();
        } 
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public String getUrl() 
    {
        return url_;
    }
    
    public static void main(String [] args)
    {
    	System.setProperty("http.proxyHost", "www-proxy");
    	System.setProperty("http.proxyPort", "3128");
    	
    	WtsClient client = new WtsClient("http://virolab.med.kuleuven.be/wts/services/");
        try 
        {
            String challenge = client.getChallenge("kdforc0");
            System.err.println(challenge);
            String ticket = client.login("kdforc0", challenge, "Vitabis1", "regadb-align");
            System.err.println(ticket);
            client.upload(ticket, "regadb-align", "nt_sequences", new File("/home/plibin0/mutations.htm"));
            client.upload(ticket, "regadb-align", "region", new File("/home/plibin0/region.htm"));
            client.start(ticket, "regadb-align");
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            System.err.println("monitor log file:");
            String logFile = new String(client.monitorLogFile(ticket, "regadb-align"));
            System.err.println(logFile);
            
            System.err.println("monitor log tail:");
            String logTail = new String(client.monitorLogTail(ticket, "regadb-align", 10));
            System.err.println(logTail);
            
            System.err.println("status:" + client.monitorStatus(ticket, "regadb-align"));
            client.download(ticket, "regadb-align", "aa_sequences", new File("/home/plibin0/aaseq_service.htm"));
            
            client.closeSession(ticket, "regadb-align");
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
}
