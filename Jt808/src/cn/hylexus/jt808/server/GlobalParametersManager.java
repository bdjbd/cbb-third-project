package cn.hylexus.jt808.server;

import java.util.ResourceBundle;


public class GlobalParametersManager 
{
	private ResourceBundle resource=null;
	private static GlobalParametersManager single=null;
	
	public static final String RestfulAPIServerHost = "RestfulAPIServerHost";  
	public static final String AnnexServerHost="AnnexServerHost";
	public static final String TcpServerPort = "TcpServerPort";
	public static final String ControlServerSleep = "ControlServerSleep";  
	public static final String TcpServerThreadCount = "TcpServerThreadCount";
	
	private GlobalParametersManager()
	{
		//GlobalParameter.properties   
	    resource = ResourceBundle.getBundle("GlobalParameter");
	}
	
    public static synchronized GlobalParametersManager getInstance() 
    {  
        if (single == null) {    
            single = new GlobalParametersManager();  
        }    
       return single;  
    }
    
    public String getValue(String key)
    {
    	return resource.getString(key);
    }
}
