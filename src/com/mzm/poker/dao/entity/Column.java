package com.mzm.poker.dao.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	String columnName() default "";
	enum DataType {
		SMALLINT, INT, BIGINT, DOUBLE, VARCHAR, TIMESTAMP
	};
	DataType dataType() default DataType.SMALLINT;
	boolean isAutoIncrement() default false;
}
