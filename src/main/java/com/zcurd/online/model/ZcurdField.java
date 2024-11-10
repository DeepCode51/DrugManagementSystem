package com.zcurd.online.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

public class ZcurdField extends Model<ZcurdField> {
	private static final long serialVersionUID = 1L;
	public static final ZcurdField me = new ZcurdField();
	
	public int getIsShowList() {
		return getInt("is_show_list");
	}
	
	public int getIsSum() {
		return getInt("is_sum");
	}
	
	public String getFieldName() {
		return get("field_name");
	}
	
	public void setFieldName(String value) {
		set("field_name", value);
	}
	
	public Page<ZcurdField> paginate(int pageNumber, int pageSize, int headId) {
		return paginate(pageNumber, pageSize, "select * ", "from zcurd_field where head_id=? order by order_num", new Object[]{headId});
	}
	
	public List<ZcurdField> findByHeadId(int headId) {
		return find("select * from zcurd_field where head_id=? order by order_num", headId);
	}
}
