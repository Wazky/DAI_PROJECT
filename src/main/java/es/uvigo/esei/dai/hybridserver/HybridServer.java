/**
 *  HybridServer
 *  Copyright (C) 2025 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import es.uvigo.esei.dai.hybridserver.controler.DefaultPagesController;
import es.uvigo.esei.dai.hybridserver.model.dao.PageDAO;
import es.uvigo.esei.dai.hybridserver.model.dao.PageDBDAO;
import es.uvigo.esei.dai.hybridserver.model.dao.PageMapDAO;

public class HybridServer implements AutoCloseable {
  
  private Thread serverThread;
  private ExecutorService threadPool;
  private final static int DEFAULT_NUM_CLIENTS = 50;
  private boolean stop;
  
  private int SERVICE_PORT = 8888;
  private int NUM_CLIENTS;
  private String DB_URL;
  private String DB_USERNAME;
  private String DB_PASSWORD;

  private PageDAO dao;
  private Map<String, String> pages;
  private DefaultPagesController controller;

  /**
   * Initializes the server with default parameters.
   */
  public HybridServer() {
    // Initialize with default parameters 
    defaultInitialization();
    // Initialize default pages
    initDefaultPages();

    // By default, use in memory DAO
    this.dao = new PageMapDAO(this.pages);
    this.controller = new DefaultPagesController(dao);

    this.threadPool = Executors.newFixedThreadPool(DEFAULT_NUM_CLIENTS);

  }

  /**
   * Initializes the server with the provided pages and default parameters.
   * 
   * @param pages A map of page UUIDs to their HTML content.
   */
  public HybridServer(Map<String, String> pages) {
    // Initialize with default parameters
    defaultInitialization();
    // Initialize pages with the provided map
    initDefaultPages();
    
    for (String uuid : pages.keySet()) {
      this.pages.put(uuid, pages.get(uuid));
    }
    
    // Initialize the DAO (Map-based DAO)
    this.dao = new PageMapDAO(pages);
    this.controller = new DefaultPagesController(dao);

    this.threadPool = Executors.newFixedThreadPool(DEFAULT_NUM_CLIENTS);
  }

  /**
   * Initializes the server with parameters from the provided properties.
   * If a property is missing, a default value is used.
   * 
   * @param properties A Properties object containing configuration parameters.
   */
  public HybridServer(Properties properties) {

    this.NUM_CLIENTS = Integer.parseInt(properties.getProperty("numClients", "50"));
    this.SERVICE_PORT = Integer.parseInt(properties.getProperty("port", "8888"));
    this.DB_URL = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/hstestdb");
    this.DB_USERNAME = properties.getProperty("db.user", "hsdb");
    this.DB_PASSWORD = properties.getProperty("db.password", "hsdbpass");

    // Initialize default pages
    //initDefaultPages();

    // Initialize the DAO (DB-based DAO)
    this.dao = new PageDBDAO(DB_URL, DB_USERNAME, DB_PASSWORD);
    this.controller = new DefaultPagesController(dao);

    this.threadPool = Executors.newFixedThreadPool(NUM_CLIENTS);

  }

  public int getPort() {
    return SERVICE_PORT;
  }

  public void start() {
    this.serverThread = new Thread() {
      @Override
      public void run() {
        try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {

          
          while (true) {
            Socket socket = serverSocket.accept();
              if (stop)
                break;
              
              infoParams();
              
              threadPool.execute(new ClientThread(socket, controller));


          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };

    this.stop = false;
    this.serverThread.start();
  }

  @Override
  public void close() {
    // TODO Si es necesario, añadir el código para liberar otros recursos.
    this.stop = true;

    try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
      // Esta conexión se hace, simplemente, para "despertar" el hilo servidor
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      this.serverThread.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    this.serverThread = null;

    
    threadPool.shutdownNow();

    // Add this give error in the tests
    
    try {
      this.threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    
  }

  /**
   * Initializes default server parameters.
   */
  private void defaultInitialization() {
    this.SERVICE_PORT = 8888;
    this.NUM_CLIENTS = 50;
    this.DB_URL = "jdbc:mysql://localhost:3306/hstestdb";
    this.DB_USERNAME = "hsdb";
    this.DB_PASSWORD = "hsdbpass";
  }

  /**
   * Initializes the default HTML pages in the in-memory database.
   */
  private void initDefaultPages() {

    this.pages = new HashMap<>();

    String[] pageUUIDs =  {
      "6df1047e-cf19-4a83-8cf3-38f5e53f7725",
      "79e01232-5ea4-41c8-9331-1c1880a1d3c2",
      "a35b6c5e-22d6-4707-98b4-462482e26c9e",
      "3aff2f9c-0c7f-4630-99ad-27a0cf1af137",
      "77ec1d68-84e1-40f4-be8e-066e02f4e373",
      "8f824126-0bd1-4074-b88e-c0b59d3e67a3",
      "c6c80c75-b335-4f68-b7a7-59434413ce6c",
      "f959ecb3-6382-4ae5-9325-8fcbc068e446",
      "2471caa8-e8df-44d6-94f2-7752a74f6819",
      "fa0979ca-2734-41f7-84c5-e40e0886e408"
    };

    String[] pageContents = {
      "This is the html page 6df1047e-cf19-4a83-8cf3-38f5e53f7725.",
      "This is the html page 79e01232-5ea4-41c8-9331-1c1880a1d3c2.",
      "This is the html page a35b6c5e-22d6-4707-98b4-462482e26c9e.",
      "This is the html page 3aff2f9c-0c7f-4630-99ad-27a0cf1af137.",
      "This is the html page 77ec1d68-84e1-40f4-be8e-066e02f4e373.",
      "This is the html page 8f824126-0bd1-4074-b88e-c0b59d3e67a3.",
      "This is the html page c6c80c75-b335-4f68-b7a7-59434413ce6c.",
      "This is the html page f959ecb3-6382-4ae5-9325-8fcbc068e446.",
      "This is the html page 2471caa8-e8df-44d6-94f2-7752a74f6819.",
      "This is the html page fa0979ca-2734-41f7-84c5-e40e0886e408."
    };  

    for (int i = 0; i < pageUUIDs.length; i++) {
      this.pages.put(pageUUIDs[i], pageContents[i]);
    }
  }

  private void infoParams() {
    System.out.println("Service port: " + SERVICE_PORT);
    System.out.println("Max. number of clients: " + NUM_CLIENTS);
    System.out.println("Database URL: " + DB_URL);
    System.out.println("Database user: " + DB_USERNAME);
    System.out.println("Database password: " + DB_PASSWORD); // For security reasons, do not print the password
  }

}
