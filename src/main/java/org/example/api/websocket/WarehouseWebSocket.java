package org.example.api.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.api.model.Warehouse;
import org.example.api.service.WarehouseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@ServerEndpoint("/warehouseWebSocket")
public class WarehouseWebSocket {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(WarehouseWebSocket.class);

    private static final Set<Session> connectedSessions = Collections.synchronizedSet(new HashSet<>());
    private static final WarehouseService warehouseService = new WarehouseService();

    @OnOpen
    public void onOpen(Session session) {
        connectedSessions.add(session);

        logger.error("WebSocket connection opened. Session ID: " + session.getId());

        try {
            session.getBasicRemote().sendText("Welcome to the Warehouse WebSocket!");
        } catch (IOException e) {
            logger.error("Can not create websocket connection. Reason: %s%n" + e);
        }
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        logger.error("Received message from session " + session.getId() + ": " + message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            String messageType = jsonNode.get("type").asText();
            String sortBy;
            String sortOrder;

            switch (messageType) {
                case "getWarehouses":
                    sortBy = jsonNode.has("sortBy") ? jsonNode.get("sortBy").asText() : null;
                    sortOrder = jsonNode.has("sortOrder") ? jsonNode.get("sortOrder").asText() : null;

                    List<Warehouse> warehouses = warehouseService.getAllWarehouses(sortBy, sortOrder);

                    String warehousesJson = objectMapper.writeValueAsString(warehouses);
                    session.getBasicRemote().sendText(warehousesJson);
                    break;
                case "getWarehousesByFilter":
                    String name = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
                    String address = jsonNode.has("address") ? jsonNode.get("address").asText() : null;
                    String city = jsonNode.has("city") ? jsonNode.get("city").asText() : null;
                    String state = jsonNode.has("state") ? jsonNode.get("state").asText() : null;
                    String country = jsonNode.has("country") ? jsonNode.get("country").asText() : null;
                    int limit = jsonNode.has("limit") ? jsonNode.get("limit").asInt() : 0;
                    int offset = jsonNode.has("offset") ? jsonNode.get("offset").asInt() : 0;
                    sortBy = jsonNode.has("sortBy") ? jsonNode.get("sortBy").asText() : null;
                    sortOrder = jsonNode.has("sortOrder") ? jsonNode.get("sortOrder").asText() : null;
                    List<Warehouse> warehousesByFilter = warehouseService.getWarehousesByFilter(name, address, city, state, country, limit, offset, sortBy, sortOrder);

                    String warehousesByFilterJson = objectMapper.writeValueAsString(warehousesByFilter);
                    session.getBasicRemote().sendText(warehousesByFilterJson);
                    break;
                case "updateWarehouse":
                    Warehouse updatedWarehouse = objectMapper.convertValue(jsonNode.get("warehouse"), Warehouse.class);
                    warehouseService.updateWarehouse(updatedWarehouse);

                    broadcastUpdateMessage(updatedWarehouse);
                    break;
                default:
                    session.getBasicRemote().sendText("Unknown message type: " + messageType);
                    logger.error("Unknown message type: " + messageType);
            }
        } catch (IOException e) {
            logger.error("Can not get websocket message. Reason: %s%n" + e);
        }
    }


    @OnClose
    public void onClose(Session session) {
        connectedSessions.remove(session);

        logger.error("WebSocket connection closed. Session ID: " + session.getId());
    }
    private void broadcastMessageToAll(String message) {
        for (Session session : connectedSessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.error("Can not inform each session. Reason: %s%n" + e);
                }
            }
        }
    }

    private void broadcastUpdateMessage(Warehouse updatedWarehouse) {
        try {
            String updatedWarehouseJson = objectMapper.writeValueAsString(updatedWarehouse);

            broadcastMessageToAll(updatedWarehouseJson);
        } catch (IOException e) {
            logger.error("Can not broadcast update message. Reason: %s%n" + e);
        }
    }

}
