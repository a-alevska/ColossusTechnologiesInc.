package org.example.api.servlet;

import org.example.api.model.Warehouse;
import org.example.api.service.WarehouseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/warehouses")
public class WarehouseServlet extends HttpServlet {
    private WarehouseService warehouseService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        warehouseService = new WarehouseService();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");

        String nameParam = req.getParameter("name");
        String addressParam = req.getParameter("address");
        String cityParam = req.getParameter("city");
        String stateParam = req.getParameter("state");
        String countryParam = req.getParameter("country");
        int limitParam = Integer.parseInt(req.getParameter("limit"));
        int offsetParam = Integer.parseInt(req.getParameter("offset"));

        String sortBy = req.getParameter("sortBy");
        String sortOrder = req.getParameter("sortOrder");

        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            Warehouse warehouse = warehouseService.getWarehouseById(id);

            if (warehouse != null) {
                resp.getWriter().write(objectMapper.writeValueAsString(warehouse));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else if(nameParam != null || addressParam != null || cityParam != null || stateParam != null || countryParam != null){
            List<Warehouse> warehouses = warehouseService.getWarehousesByFilter(nameParam, addressParam, cityParam, stateParam, countryParam, limitParam, offsetParam, sortBy, sortOrder);

            if (warehouses != null) {
                for(Warehouse warehouse: warehouses) resp.getWriter().write(objectMapper.writeValueAsString(warehouse));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        else {
            List<Warehouse> warehouses = warehouseService.getAllWarehouses(sortBy, sortOrder);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(warehouses));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Warehouse newWarehouse = objectMapper.readValue(req.getReader(), Warehouse.class);
        warehouseService.addWarehouse(newWarehouse);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Warehouse updatedWarehouse = objectMapper.readValue(req.getReader(), Warehouse.class);
        warehouseService.updateWarehouse(updatedWarehouse);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");

        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            warehouseService.deleteWarehouse(id);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
