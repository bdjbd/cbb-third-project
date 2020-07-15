package com.am.frame.other.taskInterface.impl.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author YueBin
 * @create 2016年6月27日
 * @version 
 * 说明:<br />

<name>帅哥</name> （真实姓名）
<id>330182198804273139</id> (实名制商品需要传多个身份证）
***/
@XmlRootElement(name="credential")
public class Credential {
	private String name;
	private String id;
	
	@XmlElement(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name="id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
