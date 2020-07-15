package com.fastunit.app.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastunit.util.Checker;
import com.fastunit.util.StringUtil;

//支持两个特殊字符：“*”通配符、“-”范围，一个ip段内不能同时包含*和-
public class IPChecker {

	private static final Logger log = LoggerFactory.getLogger(IPChecker.class);

	public static boolean check(String clientIp, String ipConfigs) {
		if ("127.0.0.1".equals(clientIp) || Checker.isEmpty(ipConfigs)) {
			return true;// 本机或未设置ip规则时不检查
		}
		String[] ip = StringUtil.split(clientIp, "\\.");
		String[] ips = StringUtil.split(ipConfigs, ";");
		for (int i = 0; i < ips.length; i++) {
			if (Checker.isEmpty(ips[i])) {
				continue;// 空字符串跳过
			}
			if (checkIp(ip, StringUtil.split(ips[i], "\\."))) {
				return true; // 有一个通过即可
			} else {
				log.debug(clientIp + " is unpassed by config \"" + ips[i] + "\"");
			}
		}
		return false;
	}

	// 4段全部符合
	private static boolean checkIp(String[] ip, String[] ipConfig) {
		for (int i = 0; i < 4; i++) {
			if (!checkOctet(ip[i], ipConfig[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkOctet(String octet, String octetConfig) {
		int index = octetConfig.indexOf("-");
		if (index > 0) {
			int start = Integer.parseInt(octetConfig.substring(0, index));
			int end = Integer.parseInt(octetConfig.substring(index + 1));
			int ipInt = Integer.parseInt(octet);
			return ipInt >= start && ipInt <= end;
		} else {
			// 消除连续的*
			octetConfig = StringUtil.replace(octetConfig, "**", "*");
			if (octetConfig.length() == 3 && octetConfig.startsWith("*")
					&& octetConfig.endsWith("*")) {
				// 首、尾是*, *1*
				return octet.length() == 3
						&& octet.substring(1, 2).equals(octetConfig.substring(1, 2));
			} else {
				// 最多包含一个*
				switch (octetConfig.indexOf("*")) {
				case 0:// *位于第1位
					if (octetConfig.length() == 1) {
						return true;
					} else {
						return octet.length() > 1
								&& octet.endsWith(octetConfig.substring(1));
					}
				case 1:// *位于第2位
					if (octetConfig.length() == 2) {
						return octet.length() > 1
								&& octet.startsWith(octetConfig.substring(0, 1));
					} else {// 3
						return octet.length() == 3
								&& octet.startsWith(octetConfig.substring(0, 1))
								&& octet.endsWith(octetConfig.substring(2));
					}
				case 2:// *位于第3位
					return octet.length() == 3
							&& octet.startsWith(octetConfig.substring(0, 2));
				default:// 不包含*
					return octet.equals(octetConfig);
				}
			}
		}
	}
}
