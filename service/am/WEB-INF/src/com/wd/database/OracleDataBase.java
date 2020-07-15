package com.wd.database;

public class OracleDataBase implements IDataBase {
	@Override
	public String getInstrStr(String substr, String str) {
		return " instr(" + substr + "," + str + ") ";
	}

	@Override
	public String getTo_DateStr(String dateStr, String formatStr) {
		formatStr = formatStr.replaceAll("HH", "HH24");
		formatStr = formatStr.replaceAll("mm", "MI");
		return " to_Date('" + dateStr + "','" + formatStr + "') ";
	}

	@Override
	public String getSysdateStr() {
		return " sysdate ";
	}

	@Override
	public String getTop1Str() {
		return " and rownum = 1 ";
	}

	@Override
	public String getTo_NumberStr(String str, String formatStr) {
		return " to_Number(" + str + ") ";
	}

	@Override
	public String getDateTimeDifferenceStr(String str) {
		return " (round(to_number(sysdate - " + str + ") * 24 * 60 * 60 * 60))";
	}
}
