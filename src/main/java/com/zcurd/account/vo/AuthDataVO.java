package com.zcurd.account.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限数据
 * @author 钟世云 2018年3月25日 下午9:11:07
 */
public class AuthDataVO {
	/** 已授权URL **/
	private List<String> authUrl = new ArrayList<>();
	/** 已授权按钮URL **/
	private List<String> authBtnUrl = new ArrayList<>();
	/** 页面未授权按钮 **/
	private Map<String, Set<String>> noAuthPageBtn = new HashMap<>();
	/** 数据权限 **/
	private Map<String, String> authDataRule;
	
	public AuthDataVO() {}
	
	public AuthDataVO(List<String> authUrl, List<String> authBtnUrl, Map<String, Set<String>> noAuthPageBtn, Map<String, String> authDataRule) {
		this.authUrl = authUrl;
		this.authBtnUrl = authBtnUrl;
		this.noAuthPageBtn = noAuthPageBtn;
		this.authDataRule = authDataRule;
	}

	public List<String> getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(List<String> authUrl) {
		this.authUrl = authUrl;
	}

	public Map<String, Set<String>> getNoAuthPageBtn() {
		return noAuthPageBtn;
	}

	public void setNoAuthPageBtn(Map<String, Set<String>> noAuthPageBtn) {
		this.noAuthPageBtn = noAuthPageBtn;
	}

	public Map<String, String> getAuthDataRule() {
		return authDataRule;
	}

	public void setAuthDataRule(Map<String, String> authDataRule) {
		this.authDataRule = authDataRule;
	}

	public List<String> getAuthBtnUrl() {
		return authBtnUrl;
	}

	public void setAuthBtnUrl(List<String> authBtnUrl) {
		this.authBtnUrl = authBtnUrl;
	}
}