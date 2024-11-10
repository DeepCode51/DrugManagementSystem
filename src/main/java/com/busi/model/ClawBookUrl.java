package com.busi.model;

import java.util.Map;

import com.jfinal.plugin.activerecord.Model;
import com.zcurd.common.DbMetaTool;

public class ClawBookUrl extends Model<ClawBookUrl> {
	private static final long serialVersionUID = 1L;
	public static final ClawBookUrl me = new ClawBookUrl();
		
	public Map<String, Object> getDictDatastatus() {
		return DbMetaTool.getDictData("select '0', '未采集' union all select '1', '采集中' union all select '2', '采集完'");
	}	
	
}
