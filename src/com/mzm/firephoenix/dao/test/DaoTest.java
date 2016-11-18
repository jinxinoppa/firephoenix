package com.mzm.firephoenix.dao.test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mzm.firephoenix.dao.JdbcDaoSupport;
import com.mzm.firephoenix.dao.QueryMeta;
import com.mzm.firephoenix.dao.entity.AbstractEntity;
import com.mzm.firephoenix.dao.entity.Column;
import com.mzm.firephoenix.dao.entity.Entity;
import com.mzm.firephoenix.dao.entity.FivepkAccount;
import com.mzm.firephoenix.dao.entity.FivepkSeoId;
import com.mzm.firephoenix.dao.entity.FivepkSeoIdList;
import com.mzm.firephoenix.junits.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class DaoTest extends BaseTest {

	@Resource
	JdbcDaoSupport jdbcDaoSupport;
	@Resource
	JdbcTemplate jdbcTemplate;

	// @Test
	public static void main(String[] s) throws IllegalArgumentException, IllegalAccessException {
		TableTest account = new TableTest();
		account.setColumnA((byte) 1);
		account.setColumnB((short) 1);
		account.setColumnC(1);
		account.setColumnD(1);
		account.setColumnE(1);
		// account.setColumnF(1);
		// account.setColumnG("中文@123");
		FivepkAccount fivepkAccount = new FivepkAccount();
		fivepkAccount.setName("1");
		fivepkAccount.setPassword("1");
		fivepkAccount.setSeoid("1");
//		fivepkAccount.setCreateDate(new Date());
		sql(fivepkAccount);
	}

	public static String sql(AbstractEntity entity) {
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
				if (column.isAutoIncrement()){
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
System.out.println(sql);
		return sql;
	}
	@Ignore
	@Test
	public void test() {
		int id = jdbcTemplate.execute(new ConnectionCallback<Integer>() {

			@Override
			public Integer doInConnection(Connection arg0) throws SQLException, DataAccessException {
				PreparedStatement pstmt = (PreparedStatement) arg0.prepareStatement("select 1 from table_test", Statement.RETURN_GENERATED_KEYS);// 传入参数：Statement.RETURN_GENERATED_KEYS
				pstmt.execute();
				ResultSet rs = pstmt.getGeneratedKeys();
				int generatedKey = 0;
				while (rs.next()) {
					generatedKey = rs.getInt(1);
				}
				return generatedKey;
			}
		});
		System.out.println(id);
		// int id = jdbcTemplate.execute("insert into `table_test` values ()",
		// new PreparedStatementCallback<Integer>() {
		//
		// @Override
		// public Integer doInPreparedStatement(PreparedStatement arg0)
		// throws SQLException, DataAccessException {
		// ResultSet rs = arg0.getGeneratedKeys();
		// int id = 0;
		// while (rs.next()){
		// id = rs.getInt("column_h");
		// }
		// return id;
		// }
		// });
		// System.out.println(id);
	}

	@Ignore
	@Test
	public void saveTest() {
		String sql = "insert into `fivepk_player_info` (account_id, name, nick_name) values (" + 1 + ", " + 2 + ", \"玩家@" + 1 + "\")";
	}

//	@Ignore
	@Test
	public void selectTest() {
//		List<String> list = jdbcDaoSupport.queryGroupBy(FivepkSeoIdList.class, new QueryMeta("seoid"));
		List<FivepkSeoId> list = jdbcDaoSupport.query(FivepkSeoId.class);
	}

	@Ignore
	@Test
	public void queryTest() {
//		List<TableTest> list = jdbcDaoSupport.query("select * from table_test", TableTest.class, new QueryMeta(1, 2));
//		for (TableTest tableTest : list) {
//			System.out.println(tableTest.getColumnA());
//		}
	}

	@Ignore
	@Test
	public void updateTest() {
		
	}

	@Ignore
	@Test
	public void queryMap() {
		Map<String, Object> map = jdbcDaoSupport.queryMap("select * from table_test");
	}
}
