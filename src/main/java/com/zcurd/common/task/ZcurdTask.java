package com.zcurd.common.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.plugin.cron4j.ITask;
import com.zcurd.common.util.StringUtil;
import com.zcurd.common.util.UrlUtil;
import com.zcurd.online.model.TaskBase;
import com.zcurd.online.model.TaskLog;

public class ZcurdTask implements ITask {
	private int id;	//taskBaseId
	private int taskTargetType;	//任务类型
	private String taskTargetValue;	//任务值

	public ZcurdTask(int taskTargetType, String taskTargetValue) {
		this.taskTargetType = taskTargetType;
		this.taskTargetValue = taskTargetValue;
	}
	
	public ZcurdTask(int id, int taskTargetType, String taskTargetValue) {
		this.id = id;
		this.taskTargetType = taskTargetType;
		this.taskTargetValue = taskTargetValue;
	}
	
	@Override
	public void run() {
		TaskLog log = new TaskLog();
		Date startDate = new Date();
		log.setTaskId(id).setStartTime(startDate).save();
		
		String result = "成功";
		try {
			for (final String value : taskTargetValue.trim().split(";")) {
				if(StringUtil.isEmpty(value)) {
					continue;
				}
				
				if(taskTargetType == TaskConstant.TASK_TARGET_TYPE1) {
					String content = UrlUtil.getAsText(value);
					// System.out.println(content);
					// TODO 执行结果判断
					
				}else if(taskTargetType == TaskConstant.TASK_TARGET_TYPE2) {
					//Db.find(value);
					
					//权限太大慎用，可执行任意SQL
					Db.execute(new ICallback() {
						@Override
						public Object call(Connection conn) throws SQLException {
							conn.createStatement().execute(value);
							return true;
						}
					});
				}else if(taskTargetType == TaskConstant.TASK_TARGET_TYPE3) {
					ITask task = (ITask) Class.forName(value).newInstance();
					task.run();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "失败";
			log.setRemark(e.getMessage());
		}
		
		Date endDate = new Date();
		int costTime = (int) (endDate.getTime() - startDate.getTime());
		log.setResult(result);
		log.setEndTime(endDate);
		log.setCostTime(costTime);
		log.update();
		
		//更新任务运行信息
		if(id > 0) {
			TaskBase.me.findById(id)
				.setLastRunResult(result)
				.setLastRunTime(startDate)
				.setLastRunTimeCost(costTime)
				.update();
		}
		System.out.println(new Date() + "定时任务执行完成");
	}

	@Override
	public void stop() {
		System.out.println("top");
	}

}
