package prog.paket.sql;

import java.sql.SQLException;
import java.util.List;

import prog.paket.baza.struct.SongTrait;
import prog.paket.baza.struct.TraitItem;
import prog.paket.baza.struct.menutree.TraitNode;
import prog.paket.sql.entiteti.Trait;

public class TraitSQLExchange extends AbstractSQLExchange {

	@Override
	protected void executeStatements() throws SQLException {
		// TODO Auto-generated method stub
	}

	public Trait createNew(String name, String abrev, long parentId) {}

	public void deleteTrait(long id) {
		stmt = conn.createStatement();
		resultSet = stmt.executeQuery(statements[0]);
	}

	public List<Trait> getAll() {}

	public void saveAll(List<Trait> traits) {}

	public void saveAll(List<TraitNode> traits) {}
}
