package com.zcurd.common.task;

import java.util.HashMap;
import java.util.Map;

/**
 * 定时任务常量
 * @author 钟世云 2017年3月20日 下午11:09:30
 */
public class TaskConstant {
	/**任务状态 运行中**/
	public final static int TASK_STATU1 = 1;
	/**任务状态 已停止**/
	public final static int TASK_STATU2 = 2;
	
	/**任务目标类型 URL**/
	public final static int TASK_TARGET_TYPE1 = 1;
	/**任务目标类型 SQL**/
	public final static int TASK_TARGET_TYPE2 = 2;
	/**任务目标类型 ITask**/
	public final static int TASK_TARGET_TYPE3 = 3;
	
	
	/**任务集合 key为taskBaseId**/
	private static Map<Integer, Object> taskMap = new HashMap<Integer, Object>();
	
	/**任务集合**/
	public static Map<Integer, Object> getTaskMap() {
		return taskMap;
	}

}
