package demians;

import com.demians.Exception.DaoOperationException;
import com.demians.dao.DogsDao;
import com.demians.dao.DogsDaoImpl;
import com.demians.model.Breed;
import com.demians.model.Vocation;
import com.demians.util.JdbcUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class DogsTest {
    private static final String FILL_BREEDS_SQL = "" +
            "insert into breeds (name, average_weight, origin, recomended_nickname) values ('Beagle', 10, 'Great Britain', 'Clyde');" +
            "insert into breeds (name, average_weight, origin, recomended_nickname) values ('Greyhound', 37, 'Great Britain', 'Flash');" +
            "insert into breeds (name, average_weight, origin, recomended_nickname) values ('Turkish kangal', 60, 'Turkie', 'Babai');" +
            "insert into breeds (name, average_weight, origin, recomended_nickname) values ('Akita inu', 40, 'Japan', 'Hatiko');" +
            "insert into breeds (name, average_weight, origin, recomended_nickname) values ('Rottweiler', 50, 'Germany', 'Klaus');" +
            "";

    private static final String FILL_VOCATIONS_SQL = "" +
            "insert into vocations (mission) values ('hunting');" +
            "insert into vocations (mission) values ('sheep shearing');" +
            "insert into vocations (mission) values ('guarding');" +
            "insert into vocations (mission) values ('racing');" +
            "";
    private static final String FILL_BREEDS_VOCATIONS_SQL = "" +
            "insert into breeds_vocations (breeds_id, vocations_id) values (1, 1);" +
            "insert into breeds_vocations (breeds_id, vocations_id) values (2, 1);" +
            "insert into breeds_vocations (breeds_id, vocations_id) values (2, 4);" +
            "insert into breeds_vocations (breeds_id, vocations_id) values (3, 2);" +
            "insert into breeds_vocations (breeds_id, vocations_id) values (3, 3);" +
            "insert into breeds_vocations (breeds_id, vocations_id) values (5, 1);" +
            "insert into breeds_vocations (breeds_id, vocations_id) values (5, 2);" +
            "insert into breeds_vocations (breeds_id, vocations_id) values (5, 3);" +
            "";
    private static DogsDao dogsDao;
    private static final Logger logger = LoggerFactory.getLogger(DogsTest.class);

    @BeforeClass
    public static void init() throws SQLException {
        DataSource h2DataSource = JdbcUtil.createDefaultInMemoryH2DataSource();
        createDogsTables(h2DataSource);
        dogsDao = new DogsDaoImpl(h2DataSource);
        fillTestTable(h2DataSource, FILL_BREEDS_SQL);
        fillTestTable(h2DataSource, FILL_VOCATIONS_SQL);
        fillTestTable(h2DataSource, FILL_BREEDS_VOCATIONS_SQL);

    }

    static void fillTestTable(DataSource dataSource, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement vocationStatement = connection.createStatement();
            vocationStatement.executeUpdate(sql);
        }
    }

    private static void createDogsTables(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            Statement createTableStatement = connection.createStatement();
            createTableStatement.execute("CREATE TABLE IF NOT EXISTS breeds (\n" +
                    "  id                    SERIAL NOT NULL,\n" +
                    "  name                  VARCHAR(255) NOT NULL,\n" +
                    "  average_weight        INT NOT NULL,\n" +
                    "  origin                VARCHAR(63),\n" +
                    "  recomended_nickname   VARCHAR(63),\n" +
                    "  CONSTRAINT  breeds_PK PRIMARY KEY (id)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS vocations (\n" +
                    "  id                      SERIAL NOT NULL,\n" +
                    "  mission                 VARCHAR(255) NOT NULL,\n" +
                    "  CONSTRAINT              vocations_PK PRIMARY KEY (id)\n" +
                    ");\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS breeds_vocations (\n" +
                    "  breeds_id bigint NOT NULL,\n" +
                    "  vocations_id bigint NOT NULL,\n" +
                    "  CONSTRAINT breeds_vocations_breeds_id_vocations_id_PK PRIMARY KEY (breeds_id, vocations_id),\n" +
                    "  CONSTRAINT breeds_vocations_breeds_FK FOREIGN KEY (breeds_id) REFERENCES breeds,\n" +
                    "  CONSTRAINT breeds_vocations_vocations_FK FOREIGN KEY (vocations_id) REFERENCES vocations\n" +
                    ");");
        }
    }


    private Breed generateTestBreed(String breedName) {
        return Breed.builder()
                .name(breedName)
                .averageWeight(RandomUtils.nextInt(5, 100))
                .origin(RandomStringUtils.randomAlphabetic(10))
                .recomendedNickname(RandomStringUtils.randomAlphabetic(10))
                .build();
    }



    @Test
    public void testGenerateBreed() throws SQLException {
//        init();

        Breed tibetianMastiff = new Breed();
        tibetianMastiff.setName("Tibetian mastiff");
        tibetianMastiff.setAverageWeight(80);
        tibetianMastiff.setOrigin("Tibet");
        tibetianMastiff.setRecomendedNickname("Bubuh");

        dogsDao.save(tibetianMastiff);

        assertEquals(tibetianMastiff, dogsDao.findOne(6L));
    }

    @Test
    public void testUpdate() throws SQLException {
//        init();

        dogsDao.save(generateTestBreed("Bull terrier"));
        Breed miniBullTerrier = new Breed();
        miniBullTerrier.setId(6L);
        miniBullTerrier.setName("Miniature bull terrier");
        miniBullTerrier.setAverageWeight(20);
        miniBullTerrier.setOrigin("Great Britain");
        miniBullTerrier.setRecomendedNickname("Lucky");

        dogsDao.update(miniBullTerrier);

        assertEquals(miniBullTerrier, dogsDao.findOne(6L));

    }

    @Test
    public void testAddFew() throws SQLException {
//        init();
        dogsDao.save(generateTestBreed("Zwergschnauzer"));
        dogsDao.save(generateTestBreed("Mittelschnauzer"));
        dogsDao.save(generateTestBreed("Riesenschnauzer "));

        logger.info(String.valueOf(dogsDao.findAll().toString()));
        logger.info(String.valueOf(dogsDao.findAll().size()));
        assertEquals(10, dogsDao.findAll().size());
    }

    @Test
    public void testDeleteExtinctBreeds() throws SQLException {
//        init();
        Breed oldEnglishBulldog = generateTestBreed("Old English Bulldog");
        Breed molossus = generateTestBreed("Molossus");
        Breed irishVolfhound = generateTestBreed("Irish volfhound");

        dogsDao.save(oldEnglishBulldog);
        dogsDao.save(molossus);
        dogsDao.save(irishVolfhound);

        dogsDao.remove(oldEnglishBulldog);
        dogsDao.remove(molossus);

        logger.info(dogsDao.findAll().toString());
        assertEquals(7, dogsDao.findAll().size());
    }

    @Test
    public void testBreedSuitableFor() throws SQLException {
//        init();

        List<Breed> naturalBornHunters = dogsDao.breedSuitableFor("hunting");

        assertEquals(3, naturalBornHunters.size());
    }

}
