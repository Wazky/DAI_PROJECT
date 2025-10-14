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

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;



public class Launcher {
  public static void main(String[] args) {
    
    HybridServer server = null;
    boolean error = false;  // To track if an error occurred during initialization

    switch (args.length) {

      // Default initialization of HybridServer
      case 0:
        server = new HybridServer();
        break;

      // Initialization of HybridServer with a configuration file
      case 1:
        Properties config = new Properties();

        try {
          // Load configuration from the specified file
          config.load(new FileInputStream(args[0]));
        
        } catch (FileNotFoundException e) {
          System.err.println("Configuration file not found: " + args[0]);
          e.printStackTrace();
          error = true;
          break;

        } catch (IOException e) {
          System.err.println("Error loading configuration file: " + e.getMessage());
          e.printStackTrace();
          error = true;
        }

        server = new HybridServer(config);
        break;

      // Invalid usage
      default:
        System.err.println("Usage: java es.uvigo.esei.dai.hybridserver.Launcher [config-file]");
        error = true;
        break;
    }
    
    // Exit if there was an error during initialization
    if (error) {
      System.exit(1);
    }
    
    // Start the server
    server.start();

  }
}
