package com.zcurd.common;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.zcurd.common.util.Pager;
import com.zcurd.common.util.StringUtil;
import com.zcurd.online.vo.QueryParamVO;

public class DBTool{
	public static final DBTool me = new DBTool();
	
	
	public static List<Record> findByMultProperties(String table, String[] properties, Object[] values) {
		return findByMultProperties(table, properties, values, null, null);
	}
	
	public static List<Record> findByMultProperties(String table, String[] properties, Object[] values, String orderBy) {
		return findByMultProperties(table, properties, values, orderBy, null);
	}
	
	public static List<Record> findByMultProperties(String table, String[] properties, Object[] values, Pager pager) {
		return findByMultProperties(table, properties, values, null, pager);
	}
	
	public static List<Record> findByMultProperties(String table, String[] properties, Object[] values, String orderBy, Pager pager) {
		String[] symbols = new String[properties.length];
		for (int i = 0; i < properties.length; i++) {
			symbols[i] = "=";
		}
		return findByMultProperties(null, table, properties, symbols, values, orderBy, pager);
	}
	
	public static List<Record> findByMultProperties(String table, String[] properties, String[] symbols, Object[] values) {
		return findByMultProperties(null, table, properties, symbols, values, null, null);
	}
	
	public static List<Record> findByMultProperties(String table, String[] properties, String[] symbols, Object[] values, Pager pager) {
		return findByMultProperties(null, table, properties, symbols, values, null, pager);
	}
	
	public static List<Record> findByMultProperties(String table, String[] properties, String[] symbols, Object[] values, String orderBy, Pager pager) {
		return findByMultProperties(null, table, properties, symbols, values, orderBy, pager);
	}
	
	public static List<Record> findByMultProperties(String[] fields, String table, String[] properties, Object[] values) {
		return findByMultProperties(fields, table, properties, null, values, null, null);
	}
	
	public static List<Record> findByMultProperties(String[] fields, String table, String[] properties, String[] symbols, Object[] values) {
		return findByMultProperties(fields, table, properties, symbols, values, null, null);
	}
	
	public static List<Record> findByMultProperties(String[] fields, String table, String[] properties, String[] symbols, Object[] values, Pager pager) {
		return findByMultProperties(fields, table, properties, symbols, values, null, pager);
	}
	
	public static List<Record> findByMultProperties(String[] fields, String table, String[] properties, String[] symbols, Object[] values, String orderBy, Pager pager) {
		return findByMultPropertiesDbSource(null, fields, table, properties, symbols, values, orderBy, pager);
	}
	
	
	
	public static List<Record> findByMultPropertiesDbSource(String dbSource, String table, String[] properties, String[] symbols, Object[] values) {
		return findByMultPropertiesDbSource(dbSource, null, table, properties, symbols, values, null, null);
	}
	
	public static List<Record> findByMultPropertiesDbSource(String dbSource, String table, String[] properties, String[] symbols, Object[] values, Pager pager) {
		return findByMultPropertiesDbSource(dbSource, null, table, properties, symbols, values, null, pager);
	}
	
	public static List<Record> findByMultPropertiesDbSource(String dbSource, String table, String[] properties, String[] symbols, Object[] values, String orderBy, Pager pager) {
		return findByMultPropertiesDbSource(dbSource, null, table, properties, symbols, values, orderBy, pager);
	}
	
	public static List<Record> findList(QueryParamVO vo) {
		return findByMultPropertiesDbSource(vo.getDbSource(), null, vo.getTable(), vo.getPropertiesToArray(), vo.getSymbolsToArray(), vo.getValuesToArray(), vo.getWhereSql(), vo.getOrderBy(), vo.getPager());
	}
	
	public static List<Record> findByMultPropertiesDbSource(String dbSource, String table, String[] properties, String[] symbols, Object[] values, String whereSql, String orderBy, Pager pager) {
		return findByMultPropertiesDbSource(dbSource, null, table, properties, symbols, values, whereSql, orderBy, pager);
	}
	
	public static List<Record> findByMultPropertiesDbSource(String dbSource, String table, QueryParamVO vo) {
		return findByMultPropertiesDbSource(dbSource, null, table, vo.getPropertiesToArray(), vo.getSymbolsToArray(), vo.getValuesToArray(), vo.getWhereSql(), null, null);
	}
	
	public static List<Record> findByMultPropertiesDbSource(String dbSource, String[] fields, String table, String[] properties, String[] symbols, Object[] values, String orderBy, Pager pager) {
		return findByMultPropertiesDbSource(dbSource, fields, table, properties, symbols, values, null, orderBy, pager);
	}
	
	public static List<Record> findByMultPropertiesDbSource(String dbSource, String[] fields, String table, String[] properties, String[] symbols, Object[] values, String whereSql, String orderBy, Pager pager) {
		checkSecurity(properties, symbols);
		StringBuilder sb = new StringBuilder("select ");
		List<Object> params = new ArrayList<>();
		if(fields == null || fields.length == 0) {
			fields = new String[]{"*"};
		}
		for (int i = 0; i < fields.length; i++) {
			if(i > 0) {
				sb.append(", ");
			}
			sb.append(fields[i]);
		}
		
		sb.append(" from " + table + " where 1=1");
		
		for (int i = 0; i < properties.length; i++) {
			//多选搜索，用or代替in，防止SQL注入
			if(ZcurdConst.SEARCH_TYPE_OR2IN.equals(symbols[i]) && values[i] != null && values[i].toString().indexOf(",") != -1) {
				String orIn = "";
				for (String orItem : values[i].toString().split(",")) {
					orIn += " or " + properties[i] + " = ?";
					params.add(orItem.trim());
				}
				sb.append(" and (" + orIn.substring(3) + ")");
			}else {
				if(ZcurdConst.SEARCH_TYPE_OR2IN.equals(symbols[i])) {
					symbols[i] = "=";
				}
				sb.append(" and " + properties[i] + " " + symbols[i] + " ?");
				params.add(values[i]);
			}
		}
		
		if(StringUtil.isNotEmpty(whereSql)) {
			sb.append(" and " + whereSql);
		}
		
		if(StringUtil.isNotEmpty(orderBy)) {
			sb.append(" order by " + orderBy);
		}
		if(pager != null) {
			sb.append(" limit " + pager.getStartRow() + ", " + pager.getRows());
		}
		return Db.use(ZcurdTool.getDbSource(dbSource)).find(sb.toString(), params.toArray());
	}
	
	public static int countByMultProperties(String table, String[] properties, Object[] values) {
		String[] symbols = new String[properties.length];
		for (int i = 0; i < properties.length; i++) {
			symbols[i] = "=";
		}
		return countByMultProperties(table, properties, symbols, values);
	}
	
	public static int countByMultProperties(String table, String[] properties, String[] symbols, Object[] values) {
		return countByMultPropertiesDbSource(null, table, properties, symbols, values);
	}
	
	public static int countByMultPropertiesDbSource(String dbSource, String table, String[] properties, String[] symbols, Object[] values) {
		return countByMultPropertiesDbSource(dbSource, table, properties, symbols, values, null);
	}
	
	public static int countBy(QueryParamVO vo) {
		return countByMultPropertiesDbSource(vo.getDbSource(), vo.getTable(), vo.getPropertiesToArray(), vo.getSymbolsToArray(), vo.getValuesToArray(), vo.getWhereSql());
	}
	
	public static int countByMultPropertiesDbSource(String dbSource, String table, String[] properties, String[] symbols, Object[] values, String whereSql) {
		checkSecurity(properties, symbols);
		StringBuilder sb = new StringBuilder("select count(*)");
		List<Object> params = new ArrayList<>();
		sb.append(" from " + table + " where 1=1");
		for (int i = 0; i < properties.length; i++) {
			//多选搜索，用or代替in，防止SQL注入
			if(ZcurdConst.SEARCH_TYPE_OR2IN.equals(symbols[i]) && values[i] != null && values[i].toString().indexOf(",") != -1) {
				String orIn = "";
				for (String orItem : values[i].toString().split(",")) {
					orIn += " or " + properties[i] + " = ?";
					params.add(orItem.trim());
				}
				sb.append(" and (" + orIn.substring(3) + ")");
			}else {
				if(ZcurdConst.SEARCH_TYPE_OR2IN.equals(symbols[i])) {
					symbols[i] = "=";
				}
				sb.append(" and " + properties[i] + " " + symbols[i] + " ?");
				params.add(values[i]);
			}
		}
		if(StringUtil.isNotEmpty(whereSql)) {
			sb.append(" and " + whereSql);
		}
		return Db.use(ZcurdTool.getDbSource(dbSource)).queryLong(sb.toString(), params.toArray()).intValue();
	}
	
	public static List<Object> findDbSource(String dbSource, String selectSQL, String[] properties, String[] symbols, Object[] values, String whereSql) {
		checkSecurity(properties, symbols);
		StringBuilder sb = new StringBuilder(selectSQL);
		List<Object> params = new ArrayList<>();
		if(selectSQL.toLowerCase().indexOf("where") < 0) {
			sb.append(" where");
		}
		sb.append(" 1=1");
		for (int i = 0; i < properties.length; i++) {
			//多选搜索，用or代替in，防止SQL注入
			if(ZcurdConst.SEARCH_TYPE_OR2IN.equals(symbols[i]) && values[i] != null && values[i].toString().indexOf(",") != -1) {
				String orIn = "";
				for (String orItem : values[i].toString().split(",")) {
					orIn += " or " + properties[i] + " = ?";
					params.add(orItem.trim());
				}
				sb.append(" and (" + orIn.substring(3) + ")");
			}else {
				if(ZcurdConst.SEARCH_TYPE_OR2IN.equals(symbols[i])) {
					symbols[i] = "=";
				}
				sb.append(" and " + properties[i] + " " + symbols[i] + " ?");
				params.add(values[i]);
			}
		}
		
		if(StringUtil.isNotEmpty(whereSql)) {
			sb.append(" and " + whereSql);
		}
		
		List<Object> result = Db.use(ZcurdTool.getDbSource(dbSource)).query(sb.toString(), params.toArray());
		return result;
	}
	
	/**
	 * 解析dbSource和sql，格式为[dbSource=xxx]select ...
	 * @param sql
	 * @return String[0]为数据源，String[1]为sql
	 */
	public static String[] parseSQL4DbSource(String sql) {
		String dbSource = "";
		if(sql.toLowerCase().startsWith("[dbSource".toLowerCase())) {
			dbSource = sql.substring(1, sql.indexOf("]")).split("=")[1].trim();
			sql = sql.substring(sql.indexOf("]") + 1);
		}
		return new String[]{dbSource, sql};
	}
	
	/**
	 * 支持带数据源的查询
	 * @param sql 格式为[dbSource=xxx]select ...
	 */
	public static List<Record> findBySQL4DbSource(String sql) {
		String[] parseSql = parseSQL4DbSource(sql);
		return use(parseSql[0]).find(parseSql[1]);
	}
	
	public static DbPro use(String dbSource) {
		return Db.use(ZcurdTool.getDbSource(dbSource));
	}
	
	/**
	 * 判断是否安全的查询条件
	 * @return true: 安全, false: 不安全
	 */
	public static boolean isSecurity(String[] properties, String[] symbols) {
		return isSecurity(properties, symbols, false);
	}
	
	/**
	 * 判断是否安全的查询条件。如不安全，抛出运行时异常
	 */
	public static boolean checkSecurity(String[] properties, String[] symbols) {
		return isSecurity(properties, symbols, true);
	}
	
	/**
	 * 判断是否安全的查询条件
	 * @param isThrowException	是否抛出异常 (true: 抛出异常, false: 不抛出异常)
	 * @return true: 安全, false: 不安全
	 */
	private static boolean isSecurity(String[] properties, String[] symbols, boolean isThrowException) {
		if(properties != null) {
			String[] danger4Properties = new String[]{" ", "=", ">" , "<"};
			for (String propertie : properties) {
				if(StringUtil.isNotEmpty(propertie)) {
					propertie = propertie.trim();
					for (String str : danger4Properties) {
						if(propertie.contains(str)) {
							if(isThrowException) {
								throw new RuntimeException("SQL Danger: [" + propertie + "] 不是一个安全的properties");
							}else{
								return false;
							}
						}
					}
				}
			}
		}
		
		if(properties != null) {
			if(1==1) return false;
			String[] danger4Symbols = new String[]{" ", "'"};
			for (String symbol : symbols) {
				if(StringUtil.isNotEmpty(symbol)) {
					symbol = symbol.trim();
					for (String str : danger4Symbols) {
						if(symbol.contains(str)) {
							if(isThrowException) {
								throw new RuntimeException("SQL Danger: [" + symbol + "] 不是一个安全的symbols");
							}else{
								return false;
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		C3p0Plugin c3p0Plugin = new C3p0Plugin("jdbc:mysql://127.0.0.1/zcurd?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull", "root", "123456");
		ActiveRecordPlugin arp = new ActiveRecordPlugin("zcurd", c3p0Plugin); 
		c3p0Plugin.start();
		arp.start();
		
		List<Record> list = findByMultProperties("sys_menu" , new String[]{"parent_id"}, new Object[]{0});
		for (Record record : list) {
			System.out.println(record);
		}
		System.out.println(countByMultProperties("sys_menu" , new String[]{"parent_id"}, new Object[]{0}));
		
		/*List<Object> list = findDbSource(null, "select sum(id), avg(id) from sys_menu", new String[]{"id"}, new String[]{"<"}, new Object[]{5});
		System.out.println(list);*/
		
		System.out.println(isSecurity(new String[]{"name>1"}, new String[]{}));
	}

}
