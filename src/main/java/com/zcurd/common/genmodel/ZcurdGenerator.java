package com.zcurd.common.genmodel;

import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.zcurd.common.util.StringUtil;

/**
 * model生成器
 * @author 钟世云 2020年4月7日 下午11:46:34
 */
public class ZcurdGenerator extends Generator {
	
	public ZcurdGenerator(DataSource dataSource, String baseModelPackageName, String baseModelOutputDir,
			String modelPackageName, String modelOutputDir) {
		super(dataSource, baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
	}

	/**
	 * 生成model
	 * @param tables	需要生成model的表，支持正则表达式
	 */
	public void generate(String... tables) {
		if (dialect != null) {
			metaBuilder.setDialect(dialect);
		}
		
		long start = System.currentTimeMillis();
		List<TableMeta> genTableMetas = new LinkedList<>();
		for (TableMeta tm : metaBuilder.build()) {
			for (String table : tables) {
				if(StringUtil.equals(table, tm.name) || tm.name.matches(table)) {
					genTableMetas.add(tm);
					break;
				}
			}
		}
		
		if (genTableMetas.size() == 0) {
			System.out.println("TableMeta 数量为 0，不生成任何文件");
			return ;
		}
		
		baseModelGenerator.generate(genTableMetas);
		
		if (modelGenerator != null) {
			modelGenerator.generate(genTableMetas);
		}
		
		if (mappingKitGenerator != null) {
			mappingKitGenerator.generate(genTableMetas);
		}
		
		if (dataDictionaryGenerator != null && generateDataDictionary) {
			dataDictionaryGenerator.generate(genTableMetas);
		}
		
		long usedTime = (System.currentTimeMillis() - start) / 1000;
		System.out.println("Generate complete in " + usedTime + " seconds.");
	}
	
}
