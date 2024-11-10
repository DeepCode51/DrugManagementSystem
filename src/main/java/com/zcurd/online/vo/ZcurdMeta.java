package com.zcurd.online.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zcurd.online.model.ZcurdField;
import com.zcurd.online.model.ZcurdHead;
import com.zcurd.online.model.ZcurdHeadBtn;
import com.zcurd.online.model.ZcurdHeadJs;

/**
 * 表单元数据
 * @author 钟世云
 */
public class ZcurdMeta {
	
	private ZcurdHead head;
	private List<ZcurdField> fieldList;
	private Map<String, Map<String, Object>> dictMap;
	private List<ZcurdField> addFieldList;
	private List<ZcurdField> updateFieldList;
	private List<ZcurdField> footerFieldList;	//footer列
	private List<ZcurdHeadBtn> btnList;			//扩展按钮
	private List<ZcurdHeadBtn> topList;			//顶部按钮
	private List<ZcurdHeadBtn> lineList;		//行内按钮
	private List<ZcurdHeadJs> jsList;			//扩展js
	
	public Map<String, Object> toMap() {
		Map<String, Object> metaData = new HashMap<String, Object>();
		metaData.put("head", getHead());
		metaData.put("fieldList", getFieldList());
		metaData.put("dictMap", getDictMap());
		metaData.put("addFieldList", getAddFieldList());
		metaData.put("updateFieldList", getUpdateFieldList());
		metaData.put("btnList", getBtnList());
		metaData.put("topList", getTopList());
		metaData.put("lineList", getLineList());
		metaData.put("jsList", getJsList());
		metaData.put("footerFieldList", getFooterFieldList());
		return metaData;
	}
	
	public ZcurdHead getHead() {
		return head;
	}
	public void setHead(ZcurdHead head) {
		this.head = head;
	}
	public List<ZcurdField> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<ZcurdField> fieldList) {
		this.fieldList = fieldList;
	}
	public Map<String, Map<String, Object>> getDictMap() {
		return dictMap;
	}
	public void setDictMap(Map<String, Map<String, Object>> dictMap) {
		this.dictMap = dictMap;
	}
	public List<ZcurdField> getAddFieldList() {
		return addFieldList;
	}
	public void setAddFieldList(List<ZcurdField> addFieldList) {
		this.addFieldList = addFieldList;
	}
	public List<ZcurdField> getUpdateFieldList() {
		return updateFieldList;
	}
	public void setUpdateFieldList(List<ZcurdField> updateFieldList) {
		this.updateFieldList = updateFieldList;
	}
	public List<ZcurdHeadBtn> getBtnList() {
		return btnList;
	}
	public void setBtnList(List<ZcurdHeadBtn> btnList) {
		this.btnList = btnList;
	}
	public List<ZcurdHeadJs> getJsList() {
		return jsList;
	}
	public void setJsList(List<ZcurdHeadJs> jsList) {
		this.jsList = jsList;
	}
	public List<ZcurdHeadBtn> getTopList() {
		return topList;
	}
	public void setTopList(List<ZcurdHeadBtn> topList) {
		this.topList = topList;
	}
	public List<ZcurdHeadBtn> getLineList() {
		return lineList;
	}
	public void setLineList(List<ZcurdHeadBtn> lineList) {
		this.lineList = lineList;
	}
	public List<ZcurdField> getFooterFieldList() {
		return footerFieldList;
	}
	public void setFooterFieldList(List<ZcurdField> footerFieldList) {
		this.footerFieldList = footerFieldList;
	}
}
