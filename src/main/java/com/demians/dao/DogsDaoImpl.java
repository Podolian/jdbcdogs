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

    private static final String NEW_BREED_SQL = "insert into breeds (name, average_weight, origin, origin, recomended_nickname) values (?, ?, ?, ?);";
    private static final String FIND_ALL = "select * from breeds;";
    private static final String UPDATE_SQL = "update breeds set name = ?, average_weight = ?, origin = ?, recomended_nickname = ? ;";
    private static final String BREED_REP_VOCATION_SQL = "select breeds.* from breeds join breeds_vocation bv on breeds.id = bv.breeds_id join vocation on vocation.id = bv.vocation_id and mission = ?;";
    private DataSource dataSource;

    public DogsDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void save(Breed breed) {
        Objects.requireNonNull(breed);
        try (Connection connection = dataSource.getConnection()) {
            saveNewBreed(connection, breed);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Not able to save a new breed - %s", breed.getName()));
        }
    }

    private void saveNewBreed(Connection connection, Breed breed) {
        try {
            PreparedStatement newBreedStatement = connection.prepareStatement(NEW_BREED_SQL, Statement.RETURN_GENERATED_KEYS);
            executeStatementFilledWithBreedData(newBreedStatement, breed);
            newBreedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to save new breed - %s", breed.getName()));
        }
    }

    private PreparedStatement executeStatementFilledWithBreedData(PreparedStatement newBreedStatement, Breed breed) {
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

    private Breed parseRow(ResultSet allBreeds) throws SQLException {
        Breed breed = new Breed();
        breed.setId(allBreeds.getLong("id"));
        breed.setName(allBreeds.getString("name"));
        breed.setAverageWeight(allBreeds.getInt("average_weight"));
        breed.setOrigin(allBreeds.getString("origin"));
        breed.setRecomendedNickname(allBreeds.getString("recomended_nickname"));
        return breed;
    }

    public void update(Breed breed) {
        Objects.requireNonNull(breed);
        try(Connection connection = dataSource.getConnection()){
            changeBreed(connection, breed);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to update breed - %s", breed.getName()));
        }
    }

    private void changeBreed(Connection connection, Breed breed) throws SQLException {
//        checkIfExists(breed, connection);
        PreparedStatement changeStatement = connection.prepareStatement(UPDATE_SQL, Statement.RETURN_GENERATED_KEYS);
        fillUpdateStatement(changeStatement, breed);
        changeStatement.executeUpdate();
    }

    private void fillUpdateStatement(PreparedStatement changeStatement, Breed breed) throws SQLException {
        changeStatement.setString(1, breed.getName());
        changeStatement.setInt(2, breed.getAverageWeight());
        changeStatement.setString(3, breed.getOrigin());
        changeStatement.setString(4, breed.getRecomendedNickname());
    }



    public void remove(Breed breed) {

    }

    public List<Breed> breedSuitableFor(Vocation vocation) {
        Objects.requireNonNull(vocation);
        try(Connection connection = dataSource.getConnection()){
            return breedSuitableForVocation(connection, vocation);
        } catch (SQLException e) {
            throw new DaoOperationException(String.format("Unable to find a suitable breed"));
        }
    }

    private List<Breed> breedSuitableForVocation(Connection connection, Vocation vocation) throws SQLException {
        PreparedStatement vocationStatement = connection.prepareStatement(BREED_REP_VOCATION_SQL);
        vocationStatement.setString(1, vocation.getMission());
        ResultSet suitableBreedsResultSet = vocationStatement.getResultSet();
        return toList(suitableBreedsResultSet);
    }
}
