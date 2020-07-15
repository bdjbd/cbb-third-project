package com.fastunit.demo.samples;

import java.sql.Connection;

import com.fastunit.MapList;
import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.jdbc.DB;
import com.fastunit.jdbc.Table;
import com.fastunit.jdbc.TableRow;
import com.fastunit.support.MapListFactory;
import com.fastunit.support.UnitInterceptor;
import com.fastunit.support.action.TransactionAction;

//某个单元多处使用的接口可定义为一个类
public class MultiImpl extends TransactionAction implements MapListFactory,
		UnitInterceptor {

	@Override
	public void doAction(DB db, ActionContext ac) throws Exception {
		// 一、DB可以用来操作数据库
		// 1、查询数据
		MapList list = db.query("");
		// 2、执行sql语句
		int result = db.execute("");
		// 3、获得原始连接
		Connection connection = db.getConnection();
		// 4、依据Table对象操作某个表
		Table table = new Table("app", "T");
		TableRow tr = table.addInsertRow();
		tr.setValue("a", "value");
		tr = table.addUpdateRow();
		tr.setValue("a", "value");
		tr.setOldValue("a", "value");
		tr = table.addDeleteRow();
		tr.setOldValue("a", "value");
		int[] results = db.save(table);

		// 二、ActionContext可用来获取相关数据
		// 1、获取Request数据
		ac.getRequestParameter("");
		// 2、获取/设置Session数据
		ac.getSessionAttribute("");
		ac.setSessionAttribute("", "");
		// 3、获得用户对象
		ac.getVisitor().getUser();
		// 4、获得Action自定义参数
		ac.getActionParameter();
		// 5、获得Action结果对象
		ac.getActionResult();

	}

	@Override
	public MapList getMapList(ActionContext ac) {
		// 提供单元数据
		return null;
	}

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		// 拦截单元
		return unit.write(ac);
	}

}
