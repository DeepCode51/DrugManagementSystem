package com.zcurd.common.task;

import com.jfinal.plugin.cron4j.ITask;

/**
 * com.zcurd.common.task.DemoTask，用于配置类型为taskClass的定时任务
 * @author 钟世云 2017年3月18日 下午2:49:46
 */
public class DemoTask implements ITask {
	
	@Override
	public void run() {
		System.out.println("demo run!");
	}

	@Override
	public void stop() {
		System.out.println("demo stop!");
	}

}
