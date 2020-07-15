package com.am.mall.beans.commodity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.fastunit.MapList;
import com.fastunit.Row;
import com.fastunit.util.Checker;

/**
 * @author Mike
 * @create 2014年11月18日
 * @version 
 * 说明:<br />
 * 商品
 */
public class Commodity {

	private String  id; 
	private String  materialsTypeID; 
	private String  name; 
	private String  abstracts; 
	private String  mainImages; 
	private long  maxSaleNumber;   
	private double  discount; 
	private String  discountDate; 
	private String  isPostage; 
	private long  saleNumber;   
	private String  brandID; 
	private double  recommendValue; 
	private String  supportPaymentMode; 
	private String  detilImages; 
	private String  orgCode; 
	private String  commodityState; 
	private String  menuCode; 
	/**订单状态流程ID**/
	private String  orderStateID; 
	private String  distStateID; 
	private double  price; 
	private String  createDate; 

	private String mallClass;
	private String storeId;
	private String canStack;
	private String play;
	private String seat;
	private String inEnsure;
	private String currentStoreNumber;
	private String scanIn;
	private String supperReturn;
	private String validityDateStart;
	private String validityDateEnd;
	private String breakFast;
	private String cancel;
	private String carType;
	private String carRentalType;
	private String carTypeLabel;
	private String myPlayLabl;
	private String isGroup;
	private String advanceDays;
	private String triphComment;
	private String everySunday;
	private String national_package;
	private String one_day_to_arrive;
	private String goods_code;
	
	
	public Commodity(){}
	
	public Commodity(MapList map){
		
		if(!Checker.isEmpty(map)){
			Row row=map.getRow(0);
			
			this.id=row.get("id");       
			this.materialsTypeID=row.get("materialstypeid");   
			this.name=row.get("name");        
			this.abstracts=row.get("abstract");          
			this.mainImages=row.get("mainimages");
			this.maxSaleNumber=row.getLong("maxsalenumber",0); 
			this.discount=row.getDouble("discount",0);
			this.discountDate=row.get("");
			this.isPostage=row.get("ispostage");
			this.saleNumber=row.getLong("salenumber",0);
			this.brandID=row.get("brandid");
			this.recommendValue=row.getDouble("recommendvalue",-100);
			this.supportPaymentMode=row.get("supportpaymentmode");
			this.detilImages=row.get("detilimages");
			this.orgCode=row.get("orgcode");
			this.commodityState=row.get("commoditystate");
			this.menuCode=row.get("menucode");
			this.orderStateID=row.get("orderstateid");
			this.distStateID=row.get("diststateid");
			this.price=row.getDouble("price",0);
			this.createDate=row.get("createdate");
			
			this.mallClass=row.get("mall_class");
			this.storeId=row.get("store");
			this.canStack=row.get("can_stack");
			this.play=row.get("play");
			this.seat=row.get("seat");
			this.inEnsure=row.get("in_ensure");
			this.currentStoreNumber=row.get("current_store_number");
			this.scanIn=row.get("scan_in");
			this.supperReturn=row.get("supper_return");
			this.validityDateStart=row.get("validity_date_start");
			this.validityDateEnd=row.get("validity_date_end");
			this.breakFast=row.get("breakfast");
			this.cancel=row.get("cancel");
			this.carType=row.get("car_type");
			this.carRentalType=row.get("car_rental_type");
			this.carTypeLabel=row.get("car_type_label");
			this.myPlayLabl=row.get("my_play_label");
			this.isGroup=row.get("is_group");
			this.advanceDays=row.get("advance_days");
			this.triphComment=row.get("triph_comment");
			this.everySunday=row.get("every_sunday");
			this.national_package=row.get("national_package");
			this.one_day_to_arrive=row.get("one_day_to_arrive");
			
			this.goods_code=row.get("goods_code");
			

		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMaterialsTypeID() {
		return materialsTypeID;
	}

	public void setMaterialsTypeID(String materialsTypeID) {
		this.materialsTypeID = materialsTypeID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbstracts() {
		return abstracts;
	}

	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}

	public String getMainImages() {
		return mainImages;
	}

	public void setMainImages(String mainImages) {
		this.mainImages = mainImages;
	}

	public long getMaxSaleNumber() {
		return maxSaleNumber;
	}

	public void setMaxSaleNumber(long maxSaleNumber) {
		this.maxSaleNumber = maxSaleNumber;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getDiscountDate() {
		return discountDate;
	}

	public void setDiscountDate(String discountDate) {
		this.discountDate = discountDate;
	}

	public String getIsPostage() {
		return isPostage;
	}

	public void setIsPostage(String isPostage) {
		this.isPostage = isPostage;
	}

	public long getSaleNumber() {
		return saleNumber;
	}

	public void setSaleNumber(long saleNumber) {
		this.saleNumber = saleNumber;
	}

	public String getBrandID() {
		return brandID;
	}

	public void setBrandID(String brandID) {
		this.brandID = brandID;
	}

	public double getRecommendValue() {
		return recommendValue;
	}

	public void setRecommendValue(double recommendValue) {
		this.recommendValue = recommendValue;
	}

	public String getSupportPaymentMode() {
		return supportPaymentMode;
	}

	public void setSupportPaymentMode(String supportPaymentMode) {
		this.supportPaymentMode = supportPaymentMode;
	}

	public String getDetilImages() {
		return detilImages;
	}

	public void setDetilImages(String detilImages) {
		this.detilImages = detilImages;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getCommodityState() {
		return commodityState;
	}

	public void setCommodityState(String commodityState) {
		this.commodityState = commodityState;
	}

	public String getMenuCode() {
		return menuCode;
	}

	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}

	/**
	 * 订单状态流程ID
	 * @return
	 */
	public String getOrderStateID() {
		return orderStateID;
	}

	public void setOrderStateID(String orderStateID) {
		this.orderStateID = orderStateID;
	}

	public String getDistStateID() {
		return distStateID;
	}

	public void setDistStateID(String distStateID) {
		this.distStateID = distStateID;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getMallClass() {
		return mallClass;
	}

	public void setMallClass(String mallClass) {
		this.mallClass = mallClass;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getCanStack() {
		return canStack;
	}

	public void setCanStack(String canStack) {
		this.canStack = canStack;
	}

	public String getPlay() {
		return play;
	}

	public void setPlay(String play) {
		this.play = play;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public String getInEnsure() {
		return inEnsure;
	}

	public void setInEnsure(String inEnsure) {
		this.inEnsure = inEnsure;
	}

	public String getCurrentStoreNumber() {
		return currentStoreNumber;
	}

	public void setCurrentStoreNumber(String currentStoreNumber) {
		this.currentStoreNumber = currentStoreNumber;
	}

	public String getScanIn() {
		return scanIn;
	}

	public void setScanIn(String scanIn) {
		this.scanIn = scanIn;
	}

	public String getSupperReturn() {
		return supperReturn;
	}

	public void setSupperReturn(String supperReturn) {
		this.supperReturn = supperReturn;
	}

	public String getValidityDateStart() {
		
		if(!Checker.isEmpty(validityDateStart)){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			try {
				validityDateStart=sdf.format(sdf.parse(validityDateStart));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return validityDateStart;
	}

	public void setValidityDateStart(String validityDateStart) {
		this.validityDateStart = validityDateStart;
	}

	public String getValidityDateEnd() {
		if(!Checker.isEmpty(validityDateEnd)){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			try {
				validityDateEnd=sdf.format(sdf.parse(validityDateEnd));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return validityDateEnd;
	}

	public void setValidityDateEnd(String validityDateEnd) {
		this.validityDateEnd = validityDateEnd;
	}

	public String getBreakFast() {
		return breakFast;
	}

	public void setBreakFast(String breakFast) {
		this.breakFast = breakFast;
	}

	public String getCancel() {
		return cancel;
	}

	public void setCancel(String cancel) {
		this.cancel = cancel;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getCarRentalType() {
		return carRentalType;
	}

	public void setCarRentalType(String carRentalType) {
		this.carRentalType = carRentalType;
	}

	public String getCarTypeLabel() {
		return carTypeLabel;
	}

	public void setCarTypeLabel(String carTypeLabel) {
		this.carTypeLabel = carTypeLabel;
	}

	public String getMyPlayLabl() {
		return myPlayLabl;
	}

	public void setMyPlayLabl(String myPlayLabl) {
		this.myPlayLabl = myPlayLabl;
	}

	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public String getAdvanceDays() {
		return advanceDays;
	}

	public void setAdvanceDays(String advanceDays) {
		this.advanceDays = advanceDays;
	}

	public String getTriphComment() {
		return triphComment;
	}

	public void setTriphComment(String triphComment) {
		this.triphComment = triphComment;
	}

	public String getEverySunday() {
		return everySunday;
	}

	public void setEverySunday(String everySunday) {
		this.everySunday = everySunday;
	}

	public String getNational_package() {
		return national_package;
	}

	public void setNational_package(String national_package) {
		this.national_package = national_package;
	}

	public String getOne_day_to_arrive() {
		return one_day_to_arrive;
	}

	public void setOne_day_to_arrive(String one_day_to_arrive) {
		this.one_day_to_arrive = one_day_to_arrive;
	}

	public String getGoods_code() {
		return goods_code;
	}

	public void setGoods_code(String goods_code) {
		this.goods_code = goods_code;
	}
	
}
