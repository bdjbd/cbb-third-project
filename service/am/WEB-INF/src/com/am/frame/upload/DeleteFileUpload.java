package com.am.frame.upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//获得相对路径删除文件
public class DeleteFileUpload extends HttpServlet 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 608161886049380258L;

	final Logger logger = LoggerFactory.getLogger(DeleteFileUpload.class);
	
	private ServletContext sc;
	
	@Override
	public void init(ServletConfig config) 
	{
		sc = config.getServletContext();
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException
	{
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		//构建输出对象
		PrintWriter out = new PrintWriter(response.getOutputStream());
				
		String tCode="0";
		String tMsg="Delete File Sucessful";
		
		String tFilePath=request.getParameter("full_path");
		
		String path=sc.getRealPath(tFilePath);
		
		logger.debug(" path=" + path);
		
		if(!deleteFile(path))
		{
			tCode="1";
			tMsg="Delete File Fail";
		}
		
		out.print("{\"CODE\" : \"" + tCode + "\",\"MSG\" : \"" + tMsg + "\"}");
		out.flush();
		out.close();
	}
	
    /** 
     *  根据路径删除指定的目录或文件，无论存在与否 
     *@param sPath  要删除的目录或文件 
     *@return 删除成功返回 true，否则返回 false。 
     */  
    public boolean DeleteFolder(String sPath) 
    {  
        boolean flag = false;  
        File file = new File(sPath);  
        // 判断目录或文件是否存在  
        if (!file.exists()) {  // 不存在返回 false  
            return flag;  
        } else {  
            // 判断是否为文件  
            if (file.isFile()) {  // 为文件时调用删除文件方法  
                return deleteFile(sPath);  
            } else {  // 为目录时调用删除目录方法  
                return deleteDirectory(sPath);  
            }  
        }
    }  
    
    
    /** 
     * 删除单个文件 
     * @param   sPath    被删除文件的文件名 
     * @return 单个文件删除成功返回true，否则返回false 
     */  
    public boolean deleteFile(String sPath) {  
        boolean flag = false;  
        File file = new File(sPath);  
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {  
            file.delete();  
            flag = true;  
        }  
        return flag;  
    }  
    
    /** 
     * 删除目录（文件夹）以及目录下的文件 
     * @param   sPath 被删除目录的文件路径 
     * @return  目录删除成功返回true，否则返回false 
     */  
    public boolean deleteDirectory(String sPath) {  
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
        if (!sPath.endsWith(File.separator)) {  
            sPath = sPath + File.separator;  
        }  
        
        File dirFile = new File(sPath);  
        //如果dir对应的文件不存在，或者不是一个目录，则退出  
        if (!dirFile.exists() || !dirFile.isDirectory()) {  
            return false;  
        }  
        
        boolean flag = true;  
        //删除文件夹下的所有文件(包括子目录)  
        File[] files = dirFile.listFiles();  
        
        for (int i = 0; i < files.length; i++) {  
            //删除子文件  
            if (files[i].isFile()) {  
                flag = deleteFile(files[i].getAbsolutePath());  
                if (!flag) break;  
            } //删除子目录  
            else {  
                flag = deleteDirectory(files[i].getAbsolutePath());  
                if (!flag) break;  
            }  
        }  
        if (!flag) return false;  
        
        //删除当前目录  
        if (dirFile.delete()) {  
            return true;  
        } else {  
            return false;  
        }  
    }  

}
