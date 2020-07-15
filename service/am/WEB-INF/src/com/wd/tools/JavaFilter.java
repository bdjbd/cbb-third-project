package com.wd.tools;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 文件过滤器
 * @author dzx
 * */
public class JavaFilter implements FilenameFilter 
{

	private String keyName="";//关键字
	private String extension="";//后缀名
	public JavaFilter(String keyName,String extension)
	{
		this.keyName=keyName;
		this.extension=extension;
	}
	@Override
	public boolean accept(File dir, String name) {
		return Filter(name,keyName,extension);
	}
	
	/**
	 * 过滤文件
	 * @param file 文件名
	 * @param keyName 关键字，多个关键字之间用空格分隔
	 * @param extension 后缀名，多个后缀名之间用逗号分隔
	 * */
	private boolean Filter(String file,String keyName,String extension) {
		String[] exs = extension.split(",");
		String[]kns=keyName.split(" ");
		if (exs != null && exs.length > 0 && kns!=null && kns.length>0) {
			for (int i = 0; i < exs.length; i++) {
				if (file.toLowerCase().endsWith(exs[i].toLowerCase())) {
					for(int j=0;j<kns.length;j++)
					{
						if(kns[j]!=null && !kns[j].trim().equalsIgnoreCase("") && file.indexOf(kns[j])>=0)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
