package prog.paket.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractSQLExchange {

	protected String[] statements;

	protected Object returnValue;

	protected Connection conn = null;

	protected Statement stmt = null;

	protected ResultSet resultSet = null;

	private void startConnection() throws SQLException {
		conn = DriverManager.getConnection(
				"jdbc:mysql://localhost/sdplayer", "root", "root");
	}

	private void cleanUpConnection() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected abstract void executeStatements() throws SQLException;

	/*
	 * Preporucuje se da se doExecute koristi unutar drugih metoda
	 * npr. returnMusicCategory(String categoryName)
	 */
	public void doExecute(String[] statements) {
		this.statements = statements;
		try {
			startConnection();
			executeStatements();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			cleanUpConnection();
		}
	}
}
