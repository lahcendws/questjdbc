package com.jdbc.demojdbc.repository;

import com.jdbc.demojdbc.entity.Person;
import com.jdbc.demojdbc.util.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonRepository implements CrudDao<Person> {
    private final static String DB_URL = "jdbc:mysql://localhost:3306/jbdc?serverTimezone=GMT";
    private final static String DB_USER = "root";
    private final static String DB_PASSWORD = "";
    @Override
    public Person findById(Long id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD
            );
            statement = connection.prepareStatement(
                    "SELECT * FROM person WHERE id = ?;"
            );
            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String lastname = resultSet.getString("lastName");
                int age = resultSet.getInt("age");
                return new Person(id, name, lastname, age);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return null;
    }

    public Person update(Person person) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD
            );
            statement = connection.prepareStatement(
                    "UPDATE person SET name=?, lastName=?, age=? WHERE id=?"
            );
            statement.setString(1, person.getName());
            statement.setString(2, person.getLastName());
            statement.setInt(3, person.getAge());
            statement.setLong(4, person.getId());

            if (statement.executeUpdate() != 1) {
                throw new SQLException("failed to update data");
            }
            return person;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return null;
    }
@Override
    public Person save(Person person) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            connection = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD
            );
            statement = connection.prepareStatement(
                    "INSERT INTO person (name, lastName, age) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, person.getName());
            statement.setString(2, person.getLastName());
            statement.setInt(3,person.getAge());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("failed to insert data");
            }
            generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1);
                person.setId(id);
                return person;
            } else {
                throw new SQLException("failed to get inserted id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(generatedKeys);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return null;
    }
    @Override
    public List<Person> findAll() {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD
            );
            statement = connection.prepareStatement(
                    "SELECT * FROM person;"
            );
            resultSet = statement.executeQuery();

            List<Person> persons = new ArrayList<>();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String lastName = resultSet.getString("lastName");
                int age = resultSet.getInt("age");
                persons.add(new Person(id, name, lastName, age));
            }
            return persons;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
        return null;
    }
    @Override
    public void deleteById(Long id) {

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD
            );
            statement = connection.prepareStatement(
                    "DELETE FROM person WHERE id=?"
            );
            statement.setLong(1, id);

            if (statement.executeUpdate() != 1) {
                throw new SQLException("failed to delete data");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            JdbcUtils.closeStatement(statement);
            JdbcUtils.closeConnection(connection);
        }
    }
}
