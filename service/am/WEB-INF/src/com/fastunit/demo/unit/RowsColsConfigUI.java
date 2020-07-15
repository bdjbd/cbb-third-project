package com.fastunit.demo.unit;

import com.fastunit.Unit;
import com.fastunit.context.ActionContext;
import com.fastunit.support.UnitInterceptor;

public class RowsColsConfigUI implements UnitInterceptor {

	@Override
	public String intercept(ActionContext ac, Unit unit) throws Exception {
		String colsConfig1 = ac.getRequestParameter(
				"style.rowscolsconfig.colsconfig1", "a,5%,1;b,30%,2;");
		String colsConfig2 = ac.getRequestParameter(
				"style.rowscolsconfig.colsconfig2", "L,18%,1;a,40%,1;C,20%,2;");
		String rowsConfig2 = ac.getRequestParameter(
				"style.rowscolsconfig.rowsconfig2", "R1,R2,R3");
		unit.getElement("colsconfig1").setDefaultValue(colsConfig1);
		unit.getElement("colsconfig2").setDefaultValue(colsConfig2);
		unit.getElement("rowsconfig2").setDefaultValue(rowsConfig2);
		Unit unit1 = (Unit) unit.getElement("unit1").getObject();
		Unit unit2 = (Unit) unit.getElement("unit2").getObject();
		unit1.setColsConfig(colsConfig1);
		unit2.setColsConfig(colsConfig2);
		unit2.setRowsConfig(rowsConfig2);
		return unit.write(ac);
	}

}
