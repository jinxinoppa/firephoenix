package com.mzm.firephoenix.dao;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mzm.firephoenix.dao.entity.AbstractEntity;
import com.mzm.firephoenix.dao.entity.Column;
import com.mzm.firephoenix.dao.entity.Entity;

@Repository("jdbcDaoSupport")
public class JdbcDaoSupport {

	private static final Log logger = LogFactory.getLog(JdbcDaoSupport.class);

	@Resource
	JdbcTemplate jdbcTemplate;

	@Deprecated
	public int updateOrInsert(String sql) {
		return jdbcTemplate.update(sql);
	}

	public void update(AbstractEntity entity) {
		Class<? extends AbstractEntity> c = entity.getClass();
		Entity entityAnnotation = (Entity) c.getAnnotation(Entity.class);
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(entityAnnotation.tableName()).append(" set ");
		String updateFieldName = null;
		Field updateField = null;
		try {
			Field updateFieldsListField = c.getDeclaredField("updateFieldsList");
			updateFieldsListField.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<String> updateFieldsList = (List<String>) updateFieldsListField.get(entity);
			for (int i = 0; i < updateFieldsList.size(); i++) {
				updateFieldName = updateFieldsList.get(i);
				updateField = c.getDeclaredField(updateFieldName);
				sql.append(updateField.getDeclaredAnnotation(Column.class).columnName());
				updateField.setAccessible(true);
				sql.append(" = ");
				if (updateField.getType().getSimpleName().endsWith("String") || updateField.getType().getSimpleName().endsWith("Date")) {
					if (null == updateField.get(entity)) {
						continue;
					} else {
						sql.append("\"").append(updateField.get(entity)).append("\", ");
					}
				} else {
					sql.append(updateField.get(entity)).append(", ");
				}
			}
			sql.deleteCharAt(sql.lastIndexOf(","));
			updateField = c.getDeclaredField(entityAnnotation.primaryKey());
			Column column = updateField.getDeclaredAnnotation(Column.class);
			updateField.setAccessible(true);
			sql.append(" where ").append(column.columnName()).append(" = ").append(updateField.get(entity));
			jdbcTemplate.update(sql.toString());
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void save(AbstractEntity entity) {
		Class<? extends AbstractEntity> c = entity.getClass();
		Entity entityAnnotation = (Entity) c.getAnnotation(Entity.class);
		StringBuffer columnSb = new StringBuffer();
		StringBuffer valueSb = new StringBuffer();
		valueSb.append("value (");
		columnSb.append("insert into ").append(entityAnnotation.tableName()).append(" (");
		Field[] field = c.getDeclaredFields();
		Column column = null;
		try {
			for (Field field2 : field) {
				field2.setAccessible(true);
				column = field2.getAnnotation(Column.class);
				if (column.isContinue() || column.isAutoIncrement()) {
					continue;
				}
				if (field2.getType().getSimpleName().endsWith("String") || field2.getType().getSimpleName().endsWith("Date")) {
					if (null == field2.get(entity)) {
						continue;
					} else {
						valueSb.append("\"").append(field2.get(entity)).append("\", ");
					}
				} else {
					valueSb.append(field2.get(entity)).append(", ");
				}
				columnSb.append(column.columnName()).append(", ");
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		valueSb.deleteCharAt(valueSb.lastIndexOf(",")).deleteCharAt(valueSb.lastIndexOf(" ")).append("); ");
		columnSb.deleteCharAt(columnSb.lastIndexOf(",")).deleteCharAt(columnSb.lastIndexOf(" ")).append(") ").append(valueSb.toString());
		jdbcTemplate.update(columnSb.toString());
	}

	public <T> Map<String, Object> queryMap(String sql) {
		return jdbcTemplate.queryForMap(sql);
	}

	public <T> T queryOne(Class<T> requiredType) {
		return queryOne(requiredType, null, null, null);
	}

	public <T> T queryOne(Class<T> requiredType, Object[] args) {
		return queryOne(requiredType, args, null, null);
	}

	public <T> T queryOne(Class<T> requiredType, Object[] args, String[] whereArgs) {
		return queryOne(requiredType, args, null, whereArgs);
	}

	public <T> T queryOne(Class<T> requiredType, Object[] args, QueryMeta qm, String[] whereArgs) {
		List<T> list = query(requiredType, args, qm, whereArgs);
		if (list.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, list.size());
		}
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public <T> List<T> query(Class<T> requiredType) {
		return query(requiredType, null, null, null);
	}

	public <T> List<T> query(Class<T> requiredType, Object[] args) {
		return query(requiredType, args, null, null);
	}

	public <T> List<T> query(Class<T> requiredType, QueryMeta qm) {
		return query(requiredType, null, qm, null);
	}

	public <T> List<T> query(Class<T> requiredType, Object[] args, QueryMeta qm, String[] whereArgs) {
		assert null != requiredType;
		@SuppressWarnings("unchecked")
		Class<? extends AbstractEntity> c = (Class<? extends AbstractEntity>) requiredType;
		Entity entityAnnotation = (Entity) c.getAnnotation(Entity.class);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ").append(entityAnnotation.tableName()).append(" where ");
		Field updateField = null;
		Column column = null;
		try {
			if (whereArgs == null) {
				updateField = c.getDeclaredField(entityAnnotation.primaryKey());
				updateField.setAccessible(true);
				column = updateField.getDeclaredAnnotation(Column.class);
				sql.append(column.columnName()).append(" = ?");
			} else {
				for (int i = 0; i < whereArgs.length; i++) {
					updateField = c.getDeclaredField(whereArgs[i]);
					updateField.setAccessible(true);
					column = updateField.getDeclaredAnnotation(Column.class);
					sql.append(column.columnName()).append(" = ?").append(" and ");
				}
				sql.delete(sql.length() - 5, sql.length());
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
			e.printStackTrace();
		};
		if (null != qm) {
			if (StringUtils.isNotEmpty(qm.getSort())) {
				sql.append(" order by " + qm.getSort() + " " + qm.getDir());
			}
			if (0 <= qm.getFirstResult() && 0 < qm.getMaxResults() && 0 <= qm.getLastResult()) {
				sql.append(" limit ?,?");
				args = ArrayUtils.addAll(args, new Object[]{qm.getFirstResult(), qm.getMaxResults()});

			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}

		long start = System.currentTimeMillis();
		List<T> list = null;
		try {
			list = ArrayUtils.isEmpty(args) ? jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<T>(requiredType)) : jdbcTemplate.query(sql.toString(), args, new BeanPropertyRowMapper<T>(requiredType));
		} catch (DataAccessException ex) {
			logger.error(ex, ex);
			throw ex;
		}
		long diff = System.currentTimeMillis() - start;
		if (diff > 1000) {
			logger.info(requiredType + "----" + diff);
		}
		return list;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
