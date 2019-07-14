package com.revolut.retail.ob;

import com.revolut.retail.ob.common.Constants;
import com.revolut.retail.ob.controller.TransactionController;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Main Class (Starting point)
 */
public final class Application {

    private static final Logger LOG = Logger.getLogger(Application.class);

    public static Server server;

    public static void main(String[] args) throws Exception {
        initServer();
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

    public static void initServer() {
        if (server == null) {
            synchronized (Application.class) {
                if (server == null) {
                    LOG.info("Initiating the server....");
                    QueuedThreadPool threadPool = new QueuedThreadPool(100, 10, 120);
                    server = new Server(threadPool);
                    ServerConnector connector = new ServerConnector(server);
                    connector.setPort(Constants.WEB_APPLICATION_PORT);
                    server.setConnectors(new Connector[]{connector});
                    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                    context.setContextPath("/");
                    server.setHandler(context);
                    ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
                    jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
                        TransactionController.class.getCanonicalName());
                    LOG.info("Server initialization is done..");
                }
            }
        }
    }

}
