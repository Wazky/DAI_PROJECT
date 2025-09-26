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

// Import wazky
  // Inputs
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
// Outputs
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;




public class HybridServer implements AutoCloseable {
  private static final int SERVICE_PORT = 8888;
  private Thread serverThread;
  private boolean stop;

  public HybridServer() {
    // TODO Inicializar con los parámetros por defecto
  }

  public HybridServer(Map<String, String> pages) {
    // TODO Inicializar con la base de datos en memoria conteniendo "pages"
  }

  public HybridServer(Properties properties) {
    // TODO Inicializar con los parámetros recibidos
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
            try (Socket socket = serverSocket.accept()) {
              if (stop)
                break;
              // TODO Responder al cliente
              
              // Obtain the i/o stream from the conection
              InputStream is = socket.getInputStream();
              OutputStream os = socket.getOutputStream();

              // Wrap with inputStreamReader to translate bytes to chars, then wrap it with bufferedReader for efficiency
              BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

              BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

              try {
                HTTPRequest request = new HTTPRequest(reader);
                

                writer.write("GET / HTTP/1.1 \r\n");
                writer.write("Content type: text-plain \r\n");
                writer.write("Content-Length: 11\r\n");
                writer.write("\r\n");
                writer.write("Hello world\r\n");


              } catch (HTTPParseException e) {
                //Handle HTTParseException
              } catch (IOException e) {
                //Handle IOException
              }
              
            }
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
  }
}
