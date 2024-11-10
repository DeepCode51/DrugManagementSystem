package com.zcurd.common.genmodel;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.c3p0.C3p0Plugin;

/**
 * 生新生成model
 * @author 钟世云 2020年4月7日 下午11:46:34
 */
public class RegenerateModel {
	
	public static DataSource getDataSource() {
		PropKit.use("a_little_config.txt");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(
				PropKit.get("base_jdbcUrl"), 
				PropKit.get("base_user"), 
				PropKit.get("base_password").trim());
		c3p0Plugin.start();
		return c3p0Plugin.getDataSource();
	}
	
	/**
	 * 生成model
	 * @param packageName	包名
	 * @param tables		需要生成model的表，支持正则表达式
	 */
	public static void generate(String packageName, String... tables) {
		// base model 文件保存路径
		String outputDir = PathKit.getWebRootPath() + "/src/main/java/" + packageName.replaceAll("\\.", "/");
		
		// 创建生成器
		ZcurdGenerator generator = new ZcurdGenerator(getDataSource(), packageName + ".base", outputDir + "/base", packageName, outputDir);
		// 设置是否在 Model 中生成 dao 对象
		generator.setGenerateDaoInModel(true);
		// 设置是否生成链式 setter 方法
		generator.setGenerateChainSetter(true);
		// 设置是否生成字典文件
		generator.setGenerateDataDictionary(false);
		// 生成
		generator.generate(tables);
	}

}
