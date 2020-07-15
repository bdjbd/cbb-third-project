package com.am.mall.beans.commodity;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月25日
 * @version 
 * 说明:<br />
 * 商品规格
 */
public class CommoditySpecifications {
	
	private String id;
	private String commodityID;
	private String name;      
	private String specImages;  
	private String price;    
	private String explain;
	private String dates;
	
	public CommoditySpecifications(){
	}
	
	public CommoditySpecifications(MapList map){
		
		if(!Checker.isEmpty(map)){
			Row row=map.getRow(0);
			this.id=row.get("id");
			this.commodityID=row.get("commodityid");
			this.name=row.get("name");
			this.specImages=row.get("specimages");
			this.price=row.get("price");
			this.explain=row.get("explain");
			this.dates=row.get("dates");
		}
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCommodityID() {
		return commodityID;
	}
	public void setCommodityID(String commodityID) {
		this.commodityID = commodityID;
	}
	public String getName() {
		if(name==null){
			return "";
		}else
			return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSpecImages() {
		return specImages;
	}
	public void setSpecImages(String specImages) {
		this.specImages = specImages;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getExplain() {
		return explain;
	}
	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getDates() {
		if(dates==null){
			return "";
		}else
		return dates;
	}

	public void setDates(String dates) {
		this.dates = dates;
	}
	
	
}
