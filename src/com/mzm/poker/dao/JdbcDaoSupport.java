package com.mzm.poker.dao;

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

import com.mzm.poker.dao.entity.AbstractEntity;
import com.mzm.poker.dao.entity.Column;
import com.mzm.poker.dao.entity.Entity;

@Repository("jdbcDaoSupport")
public class JdbcDaoSupport {

	private static final Log logger = LogFactory.getLog(JdbcDaoSupport.class);

	@Resource
	JdbcTemplate jdbcTemplate;

	public int updateOrInsert(String sql) {
		return jdbcTemplate.update(sql);
	}

	public void save(AbstractEntity entity) {
		jdbcTemplate.update(sql(entity));
	}

	public String sql(AbstractEntity entity) {
		Class<? extends AbstractEntity> c = entity.getClass();
		Entity entityAnnotation = (Entity) c.getAnnotation(Entity.class);
		StringBuffer columnSb = new StringBuffer();
		StringBuffer valueSb = new StringBuffer();
		valueSb.append("value (");
		columnSb.append("insert into ").append(entityAnnotation.tableName()).append(" (");
		Field[] field = c.getDeclaredFields();
		try {
			for (Field field2 : field) {
				field2.setAccessible(true);
				Column column = field2.getAnnotation(Column.class);
				if (column.isAutoIncrement()) {
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
		String sql = columnSb.toString();
		return sql;
	}

	public <T> Map<String, Object> queryMap(String sql) {
		return jdbcTemplate.queryForMap(sql);
	}

	public <T> T queryOne(String sql, Class<T> requiredType) {
		return queryOne(sql, requiredType, null, null);
	}

	public <T> T queryOne(String sql, Class<T> requiredType, Object[] args) {
		return queryOne(sql, requiredType, args, null);
	}

	public <T> T queryOne(String sql, Class<T> requiredType, Object[] args, QueryMeta qm) {
		List<T> list = query(sql, requiredType, args, qm);
		if (list.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, list.size());
		}
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public <T> List<T> query(String sql, Class<T> requiredType) {
		return query(sql, requiredType, null, null);
	}

	public <T> List<T> query(String sql, Class<T> requiredType, QueryMeta qm) {
		return query(sql, requiredType, null, qm);
	}

	public <T> List<T> query(String sql, Class<T> requiredType, Object[] args) {
		return query(sql, requiredType, args, null);
	}

	public <T> List<T> query(String sql, Class<T> requiredType, Object[] args, QueryMeta qm) {
		assert null != requiredType;
		if (null != qm) {
			if (StringUtils.isNotEmpty(qm.getSort())) {
				sql += " order by " + qm.getSort() + " " + qm.getDir();
			}
			if (0 <= qm.getFirstResult() && 0 < qm.getMaxResults() && 0 <= qm.getLastResult()) {
				sql += " limit ?,?";
				args = ArrayUtils.addAll(args, new Object[]{qm.getFirstResult(), qm.getMaxResults()});

			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(sql);
		}

		long start = System.currentTimeMillis();
		List<T> list = null;
		try {
			list = ArrayUtils.isEmpty(args) ? jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(requiredType)) : jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<T>(requiredType));
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
