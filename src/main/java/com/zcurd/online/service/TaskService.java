package com.zcurd.online.service;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.cron4j.ITask;
import com.zcurd.common.ErrorMsgException;
import com.zcurd.common.task.TaskConstant;
import com.zcurd.common.task.ZcurdTask;
import com.zcurd.online.model.TaskBase;

/**
 * 定时任务service
 * @author 钟世云 2017年3月12日 下午11:57:33
 */
public class TaskService {
	
	/**
	 * 启动所有任务（启动项目时调用）
	 */
	public void startAll() {
		System.out.println(new Date() + "开始启动定时任务");
		List<TaskBase> taskList = TaskBase.me.find("select * from task_base");
		int count = 0;
		for (TaskBase taskBase : taskList) {
			Cron4jPlugin cp = new Cron4jPlugin();
			cp.addTask(taskBase.getCron(), createTask(taskBase));
			TaskConstant.getTaskMap().put(taskBase.getId(), cp);
			if(taskBase.getStatus() == TaskConstant.TASK_STATU1) {
				cp.start();
				count++;
			}
		}
		System.out.println(new Date() + "完成启动定时任务	已启动" + count + "个任务");
	}
	
	/**
	 * 启动或停止任务
	 * @param taskId	任务id
	 * @param status	状态 1:启动，2:停止
	 */
	public void startOrStop(int taskId, int status) {
		Cron4jPlugin cp = (Cron4jPlugin) TaskConstant.getTaskMap().get(taskId);
		if(status == TaskConstant.TASK_STATU1) {
			cp.start();
		}else if(status == TaskConstant.TASK_STATU2) {
			cp.stop();
		}
		TaskBase.me.findById(taskId).setStatus(status).update();
	}
	
	/**
	 * 增加任务
	 * @param task
	 */
	public void add(TaskBase task) {
		task.setStatus(TaskConstant.TASK_STATU2); //默认停止
		task.save();
		
		Cron4jPlugin cp = new Cron4jPlugin();
		cp.addTask(task.getCron(), createTask(task));
		TaskConstant.getTaskMap().put(task.getId(), cp);
	}
	
	/**
	 * 更新任务
	 * @param taskBase
	 */
	public void update(TaskBase taskBase) {
		Cron4jPlugin cp = (Cron4jPlugin) TaskConstant.getTaskMap().get(taskBase.getId());
		try {
			if(taskBase.getStatus() == TaskConstant.TASK_STATU1) {
				cp.stop();
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new ErrorMsgException("任务停止失败，" + e.getMessage());
		}
		
		cp = new Cron4jPlugin();
		cp.addTask(taskBase.getCron(), createTask(taskBase));
		try {
			if(taskBase.getStatus() == TaskConstant.TASK_STATU1) {
				cp.start();
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new ErrorMsgException("任务启动失败，" + e.getMessage());
		}
		
		taskBase.update();
		TaskConstant.getTaskMap().put(taskBase.getId(), cp);
	}
	
	/**
	 * 删除任务
	 * @param id	任务id
	 */
	public void delete(Integer id) {
		Cron4jPlugin cp = (Cron4jPlugin) TaskConstant.getTaskMap().get(id);
		TaskBase task = TaskBase.me.findById(id);
		
		try {
			//停止任务
			if(task.getStatus() == TaskConstant.TASK_STATU1) {
				cp.stop();
			}
			cp = null;
			TaskConstant.getTaskMap().remove(id);
			task.delete();
		}catch (Exception e) {
			e.printStackTrace();
			throw new ErrorMsgException("任务停止失败，" + e.getMessage());
		}finally {
			cp = null;
			TaskConstant.getTaskMap().remove(id);
		}
	}
	
	/**
	 * 立即执行
	 */
	public void runAtSoon(TaskBase taskBase) {
		createTask(taskBase).run();
	}
	
	/**
	 * 创建一个任务
	 * @param taskBase	任务信息
	 * @return ITask
	 */
	private ITask createTask(TaskBase taskBase) {
		ITask task = new ZcurdTask(taskBase.getId(), taskBase.getTargetType(), taskBase.getTargetValue());
		return task;
	}

}
