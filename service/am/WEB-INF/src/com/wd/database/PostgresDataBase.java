package com.wd.database;

public class PostgresDataBase implements IDataBase {
	@Override
	public String getInstrStr(String substr, String str) {
		return " position(" + str + " in " + substr + ") ";
	}

	@Override
	public String getTo_DateStr(String dateStr, String formatStr) {
		return " '" + dateStr + "' ";
	}

	@Override
	public String getSysdateStr() {
		return " now() ";
	}

	@Override
	public String getTop1Str() {
		return " limit 1 ";
	}

	@Override
	public String getTo_NumberStr(String str, String formatStr) {
		return " to_Number(" + str + ",'" + formatStr + "') ";
	}

	@Override
	public String getDateTimeDifferenceStr(String str) {
		return " Extract(epoch FROM Now())  -  Extract(epoch FROM " + str
				+ ") ";
	}
}
