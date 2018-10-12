package com.acewill.ordermachine.model;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/12/22 17:24
 * Desc：
 */
public class DishKindRes extends BaseModelSz {

	/**
	 * result : 0
	 * content :
	 * errmsg : 0
	 * dishKindData : [{"kindID":1,"kindName":"肉类","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170516161615_5676.gif","seq":1,"kindColor":"","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":1,"subBrandId":null},{"kindID":2,"kindName":"素菜","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102210_346.png","seq":2,"kindColor":"","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":1,"subBrandId":null},{"kindID":3,"kindName":"汤","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102536_3935.png","seq":3,"kindColor":"","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":1,"subBrandId":null},{"kindID":4,"kindName":"甜品","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506101842_8248.png","seq":4,"kindColor":"dcf1b8","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":7,"kindName":"酒","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506101604_3503.png","seq":5,"kindColor":"dcf1b8","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":6,"kindName":"饮料","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102118_8813.png","seq":6,"kindColor":"dcf1b8","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":5,"kindName":"套餐","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102141_3841.png","seq":7,"kindColor":"dcf1b8","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":9,"kindName":"12","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102118_8813.png","seq":9,"kindColor":"","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":0,"synchroTakeOut":0,"subBrandId":null},{"kindID":10,"kindName":"13","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102118_8813.png","seq":10,"kindColor":"","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":15,"kindName":"食堂自选","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506101658_6908.png","seq":10,"kindColor":"93e5b7","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":11,"kindName":"14","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102118_8813.png","seq":11,"kindColor":"","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":12,"kindName":"16","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102118_8813.png","seq":12,"kindColor":"","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":13,"kindName":"15","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102118_8813.png","seq":13,"kindColor":"","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null},{"kindID":14,"kindName":"TestKind","imageName":"http://szfileserver.419174855.mtmss.com/common/fileupload/20170506102210_346.png","seq":14,"kindColor":"ffd200","synchroPOS":1,"synchroMealMachine":1,"synchroWechat":1,"synchroTakeOut":0,"subBrandId":null}]
	 * dishKindDataMap : null
	 */


	private List<DishKind> dishKindData;


	public List<DishKind> getDishKindData() {
		return dishKindData;
	}

	public void setDishKindData(List<DishKind> dishKindData) {
		this.dishKindData = dishKindData;
	}

}
