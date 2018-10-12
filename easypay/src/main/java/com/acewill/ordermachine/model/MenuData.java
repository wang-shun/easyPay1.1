package com.acewill.ordermachine.model;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/12/22 16:41
 * Desc：
 */
public class MenuData extends BaseModelSz {

	/**
	 * result : 0
	 * content :
	 * errmsg : 0
	 * menuData : [{"dataId":0,"stime":"07:00","etime":"23:59","dishType":0,"timeName":"全天","dishData":[{"dishID":29,"dishName":"食堂自选","price":0.01,"cost":0.01,"memberPrice":123,"waimaiPrice":0.01,"dishUnitID":1,"dishUnit":"份","dishCount":999999,"imageName":"","comment":"","dishDiscount":[],"isPackage":0,"packageItems":[],"optionCategoryList":[],"dishKind":"7","dishKindColor":"ffd58a","printerStr":"","sortMark":"234234","specificationList":[],"kindSeq":12,"waiDai_cost":0,"subBrandId":null,"marketList":[],"salesCount":0,"discounted":1,"optionRequired":false,"dishSeq":29,"wx_count":0,"dishPublicity":0,"dishPublicityStr":"","weighted":0,"temp_priced":0,"recommandList":null,"englishName":"","marketPrice":null,"marketName":null,"abandonPrice":null,"showPackageItemsFlag":false}]},{"dataId":0,"stime":"07:00","etime":"23:59","dishType":1,"timeName":"全天","dishData":[{"dishID":2,"dishName":"凉拌土豆丝","price":1.1,"cost":0,"memberPrice":15,"waimaiPrice":1.1,"dishUnitID":1,"dishUnit":"份","dishCount":999897,"imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170808092414_7676.jpg","comment":"","dishDiscount":[],"isPackage":0,"packageItems":[],"optionCategoryList":[{"id":1068,"appId":"87825359","brandId":34,"optionList":[{"id":1530,"appId":"87825359","brandId":34,"optionName":"微辣","price":1,"required":false,"categoryId":1068,"requiredStr":null},{"id":1531,"appId":"87825359","brandId":34,"optionName":"中辣","price":0,"required":false,"categoryId":1068,"requiredStr":null},{"id":1532,"appId":"87825359","brandId":34,"optionName":"猛辣","price":0,"required":false,"categoryId":1068,"requiredStr":null}],"optionCategoryName":"口味","minimalOptions":1,"multipleOptions":true,"maximalOptions":2,"multipleOptionsStr":null}],"dishKind":"3","dishKindColor":"","printerStr":"","sortMark":"1001","specificationList":[],"kindSeq":3,"waiDai_cost":0,"subBrandId":null,"marketList":[],"salesCount":0,"discounted":1,"optionRequired":false,"dishSeq":1,"wx_count":0,"dishPublicity":0,"dishPublicityStr":"","weighted":0,"temp_priced":0,"recommandList":null,"englishName":"","marketPrice":null,"marketName":null,"abandonPrice":null,"showPackageItemsFlag":false}]}]
	 * brandDishMenuList : null
	 */


	private List<MenuDataBean> menuData;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}


	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}


	public List<MenuDataBean> getMenuData() {
		return menuData;
	}

	public void setMenuData(List<MenuDataBean> menuData) {
		this.menuData = menuData;
	}

	public static class MenuDataBean {

		private List<DishModel> dishData;

		public List<DishModel> getDishData() {
			return dishData;
		}

		public void setDishData(List<DishModel> dishData) {
			this.dishData = dishData;
		}

	}
}
