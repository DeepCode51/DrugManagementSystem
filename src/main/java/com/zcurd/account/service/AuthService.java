package com.zcurd.account.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zcurd.account.model.SysMenu;
import com.zcurd.account.model.SysMenuBtn;
import com.zcurd.account.model.SysMenuDatarule;
import com.zcurd.account.model.SysRoleMenu;
import com.zcurd.account.model.SysUser;
import com.zcurd.account.vo.AuthDataVO;
import com.zcurd.common.util.StringUtil;

/**
 * 权限（新版本）
 * @author 钟世云 2018年3月21日 下午10:46:28
 */
public class AuthService {
	
	public AuthDataVO getAuthData(Long userId) {
		SysUser user = SysUser.dao.findById(userId);
		return new AuthDataVO(getAuthUrl(user), getAuthBtnUrl(user), getNoAuthPageBtn(user), getAuthDataRule(user));
	}
	
	
	/**
	 *  获得用户授权菜单(tree数据格式)
	 */
	public List<SysMenu> getAuthMenuForTree(SysUser user) {
		List<SysMenu> menuList = getAuthMenu(user);
		Set<SysMenu> pMenuList = new LinkedHashSet<>();
		Map<Integer, SysMenu> menuMap = getAllMenuMap();
		for (SysMenu sysMenu : menuList) {
			// 获取所有父菜单
			Integer pId = sysMenu.getParentId();
			while(pId != null && pId > 0 && menuMap.containsKey(pId)) {
				pMenuList.add(menuMap.get(pId));
				pId = menuMap.get(pId).getParentId();
			}
		}
		menuList.addAll(pMenuList);
		// 菜单排序
		menuList.sort(new Comparator<SysMenu>() {
			public int compare(SysMenu o1, SysMenu o2) {
				if(o1.getOrderNum() == null || o2.getOrderNum() == null || 
						o1.getOrderNum() < o2.getOrderNum()) {
					return -1;
				}
				return 1;
			}
		});
		return menuList;
	}
	
	/**
	 *  获得用户授权菜单
	 */
	public List<SysMenu> getAuthMenu(SysUser user) {
		List<SysMenu> menuList = new ArrayList<>();
		if(user == null || StringUtil.isEmpty(user.getRoles())) {
			return menuList;
		}
		menuList = SysMenu.dao.findByRoleIds(user.getRoles());
		return menuList;
	}
	
	/**
	 * 获得授权URL
	 */
	public List<String> getAuthUrl(SysUser user) {
		List<String> urls = new ArrayList<>();
		if(user == null || StringUtil.isEmpty(user.getRoles())) {
			return urls;
		}
		
		// 菜单URL
		List<SysMenu> menuList = getAuthMenu(user);
		for (SysMenu sysMenu : menuList) {
			String url = formatUrl(sysMenu.getMenuUrl());
			if(StringUtil.isNotEmpty(url)) {
				urls.add(url);
			}
		}
		return extraAuthUrlRule(urls);
	}
	
	/**
	 * 按钮URL
	 */
	public List<String> getAuthBtnUrl(SysUser user) {
		List<String> urls = new ArrayList<>();
		if(user == null || StringUtil.isEmpty(user.getRoles())) {
			return urls;
		}
		Map<Integer, String> menuMap = getAllMenuUrlMap();
	
		// 按钮URL
		List<SysMenuBtn> btnList = SysMenuBtn.dao.findByRoleIds(user.getRoles());
		for (SysMenuBtn btn : btnList) {
			if(StringUtil.isEmpty(btn.getMethodName())) {
				continue;
			}
			
			for(String method : btn.getMethodName().split(", *")) {
				if((menuMap.get(btn.getMenuId()).contains("menu"))) {
					System.err.println(menuMap.get(btn.getMenuId()));
				}
				if(StringUtil.isEmpty(method) || StringUtil.isEmpty(menuMap.get(btn.getMenuId()))) {
					continue;
				}
				if(method.contains("/")) {
					urls.add(formatUrl(method));
				}else {
					urls.add(formatUrl(menuMap.get(btn.getMenuId()).replaceAll("\\w+$", method)));	// 菜单URL + Method
				}
			}
		}
		return extraAuthUrlRule(urls);
	}
	
	/**
	 * 扩展授权URL规则
	 */
	public List<String> extraAuthUrlRule(List<String> urlList) {
		Set<String> extraList = new LinkedHashSet<>();
		for (String url : urlList) {
			// 拥有列表页面权限时，默认拥有数据列表、详情权限
			if(url.endsWith("/listPage")) {
				extraList.add(url.replace("/listPage", "/listData"));
				extraList.add(url.replace("/listPage", "/detailPage"));
			}
		}
		extraList.addAll(urlList);
		// 去重
		urlList.clear();
		urlList.addAll(extraList);
		return urlList;
	}

	/**
	 * 获得页面没有权限的按钮
	 */
	public Map<String, Set<String>> getNoAuthPageBtn(SysUser user) {
		Map<String, Set<String>> pageBtnClassMap = new HashMap<>();
		if(user == null || StringUtil.isEmpty(user.getRoles())) {
			return pageBtnClassMap;
		}
		
		// 有权限按钮
		Map<Integer, Set<String>> containsMap = new HashMap<>();
		List<SysMenuBtn> btnList = SysMenuBtn.dao.findByRoleIds(user.getRoles());
		for (SysMenuBtn btn : btnList) {
			if(StringUtil.isEmpty(btn.getClassName())) {
				continue;
			}
			Set<String> btnClassSet = containsMap.get(btn.getMenuId());
			if(btnClassSet == null) {
				btnClassSet = new HashSet<>();
			}
			btnClassSet.addAll(Arrays.asList(btn.getClassName().split(", *")));
			containsMap.put(btn.getMenuId(), btnClassSet);
		}
		
		// 需隐藏按钮 = 所有按钮 - 有权限按钮
		Map<Integer, Set<String>> menuBtnClassMap = new HashMap<>();
		for (SysMenuBtn btn : SysMenuBtn.dao.findAll()) {
			if(StringUtil.isEmpty(btn.getClassName())) {
				continue;
			}
			Set<String> set = menuBtnClassMap.get(btn.getMenuId());
			if(set == null) {
				set = new HashSet<>();
			}
			
			if(containsMap.get(btn.getMenuId()) == null) {
				set.addAll(Arrays.asList(btn.getClassName().split(", *")));
			}else {
				for(String btnClass : btn.getClassName().split(", *")) {
					if(!containsMap.get(btn.getMenuId()).contains(btnClass)) {
						set.add(btnClass);
					}
				}
			}
			menuBtnClassMap.put(btn.getMenuId(), set);
		}
		
		Map<Integer, String> allMenuMap = getAllMenuUrlMap();
		for(Integer menuId : menuBtnClassMap.keySet()) {
			pageBtnClassMap.put(allMenuMap.get(menuId), menuBtnClassMap.get(menuId));
		}
		return pageBtnClassMap;
	}

	/**
	 * 获得页面数据权限
	 */
	public Map<String, String> getAuthDataRule(SysUser user) {
		Map<String, String> pageDataRuleMap = new HashMap<>();
		if(user == null || StringUtil.isEmpty(user.getRoles())) {
			return pageDataRuleMap;
		}
		
		// 页面数据权限(不区分角色)
		Map<Integer, List<SysMenuDatarule>> menuDataRuleMap = new HashMap<>();
		String[] roleIds = user.getRoles().split(", *");
		for (String roleId : roleIds) {
			List<SysMenuDatarule> ruleList = SysMenuDatarule.dao.findByRoleId(Integer.parseInt(roleId));
			for (SysMenuDatarule item : ruleList) {
				List<SysMenuDatarule> list = menuDataRuleMap.get(item.getMenuId());
				if(list == null) {
					list = new ArrayList<>();
				}
				item.put("roleId", roleId);
				list.add(item);
				menuDataRuleMap.put(item.getMenuId(), list);
			}
		}
		
		// 页面数据权限（区分角色）
		Map<Integer, String> allMenuMap = getAllMenuUrlMap();
		outer : for (Integer menuId : menuDataRuleMap.keySet()) {
			List<SysMenuDatarule> list = menuDataRuleMap.get(menuId);
			// 同一角色取交集
			Map<String, String> roleRuleMap = new HashMap<>();
			for (SysMenuDatarule item : list) {
				String roleId = item.getStr("roleId");
				String rule = roleRuleMap.get(roleId);
				if(StringUtil.isEmpty(rule)) {
					rule = covertToSql(item);
				}else {
					rule += " and " + covertToSql(item);
				}
				roleRuleMap.put(roleId, rule);
			}
			
			// 不同角色取并集
			String pageRule = "";
			for (String roleId : roleIds) {
				// 有菜单权限，且无数据权限。则此页面无数据权限
				if(roleRuleMap.get(roleId) == null) {
					if(SysRoleMenu.dao.countByRoleIdAndMenuId(Integer.parseInt(roleId), menuId) > 0) {
						continue outer;
					}else {
						continue;
					}
				}
				if(pageRule.length() > 0) {
					pageRule += " or ";
				}
				pageRule += "(" + roleRuleMap.get(roleId) + ")";
			}
			
			// 在线表单，数据权限存放至headId
			Integer headId = getHeadId(allMenuMap.get(menuId));
			pageDataRuleMap.put(headId == null ? allMenuMap.get(menuId) : headId.toString(), pageRule);
		}
		return pageDataRuleMap;
	}
	
	/**
	 * 判断URL是否为在线表单
	 */
	public static boolean isOnlineForm(String url) {
		return url.matches("^/zcurd/\\d+/\\w+");
	}
	
	public static Integer getHeadId(String url) {
		url = formatUrl(url);
		if(isOnlineForm(url)) {
			return Integer.parseInt(url.split(("/"))[2]);
		}
		return null;
	}
	
	/**
	 * 数据权限转成SQL
	 */
	public String covertToSql(SysMenuDatarule rule) {
		return (rule.getFieldName() == null ? "" : rule.getFieldName())
				+ (rule.getSymbol() == null ? "" : rule.getSymbol())  
				+ (rule.getValue() == null ? "" : ("'" + rule.getValue()) + "'");
	}
	
	/**
	 * 获得所有菜单
	 */
	public Map<Integer, String> getAllMenuUrlMap() {
		Map<Integer, String> menuMap = new HashMap<>();
		List<SysMenu> menuAllList = SysMenu.dao.findAll();
		for (SysMenu sysMenu : menuAllList) {
			String url = sysMenu.getMenuUrl();
			if(StringUtil.isNotEmpty(url) && url.contains("/")) {
				menuMap.put(sysMenu.getId(), formatUrl(sysMenu.getMenuUrl()));
			}
		}
		return menuMap;
	}
	
	/**
	 * 获得所有菜单
	 */
	public Map<Integer, SysMenu> getAllMenuMap() {
		Map<Integer, SysMenu> menuMap = new HashMap<>();
		List<SysMenu> menuAllList = SysMenu.dao.findAll();
		for (SysMenu sysMenu : menuAllList) {
			menuMap.put(sysMenu.getId(), sysMenu);
		}
		return menuMap;
	}
	
	/**
	 * 格式化URL
	 * 如：/zcurd//18/listPage?id=1	格式化后--> /zcurd/18/listPage
	 */
	public static String formatUrl(String url) {
		if(StringUtil.isEmpty(url)) {
			return url;
		}
		int i = url.indexOf("?");
		if(i == -1) {
			i = url.indexOf("#");
		}
		if(i != -1) {
			url = url.substring(0, i);
		}
		return url.replaceAll("//+", "/");
	}
	
	public static void main(String[] args) {
		System.err.println(formatUrl("/zcurd//18/listPage?id=1"));
		System.err.println(getHeadId("/zcurd//18/listPage"));
	}
}
