package prog.paket.sql;

import java.sql.SQLException;

import prog.paket.baza.struct.menutree.TraitNode;

public class TestSQLExchange extends AbstractSQLExchange {

	@Override
	protected void executeStatements() throws SQLException {
		stmt = conn.createStatement();
		resultSet = stmt.executeQuery(statements[0]);
		resultSet.next();
		int id = resultSet.getInt("id");
		String name = resultSet.getString("traitName");
		String abrev = resultSet.getString("abrev");
		returnValue = new TraitNode(id, name, abrev);
	}

	public TraitNode getRoot() {
		doExecute(new String[]{"select id, traitName, abrev, parentTrait from Trait where id=1"});
		return (TraitNode)returnValue;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		long begin = System.currentTimeMillis();
		TestSQLExchange test = new TestSQLExchange();
		System.out.println(test.getRoot());
		System.out.println(test.getRoot());
		System.out.println(test.getRoot());
		System.out.println(test.getRoot());
		System.out.println(test.getRoot());
		long end = System.currentTimeMillis();
		System.out.println("Miliseconds: " + Long.valueOf(end - begin).toString());
	}
}
