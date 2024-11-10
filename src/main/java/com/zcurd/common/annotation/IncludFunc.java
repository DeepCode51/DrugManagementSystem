package com.zcurd.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zcurd.common.enums.IncludeFunc;

/**
 * 在线表单-包含功能
 * @author 钟世云 2020年3月29日 下午3:39:49
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IncludFunc {
	public IncludeFunc value();
}
