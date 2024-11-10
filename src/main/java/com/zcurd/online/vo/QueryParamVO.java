package com.zcurd.online.vo;

import java.util.ArrayList;
import java.util.List;

import com.zcurd.common.util.Pager;

public class QueryParamVO {
	private List<String> properties = new ArrayList<String>();
	private List<String> symbols = new ArrayList<String>();
	private List<Object> values = new ArrayList<Object>();
	
	private String table;
	private String dbSource;
	private String orderBy;
	private String whereSql = "";
	private Pager pager;
	
	public QueryParamVO() {}
	
	public QueryParamVO(String whereSql) {
		this.whereSql = whereSql;
	}

	public QueryParamVO(List<String> properties, List<String> symbols, List<Object> values, String whereSql) {
		this.properties = properties;
		this.symbols = symbols;
		this.values = values;
		this.whereSql = whereSql;
	}
	
	public QueryParamVO add(String propertie, Object value) {
		getProperties().add(propertie);
		getSymbols().add("=");
		getValues().add(value);
		return this;
	}

	public QueryParamVO add(String propertie, String symbol, Object value) {
		getProperties().add(propertie);
		getSymbols().add(symbol);
		getValues().add(value);
		return this;
	}

	public List<String> getProperties() {
		return properties;
	}
	public String[] getPropertiesToArray() {
		return properties.toArray(new String[] {});
	}
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}
	public List<String> getSymbols() {
		return symbols;
	}
	public String[] getSymbolsToArray() {
		return symbols.toArray(new String[] {});
	}
	public void setSymbols(List<String> symbols) {
		this.symbols = symbols;
	}
	public List<Object> getValues() {
		return values;
	}
	public Object[] getValuesToArray() {
		return values.toArray();
	}
	public void setValues(List<Object> values) {
		this.values = values;
	}
	public String getWhereSql() {
		return whereSql;
	}
	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public String getDbSource() {
		return dbSource;
	}

	public void setDbSource(String dbSource) {
		this.dbSource = dbSource;
	}
}
