package rs.trznica.dragan.dao.lucene;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rs.trznica.dragan.entities.struja.Brojilo;

public class BrojiloDaoTest {

	private BrojiloDao dao;
	
	@Before
	public void setup() throws IOException {
		dao = new BrojiloDao("f:/Prog/Lucene");
		dao.deleteAll();

		Brojilo brojilo = new Brojilo();
		brojilo.setId(1l);
		brojilo.setBroj("1234");
		brojilo.setEd("789 dt");
		brojilo.setOpis("neki opis 1");
		dao.save(brojilo);

		brojilo = new Brojilo();
		brojilo.setId(2l);
		brojilo.setBroj("123");
		brojilo.setEd("787 dt");
		brojilo.setOpis("neki opis 2");
		dao.save(brojilo);

		brojilo = new Brojilo();
		brojilo.setId(3l);
		brojilo.setBroj("12");
		brojilo.setEd("78 dt");
		brojilo.setOpis("neki opis 3");
		dao.save(brojilo);
	}
	
	@After
	public void dispose() throws IOException {
		dao.deleteAll();
	}
	
	@Test
	public void testLoad() throws IOException {
		Brojilo brojilo = dao.find(2l);
		Assert.assertNotNull(brojilo);
		Assert.assertEquals(2l, brojilo.getId().longValue());
		Assert.assertEquals("123", brojilo.getBroj());
		Assert.assertEquals("787 dt", brojilo.getEd());
		Assert.assertEquals("neki opis 2", brojilo.getOpis());
	}
	
	@Test
	public void testFindAll() throws IOException {
		List<Brojilo> ret = dao.findAll();
		Assert.assertEquals(3, ret.size());
	}
	
	@Test
	public void testUpdate() throws IOException {
		Brojilo brojilo = new Brojilo();
		brojilo.setId(2l);
		brojilo.setBroj("4");
		brojilo.setEd("4");
		brojilo.setOpis("neki opis 4");
		dao.update(brojilo);
		
		brojilo = dao.find(2l);
		Assert.assertEquals(2l, brojilo.getId().longValue());
		Assert.assertEquals("4", brojilo.getBroj());
		Assert.assertEquals("4", brojilo.getEd());
		Assert.assertEquals("neki opis 4", brojilo.getOpis());
	}
	
	@Test
	public void testMultipleFind() throws IOException {
		List<Long> ids = Arrays.asList(1l, 2l);
		Assert.assertEquals(2, dao.find(ids).size());
	}
}
