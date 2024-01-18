package org.example.api;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.annotation.HandlesTypes;
import javax.servlet.annotation.WebListener;
import javax.websocket.server.ServerEndpoint;

@HandlesTypes({ServerEndpoint.class, WebListener.class})
public class Main {

    public static void main(String[] args) throws Exception {
        startTomcatServer();
    }

    private static void startTomcatServer() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context context = tomcat.addWebapp("", System.getProperty("java.io.tmpdir"));

        Tomcat.addServlet(context, "warehouseServlet", "com.example.api.servlet.WarehouseServlet");
        context.addServletMappingDecoded("/api/warehouses/*", "warehouseServlet");

        tomcat.start();
        tomcat.getServer().await();
    }
}
