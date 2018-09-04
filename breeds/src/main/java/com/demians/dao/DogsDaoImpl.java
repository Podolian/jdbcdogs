package com.demians.dao;

import com.demians.Exception.DaoOperationException;
import com.demians.model.Breed;
import com.demians.model.Vocation;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DogsDaoImpl implements DogsDao {

    private static final String NEW_BREED_SQL = "insert into breeds (name, average_weight, origin, recomended_nickname) values (?, ?, ?, ?);";
    private static final String FIND_ALL = "select * from breeds;";
    private static final String UPDATE_SQL = "update breeds set name = ?, average_weight = ?, origin = ?, recomended_nickname = ? ;";
    private static final String BREED_REP_VOCATION_SQL = "" +
            "select breeds.* " +
            "from breeds " +
            "join breeds_vocations bv on breeds.id = bv.breeds_id " +
            "join vocations on vocations.id = bv.vocations_id and mission = ?;";
    private static final String FIND_SINGLE_BREED_BY_ID = "select * from breeds where id = ?;";
    private static final String REMOVE_BREED_SQL = "delete from breeds where id = ? ;";
    private DataSource dataSource;

    public DogsDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Breed breed) {
        Objects.requireNonNull(breed);
        try (Connection connection = dataSource.getConnection()) {
            saveNewBreed(connection, breed);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Not able to save a new breed - %s", breed.getName()), e);
        }
    }

    private void saveNewBreed(Connection connection, Breed breed) {
        try {
            PreparedStatement newBreedStatement = connection.prepareStatement(NEW_BREED_SQL, Statement.RETURN_GENERATED_KEYS);
            prepareStatementFilledWithBreedData(newBreedStatement, breed);
            executeUpdate(newBreedStatement);
            long id = fetchIdFromGeneratedKeys(newBreedStatement);
            breed.setId(id);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to save new breed - %s", breed.getName()),e);
        }
    }

    private long fetchIdFromGeneratedKeys(PreparedStatement newBreedStatement) throws SQLException {

        ResultSet resultSet = newBreedStatement.getGeneratedKeys();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            throw new DaoOperationException("Unable to retrive an id");
        }

    }

    private void executeUpdate(PreparedStatement newBreedStatement) throws SQLException {
        long rowsAffected = newBreedStatement.executeUpdate();
        if (rowsAffected == 0) {
            throw new DaoOperationException(String.format("No rows affected"));
        }
    }

    private PreparedStatement prepareStatementFilledWithBreedData(PreparedStatement newBreedStatement, Breed breed) {
        try {
            newBreedStatement.setString(1, breed.getName());
            newBreedStatement.setInt(2, breed.getAverageWeight());
            newBreedStatement.setString(3, breed.getOrigin());
            newBreedStatement.setString(4, breed.getRecomendedNickname());
            return newBreedStatement;
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to fullfill the statement with data of %s", breed.getName()));
        }
    }


    @Override
    public List<Breed> findAll() {
        try (Connection connection = dataSource.getConnection()) {
            return findAllBreeds(connection);
        } catch (SQLException e) {
            throw new DaoOperationException("Unable to reflect all breeds");
        }
    }

    private List<Breed> findAllBreeds(Connection connection) {
        try {
            Statement findStatement = connection.createStatement();
            ResultSet allBreeds = findStatement.executeQuery(FIND_ALL);
            return toList(allBreeds);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to find all"));
        }
    }

    private List<Breed> toList(ResultSet allBreeds) throws SQLException {
        List<Breed> listOfBreeds = new ArrayList<>();
        while (allBreeds.next()) {
            Breed breed = parseRow(allBreeds);
            listOfBreeds.add(breed);
        }
        return listOfBreeds;
    }

    private Breed parseRow(ResultSet aBreed) throws SQLException {
        Breed breed = new Breed();
        breed.setId(aBreed.getLong("id"));
        breed.setName(aBreed.getString("name"));
        breed.setAverageWeight(aBreed.getInt("average_weight"));
        breed.setOrigin(aBreed.getString("origin"));
        breed.setRecomendedNickname(aBreed.getString("recomended_nickname"));
        return breed;
    }

    @Override
    public Breed findOne(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            return findBreedById(connection, id);
        } catch (SQLException e) {
            throw new DaoOperationException("Unable to find one");
        }
    }

    private Breed findBreedById(Connection connection, Long id) {
        try {
            PreparedStatement findStatement = connection.prepareStatement(FIND_SINGLE_BREED_BY_ID);
            findStatement.setLong(1, id);
            ResultSet breedResultSet = findStatement.executeQuery();
            return retrieveBreed(breedResultSet, id);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to find a breed for id - %d", id), e);
        }
    }

    private Breed retrieveBreed(ResultSet breedResultSet, Long id) throws SQLException {
        if (breedResultSet.next()) {
            return parseRow(breedResultSet);
        } else {
            throw new DaoOperationException(String.format("The breed with id - %d doesn't exists", id));
        }
    }

    @Override
    public void update(Breed breed) {
        Objects.requireNonNull(breed);
        try (Connection connection = dataSource.getConnection()) {
            checkIfExists(connection, breed);
            changeBreed(connection, breed);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to update breed - %s", breed.getName()), e);
        }
    }

    private void changeBreed(Connection connection, Breed breed) throws SQLException {
        PreparedStatement changeStatement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS);
        fillUpdateStatement(changeStatement, breed);
        changeStatement.executeUpdate();
    }

    private void checkIfExists(Connection connection, Breed breed) {
        Long id = breed.getId();
        if(id==null){
            throw new DaoOperationException(String.format("Unable to find breed by id - %d while checking if exists", id));
        }
        findBreedById(connection, id);
    }

    private void fillUpdateStatement(PreparedStatement changeStatement, Breed breed) throws SQLException {
        changeStatement.setString(1, breed.getName());
        changeStatement.setInt(2, breed.getAverageWeight());
        changeStatement.setString(3, breed.getOrigin());
        changeStatement.setString(4, breed.getRecomendedNickname());
    }


    @Override
    public void remove(Breed breed) {
        Objects.requireNonNull(breed);
        try(Connection connection = dataSource.getConnection()) {
            checkIfExists(connection, breed);
            removeBreed(connection, breed);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to remove breed - %s", breed.getName()));
        }

    }

    private void removeBreed(Connection connection, Breed breed) throws SQLException {
            PreparedStatement removeStatement = connection.prepareStatement(REMOVE_BREED_SQL);
            removeStatement.setLong(1, breed.getId());
            removeStatement.executeUpdate();


    }

    @Override
    public List<Breed> breedSuitableFor(String mission) {
//        Objects.requireNonNull(vocation);
        try (Connection connection = dataSource.getConnection()) {
            return breedSuitableForVocation(connection, mission);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to find a suitable breed"), e);
        }
    }

    private List<Breed> breedSuitableForVocation(Connection connection, String mission) throws SQLException {
        PreparedStatement vocationStatement = connection.prepareStatement(BREED_REP_VOCATION_SQL);
        vocationStatement.setString(1, mission);
        ResultSet suitableBreedsResultSet = vocationStatement.executeQuery();
        return toList(suitableBreedsResultSet);
    }
}
