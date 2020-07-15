package com.am.frame.upload;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.p2p.service.IWebApiService;

//获得相对路径删除文件
public class DeleteFileUploadWebApi implements IWebApiService {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) 
	{
		
//		String path = request.getParameter("path");
		
		DeleteFileUpload deleteFileUpload = new DeleteFileUpload();
		
		try {
			
			deleteFileUpload.service(request, response);
			
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
