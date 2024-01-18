package org.example.api.service;

import org.example.api.dao.WarehouseDAO;
import org.example.api.model.Warehouse;

import java.util.List;

public class WarehouseService {
    private final WarehouseDAO warehouseDAO;

    public WarehouseService() {
        this.warehouseDAO = new WarehouseDAO();
    }

    public List<Warehouse> getAllWarehouses(String sortBy, String sortOrder) {
        return warehouseDAO.getAllWarehouses(sortBy, sortOrder);
    }

    public List<Warehouse> getWarehousesByFilter(String name, String address, String city, String state, String country, int limit, int offset, String sortBy, String sortOrder) {
        return warehouseDAO.getWarehousesByFilter(name, address, city, state, country, limit, offset, sortBy, sortOrder);
    }

    public Warehouse getWarehouseById(int id) {
        return warehouseDAO.getWarehouseById(id);
    }

    public void addWarehouse(Warehouse warehouse) {
        if (isValidWarehouse(warehouse)) {
            if (isNameUnique(warehouse.getName())) {
                warehouseDAO.addWarehouse(warehouse);
            } else {
                System.out.println("Error: Warehouse with this name is already exist.");
            }
        } else {
            System.out.println("Error: Wrong data for creation.");
        }
    }

    public void updateWarehouse(Warehouse warehouse) {
        if (isValidWarehouse(warehouse)) {
            if (isNameUniqueForUpdate(warehouse.getWarehouseId(), warehouse.getName())) {
                warehouseDAO.updateWarehouse(warehouse);
            } else {
                System.out.println("Error: Warehouse with this name is already exist.");
            }
        } else {
            System.out.println("Error: Wrong data for update warehouse.");
        }
    }

    public void deleteWarehouse(int id) {
        if (isWarehouseEmpty(id)) {
            warehouseDAO.deleteWarehouse(id);
        } else {
            System.out.println("Error: Can not delete warehouse, because it contains goods.");
        }
    }

    private boolean isValidWarehouse(Warehouse warehouse) {
        return warehouse != null &&
                warehouse.getName() != null && !warehouse.getName().isEmpty() &&
                warehouse.getAddressLine1() != null && !warehouse.getAddressLine1().isEmpty() &&
                warehouse.getCity() != null && !warehouse.getCity().isEmpty() &&
                warehouse.getState() != null && !warehouse.getState().isEmpty() &&
                warehouse.getCountry() != null && !warehouse.getCountry().isEmpty();
    }


    private boolean isNameUnique(String name) {
        return warehouseDAO.isNameUnique(name);
    }


    private boolean isNameUniqueForUpdate(int id, String name) {
        return warehouseDAO.isNameUniqueForUpdate(id, name);
    }


    private boolean isWarehouseEmpty(int id) {
        return warehouseDAO.isWarehouseEmpty(id);
    }
}
