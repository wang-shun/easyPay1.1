package com.acewill.chikenprintlibrary.model;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/11/30 12:47
 * Desc：
 */
public class PowerController {
	//    public static final int MEMBER = 1;//会员，允许创建、管理会员并充值
	public static final int    SELECT_SAME_TABLE = 2;//查看同岗位的桌台,允许此员工查看其他同岗位同事的桌台状况
	public static final int    RESERVE           = 4;//预定.允许此员工进入预定界面，处理客人预定事宜
	public static final int    TAKE_OUT_SALE     = 5;//外卖/外带,允许此员工进入外卖/外带界面处理业务
	public static final int    QUICK_CASHIER     = 6;//快速收银,在快餐模式中、菜没上的时候提前买单，且不会收到预警提示
	public static final int    SEND_MSG          = 7;//发送信息,允许员工编写信息并发送给其他人
	public static final int    KAOQINJI          = 8;//考勤机,此员工只是登记考勤，不能使用POS的任何功能
	public static final int    SHIFT_REPORT      = 9;//交班报表,允许此员工查看自己的交班报表
	public static final int REFUND_DISH       = 10;//退菜,允许此员工退菜（允许退的产品）并退款
	public static final int    KIT_WORK          = 11;//后厨工作,此员工在报表统计中列为后厨工作人员
	//    public static final int REPORT = 12;//报表,允许查看餐厅所有报表
	public static final int    SPECIAL_HANDLE    = 13;//特别操作权限,当“ 需要特别的授权密码才能操作” 的功能被启用后，仅拥有此项权限的员工能打开云POS
	public static final int    DISCOUNT          = 14;//打折,不需要上级授权可以对菜品进行打折，而且可以批准其他员工打折的请求
	public static final int    SERVICE_MONEY     = 15;//服务费,不需要上级授权可以在结账时收取服务费，而且可以批准其他员工收取服务费的请求
	public static final int    FREE_SINGLE       = 16;//免单,不需要上级授权可以在进行免单操作，而且可以批准其他员工免单的请求
	public static final int    ORDER_HANDLE      = 17;//订单处理,允许员工对订单进行操作
	public static final int    PAY_INTERFACE     = 18;//支付接口,修改云POS集成的支付方式设置和证书
	public static final int    SCHEDULING        = 19;//排班,可以增加、修改或删除员工的排班
	public static final int    KICK              = 20;//踢人,可以强制其他员工退出云POS
	public static final int    OPEN_POS_BOX      = 21;//打开POS和钱箱,允许此员工查看其他同岗位同事的桌台状况
	public static final int    STORE_CLOSE_CHECK = 22;//进行“打烊检查”,可以进行下班前的“打烊检查”操作。此操作需要“订单处理”和“踢人”的权限
	public static final int    RECIVER_SYS_MSG   = 23;//接收系统通知,可以接收云POS系统发出的任何通知或警告信息，如菜单中的某款菜品库存告急的预警
	public static final int    CHECK_OUT_BACK    = 24;//反结账,打开已经支付过的订单进行重新处理
	public static final int    RECEIVABLES       = 25;//收款,可以进行收款操作
	public static final int    PAYMENT           = 26;//付款,可以进行付款操作

	public static final int MESSAGE                   = 30;//允许查看信息
	public static final int MEMBER                    = 31;//允许检验会员身份
	public static final int HISTORY_ORDER             = 32;//允许查看历史订单
	public static final int REPORT                    = 33;//允许查看报表
	public static final int STAFF                     = 34;//允许查看，修改员工信息
	public static final int DAILY                     = 35;//允许进行日结
	public static final int UPLOAD_LOG                = 36;//允许上传pos日志
	public static final int STANDBY_CASH              = 37;//允许操作备用金
	public static final int ADVANCED_SETUP            = 38;//允许操作pos的高级设置
	public static final int SHIFT_WORK                = 39;//允许交班
	public static final int SHIFT_WORK_HISTORY        = 103;//交接班历史
	public static final int HISTORY_SHIFT_WORK_REPORT = 40;//允许查看历史交班记录
	public static final int UNBIND_DEVICE             = 98;//允许用户进行解绑
	public static final int SELL_OUT                  = 99;//允许对菜品进行沽清
	public static final int NETORDER                  = 100;//网上订单
	public static final int UPDATE                    = 101;//上传日志
	public static final int MODIFY_PW                 = 102;//修改密码
	public static final int ABOUT_CLOUDPOS            = 104;//关于云POS
	public static final int SUPPORT_CALL_GOODS        = 105;//支持门店订货
	public static List<Integer> powerIds;
}
