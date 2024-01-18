package org.example.api.dao;

import org.example.api.model.Warehouse;
import org.example.api.util.DatabaseUtil;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDAO {
    private static final BasicDataSource dataSource = new BasicDataSource();

    private static final Logger logger = LoggerFactory.getLogger(WarehouseDAO.class);

    private static final String url = DatabaseUtil.getConnectionUrl();
    private static final String user = DatabaseUtil.getUser();
    private static final String pass = DatabaseUtil.getPassword();

    static {
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public Warehouse getWarehouseById(int id) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM warehouse WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToWarehouse(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Error while getting warehouse by id", e);
            throw new RuntimeException("Can not get warehouse by id.");
        }
        return null;
    }

    public void addWarehouse(Warehouse warehouse) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO warehouse VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            setWarehouseParameters(preparedStatement, warehouse);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while add new warehouse", e);
            throw new RuntimeException("Can not add warehouse.");
        }
    }

    public void updateWarehouse(Warehouse warehouse) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE warehouse SET name=?, address_line_1=?, address_line_2=?, city=?, state=?, country=?, inventory_quantity=? WHERE id=?")) {
            setWarehouseParameters(preparedStatement, warehouse);
            preparedStatement.setInt(8, warehouse.getWarehouseId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating warehouse", e);
            throw new RuntimeException("Can not update warehouse.");
        }
    }

    public void deleteWarehouse(int id) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM warehouse WHERE id=?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while deleting warehouse", e);
            throw new RuntimeException("Can not delete warehouse.");
        }
    }

    public boolean isNameUnique(String name) {
        String query = "SELECT COUNT(*) FROM warehouse WHERE name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            logger.error("Error while checking if name is unique for new warehouse", e);
            throw new RuntimeException("Can not execute query \"Is name unique\".");
        }
        return false;
    }

    public boolean isNameUniqueForUpdate(int id, String name) {
        String query = "SELECT COUNT(*) FROM warehouse WHERE name = ? AND id != ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            logger.error("Error while checking if name is unique for update", e);
            throw new RuntimeException("Can not execute query \"Is name unique for update\".");
        }
        return false;
    }

    public boolean isWarehouseEmpty(int id) {
        String query = "SELECT COUNT(*) FROM warehouse WHERE id = ? AND inventory_quantity = 0";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            logger.error("Error while checking if warehouse is empty", e);
            throw new RuntimeException("Can not execute query \"Is warehouse empty\".");
        }
        return false;
    }

    public List<Warehouse> getAllWarehouses(String sortBy, String sortOrder) {
        List<Warehouse> warehouses = new ArrayList<>();
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT * FROM warehouse";
            if (sortBy != null && sortOrder != null) {
                query += " ORDER BY ? " + sortOrder;
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                if (sortBy != null) {
                    preparedStatement.setString(1, sortBy);
                }

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    Warehouse warehouse = mapResultSetToWarehouse(resultSet);
                    warehouses.add(warehouse);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting all warehouses", e);
            throw new RuntimeException("Can not execute query \"Get all warehouses\".");
        }
        return warehouses;
    }

    public List<Warehouse> getWarehousesByFilter(String name, String address, String city, String state, String country, int limit, int offset, String sortBy, String sortOrder) {
        List<Warehouse> warehouses = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = createWarehouseFilterPreparedStatement(connection, name, address, city, state, country, sortBy, sortOrder)) {

            int parameterIndex = 1;

            if (name != null) {
                preparedStatement.setString(parameterIndex++, name);
            }
            if (address != null) {
                preparedStatement.setString(parameterIndex++, address);
            }
            if (city != null) {
                preparedStatement.setString(parameterIndex++, city);
            }
            if (state != null) {
                preparedStatement.setString(parameterIndex++, state);
            }
            if (country != null) {
                preparedStatement.setString(parameterIndex++, country);
            }

            preparedStatement.setInt(parameterIndex++, limit);
            preparedStatement.setInt(parameterIndex, offset);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Warehouse warehouse = mapResultSetToWarehouse(resultSet);
                    warehouses.add(warehouse);
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting warehouses by filter", e);
            throw new RuntimeException("Can not execute query \"Get warehouses by filter\".");
        }
        return warehouses;
    }

    private PreparedStatement createWarehouseFilterPreparedStatement(Connection connection, String name, String address, String city, String state, String country, String sortBy, String sortOrder) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM warehouse WHERE 1=1");

        if (name != null) {
            queryBuilder.append(" AND name = ?");
        }
        if (address != null) {
            queryBuilder.append(" AND address = ?");
        }
        if (city != null) {
            queryBuilder.append(" AND city = ?");
        }
        if (state != null) {
            queryBuilder.append(" AND state = ?");
        }
        if (country != null) {
            queryBuilder.append(" AND country = ?");
        }
        if (sortBy != null && sortOrder != null) {
            queryBuilder.append(" ORDER BY ").append(sortBy).append(" ").append(sortOrder);
        }

        queryBuilder.append(" LIMIT ? OFFSET ?");

        return connection.prepareStatement(queryBuilder.toString());
    }



    private Warehouse mapResultSetToWarehouse(ResultSet resultSet) throws SQLException {
        return new Warehouse(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("address_line_1"),
                resultSet.getString("address_line_2"),
                resultSet.getString("city"),
                resultSet.getString("state"),
                resultSet.getString("country"),
                resultSet.getInt("inventory_quantity")
        );
    }

    private void setWarehouseParameters(PreparedStatement preparedStatement, Warehouse warehouse) throws SQLException {
        preparedStatement.setInt(1, warehouse.getWarehouseId());
        preparedStatement.setString(2, warehouse.getName());
        preparedStatement.setString(3, warehouse.getAddressLine1());
        preparedStatement.setString(4, warehouse.getAddressLine2());
        preparedStatement.setString(5, warehouse.getCity());
        preparedStatement.setString(6, warehouse.getState());
        preparedStatement.setString(7, warehouse.getCountry());
        preparedStatement.setInt(8, warehouse.getInventoryQuantity());
    }
}
