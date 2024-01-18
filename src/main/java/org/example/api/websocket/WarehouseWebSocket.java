package org.example.api.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.api.model.Warehouse;
import org.example.api.service.WarehouseService;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private static final Set<Session> connectedSessions = Collections.synchronizedSet(new HashSet<>());
    private static final WarehouseService warehouseService = new WarehouseService();

    @OnOpen
    public void onOpen(Session session) {
        connectedSessions.add(session);

        System.out.println("WebSocket connection opened. Session ID: " + session.getId());

        try {
            session.getBasicRemote().sendText("Welcome to the Warehouse WebSocket!");
        } catch (IOException e) {
            System.out.printf("Can not create websocket connection. Reason: %s%n", e.getMessage());
        }
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message from session " + session.getId() + ": " + message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            String messageType = jsonNode.get("type").asText();

            switch (messageType) {
                case "getWarehouses":
                    String sortBy = jsonNode.has("sortBy") ? jsonNode.get("sortBy").asText() : null;
                    String sortOrder = jsonNode.has("sortOrder") ? jsonNode.get("sortOrder").asText() : null;

                    List<Warehouse> warehouses = warehouseService.getAllWarehouses(sortBy, sortOrder);

                    String warehousesJson = objectMapper.writeValueAsString(warehouses);
                    session.getBasicRemote().sendText(warehousesJson);
                    break;
                case "updateWarehouse":
                    Warehouse updatedWarehouse = objectMapper.convertValue(jsonNode.get("warehouse"), Warehouse.class);
                    warehouseService.updateWarehouse(updatedWarehouse);

                    broadcastUpdateMessage(updatedWarehouse);
                    break;
                default:
                    System.out.println("Unknown message type: " + messageType);
            }
        } catch (IOException e) {
            System.out.printf("Can not get websocket message. Reason: %s%n", e.getMessage());
        }
    }


    @OnClose
    public void onClose(Session session) {
        connectedSessions.remove(session);

        System.out.println("WebSocket connection closed. Session ID: " + session.getId());
    }
    private void broadcastMessageToAll(String message) {
        for (Session session : connectedSessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    System.out.printf("Can not inform each session. Reason: %s%n", e.getMessage());
                }
            }
        }
    }

    private void broadcastUpdateMessage(Warehouse updatedWarehouse) {
        try {
            String updatedWarehouseJson = objectMapper.writeValueAsString(updatedWarehouse);

            broadcastMessageToAll(updatedWarehouseJson);
        } catch (IOException e) {
            System.out.printf("Can not broadcast update message. Reason: %s%n", e.getMessage());
        }
    }

}
