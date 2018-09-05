package org.rkcz;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.postgresql.util.PGobject;

public class Main {

	private static final String SCHEMA = "test";

	public static void main(String[] args) {
		String url = "jdbc:postgresql://192.168.99.100/postgres?user=postgres&password=example";
		List<Table> list = new LinkedList<Table>();
		for (Order industry : Order.values()) {
			for (Abeceda type : Abeceda.values()) {
				list.add(new Table(industry, type));
			}
		}

		String sql = null;
		for (Table table : list) {
			try {
				Connection conn = DriverManager.getConnection(url);
				Class<?> genericClass = table.getClass();
				StringBuilder columns = new StringBuilder();
				StringBuilder vars = new StringBuilder();
				for (Field field : genericClass.getDeclaredFields()) {
					if (columns.length() > 1) {
						columns.append(", ");
						vars.append(", ");
					}
					columns.append(field.getName());
					vars.append("?" + "::\"" + field.getType().getSimpleName() + "\"");
				}
				sql = String.format("INSERT INTO %s(%s) VALUES (%s);", getTableName(genericClass), columns.toString(),
						vars.toString());
				conn.getMetaData().getColumns(null, SCHEMA, genericClass.getSimpleName().toLowerCase(), "%");
				PreparedStatement ps = conn.prepareStatement(sql);

				Field[] fields = genericClass.getDeclaredFields();
				for (int i = 0; i < fields.length; i++) {
					fields[i].setAccessible(true);
					PGobject pgo = new PGobject();
					pgo.setType(fields[i].getType().getSimpleName());
					pgo.setValue(((Enum<?>) fields[i].get(table)).name());
					ps.setObject((i + 1), pgo);
				}
				ps.executeUpdate();

				PreparedStatement query = conn.prepareStatement("SELECT * FROM test.table;");
				ResultSet rs = query.executeQuery();
				while (rs.next()) {
					System.out.println(rs.getString("Industry") + ", " + rs.getString("Abeceda"));
				}
			} catch (SQLException e) {
				System.err.println(sql);
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static <T> String getTableName(Class<T> clazz) {
		return SCHEMA + "." + clazz.getSimpleName();
	}

}
