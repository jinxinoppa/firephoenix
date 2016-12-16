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

	public void update(AbstractEntity entity) {
		Class<? extends AbstractEntity> c = entity.getClass();
		Entity entityAnnotation = (Entity) c.getAnnotation(Entity.class);
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(entityAnnotation.tableName()).append(" set ");
		String updateFieldName = null;
		Field updateField = null;
		String whereCause = null;
		try {
			Field updateFieldsListField = c.getField("updateFieldsList");
			updateFieldsListField.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<String> updateFieldsList = (List<String>) updateFieldsListField.get(entity);
			if (!updateFieldsList.isEmpty()) {
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
				if (updateField.getType().getSimpleName().endsWith("String")) {
					whereCause = "\"" + updateField.get(entity) + "\"";
				} else {
					whereCause = String.valueOf(updateField.get(entity));
				}
				sql.append(" where ").append(column.columnName()).append(" = ").append(whereCause);
				String sqlStr = sql.toString();
				logger.info(sqlStr);
				jdbcTemplate.update(sqlStr);
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	public void save(AbstractEntity entity) {
		Class<? extends AbstractEntity> c = entity.getClass();
		Entity entityAnnotation = (Entity) c.getAnnotation(Entity.class);
		StringBuffer columnSb = new StringBuffer();
		StringBuffer valueSb = new StringBuffer();
		valueSb.append("value (");
		columnSb.append("insert into ").append(entityAnnotation.tableName()).append(" (");
		Column column = null;
		String insertFieldName = null;
		Field insertField = null;
		try {
			Field insertFieldsListField = c.getField("insertFieldsList");
			insertFieldsListField.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<String> insertFieldsList = (List<String>) insertFieldsListField.get(entity);
			if (!insertFieldsList.isEmpty()) {
				for (int i = 0; i < insertFieldsList.size(); i++) {
					insertFieldName = insertFieldsList.get(i);
					insertField = c.getDeclaredField(insertFieldName);
					insertField.setAccessible(true);
					column = insertField.getAnnotation(Column.class);
					if (column.isContinue() || column.isAutoIncrement()) {
						continue;
					}
					if (insertField.getType().getSimpleName().endsWith("String") || insertField.getType().getSimpleName().endsWith("Date")) {
						if (null == insertField.get(entity)) {
							continue;
						} else {
							valueSb.append("\"").append(insertField.get(entity)).append("\", ");
						}
					} else {
						valueSb.append(insertField.get(entity)).append(", ");
					}
					columnSb.append(column.columnName()).append(", ");
				}
			} else {
				Field[] field = c.getDeclaredFields();
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
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error(e, e);
		}
		valueSb.deleteCharAt(valueSb.lastIndexOf(",")).deleteCharAt(valueSb.lastIndexOf(" ")).append("); ");
		columnSb.deleteCharAt(columnSb.lastIndexOf(",")).deleteCharAt(columnSb.lastIndexOf(" ")).append(") ").append(valueSb.toString());
		String sql = columnSb.toString();
		logger.info(sql);
		jdbcTemplate.update(sql);
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

	public List<String> queryGroupBy(@SuppressWarnings("rawtypes") Class requiredType, QueryMeta qm) {
		assert null != requiredType;
		@SuppressWarnings("unchecked")
		Class<? extends AbstractEntity> c = (Class<? extends AbstractEntity>) requiredType;
		Entity entityAnnotation = (Entity) c.getAnnotation(Entity.class);
		StringBuffer sql = new StringBuffer();
		sql.append("select " + qm.getGroupBy() + " from ").append(entityAnnotation.tableName()).append(" group by " + qm.getGroupBy());
		logger.info(sql);

		long start = System.currentTimeMillis();
		List<String> list = null;
		try {
			list = jdbcTemplate.queryForList(sql.toString(), String.class);
		} catch (DataAccessException ex) {
			logger.error(ex, ex);
		}
		long diff = System.currentTimeMillis() - start;
		if (diff > 1000) {
			logger.info(requiredType + "----" + diff);
		}
		return list;
	}

	public <T> List<T> query(Class<T> requiredType, Object[] args, QueryMeta qm, String[] whereArgs) {
		assert null != requiredType;
		@SuppressWarnings("unchecked")
		Class<? extends AbstractEntity> c = (Class<? extends AbstractEntity>) requiredType;
		Entity entityAnnotation = (Entity) c.getAnnotation(Entity.class);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ").append(entityAnnotation.tableName());
		Field queryField = null;
		Column column = null;
		try {
			if (args != null) {
				if (whereArgs == null) {
					sql.append(" where ");
					queryField = c.getDeclaredField(entityAnnotation.primaryKey());
					queryField.setAccessible(true);
					column = queryField.getDeclaredAnnotation(Column.class);
					sql.append(column.columnName()).append(" = ?");
				} else {
					sql.append(" where ");
					for (int i = 0; i < whereArgs.length; i++) {
						queryField = c.getDeclaredField(whereArgs[i]);
						queryField.setAccessible(true);
						column = queryField.getDeclaredAnnotation(Column.class);
						sql.append(column.columnName()).append(" = ?").append(" and ");
					}
					sql.delete(sql.length() - 5, sql.length());
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
			logger.error(e, e);
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
		logger.info(sql);

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
		for (int i = 0; i < list.size(); i++) {
			T t = list.get(i);
			@SuppressWarnings("unchecked")
			Class<? extends AbstractEntity> abstractEntity = (Class<? extends AbstractEntity>) t.getClass();
			Field updateFieldsListField;
			try {
				updateFieldsListField = abstractEntity.getField("updateFieldsList");
				updateFieldsListField.setAccessible(true);
				@SuppressWarnings("unchecked")
				List<String> updateFieldsList = (List<String>) updateFieldsListField.get(t);
				updateFieldsList.clear();
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				logger.error(e, e);
			}
		}
		return list;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
