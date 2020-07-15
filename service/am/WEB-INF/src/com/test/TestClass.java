package com.test;

import java.util.Date;

import org.common.proving.operation.ProvingOperationRestore;
import org.json.JSONObject;

public class TestClass {
	
	static ProvingOperationRestore por = new ProvingOperationRestore();
	
	public static void main(String[] args) {
		String pdmNumber = "FK3ih1UFs84Q7p8JpNytWC1nAHkBzSR1baJLdICgUYeF8hh4+ENUBxN6AfENWBNWcH43D8YjeJFwlp9TDYy50PGm/Hb1w62/gKn6juC8GVh6RlqvoArzohqQhoPVYMzoT6tas1Y3zxUPDDlAw7KfNYTBl8HvirSViGwBzmKsmSmWrNkTg9clM6v2n9KeBT70";
		JSONObject sjo = por.restore(pdmNumber);
		System.out.println(new Date(Long.valueOf(sjo.get("expire_date").toString())));
	}
}
