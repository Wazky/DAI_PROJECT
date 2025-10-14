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
package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Class representing an HTTP response.
 * This class provides methods to set the status, version, content, and parameters of the response,
 * as well as to print the response to a Writer.
 */
public class HTTPResponse {

  private HTTPResponseStatus status;  // HTTP response status
  private String version;  // HTTP version
  private String content; // Content of the response
  private Map<String, String> parameters = new java.util.HashMap<>(); // Parameters of the response

  private String CRLF = "\r\n";

  public HTTPResponse() {
    // TODO Completar
  }

  /**
   * Returns the HTTP response status.
   * 
   * @return The HTTP response status.
   */
  public HTTPResponseStatus getStatus() {
    return this.status;
  }

  /**
   * Sets the HTTP response status.
   * 
   * @param status The HTTP response status to set.
   * @throws IllegalArgumentException if status is null
   */
  public void setStatus(HTTPResponseStatus status) {
    validateHTTPResponseStatus(status);
    this.status = status;
  }

  /**
   * Returns the HTTP version of the response.
   * 
   * @return The HTTP version of the response.
   */
  public String getVersion() {
    return this.version;
  }

  /**
   * Sets the HTTP version of the response.
   * Only "HTTP/1.1" is supported.
   * 
   * @param version The HTTP version to set.
   */
  public void setVersion(String version) {
    try {
      this.version = validateHTTPVersion(version);
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      this.version = null;
    }
  }

  /**
   * Returns the content of the HTTP response.
   * 
   * @return The content of the HTTP response.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Sets the content of the HTTP response.
   * Also updates the "Content-Length" parameter accordingly.
   * 
   * @param content The content to set in the HTTP response.
   */
  public void setContent(String content) {
    if (content != null) {
      this.putParameter("Content-Length", String.valueOf(content.length()));
    }

    this.content = content;
  }

  /**
   * Returns the map of parameters in the HTTP response.
   * 
   * @return The map of parameters in the HTTP response.
   */
  public Map<String, String> getParameters() {
    return this.parameters;
  }

  /**
   * Adds or updates a parameter in the HTTP response.
   * 
   * @param name The name of the parameter.
   * @param value The value of the parameter.
   * @return The added or updated parameter in "name: value" format, or null if the parameter was not added/updated.
   */
  public String putParameter(String name, String value) {
    try {
      validateParameter(name, value);      
    } catch (IllegalArgumentException e) {
    
      System.err.println(e.getMessage());
      return null;
    }

    this.parameters.put(name, value);
    return parseParameter(name);
  }

  /**
   * Checks if a parameter exists in the HTTP response.
   * 
   * @param name The name of the parameter to check.
   * @return true if the parameter exists, false otherwise.
   */
  public boolean containsParameter(String name) {
    return this.parameters.containsKey(name);
  }

  /**
   * Removes a parameter from the HTTP response.
   * 
   * @param name The name of the parameter to remove.
   * @return The removed parameter in "name: value" format, or null if the parameter was not found.
   */
  public String removeParameter(String name) {
    if (containsParameter(name)) {
      return new String(name + ": " + this.parameters.remove(name));
    }
    return null;
  }

  /**
   * Clears all parameters from the HTTP response.
   */
  public void clearParameters() {
    this.parameters.clear();
  }

  /**
   * Lists all parameters in "name: value" format.
   * 
   * @return A list of all parameters in "name: value" format.
   */
  public List<String> listParameters() {
    if (this.parameters.size() == 0) {
      return new ArrayList<String>();
    }
    List<String> params = new ArrayList<String>();
    for (String key : this.parameters.keySet()) {
      params.add(parseParameter(key));
    }
    return params;
  }

  /**
   * Prints the HTTP response to the provided Writer.
   * 
   * @param writer The Writer to print the HTTP response to.
   * @throws IOException If an I/O error occurs.
   */
  public void print(Writer writer) throws IOException {

    // Print status line
    writer.write(this.version + " " + parseHTTPResponseStatus() + CRLF);

    // Print parameters
    for (String param : listParameters()) {
      writer.write(param + CRLF);
    }

    // Print a blank line to indicate the end of the header section
    writer.write(CRLF);

    // Print content if available
    if (this.parameters.containsKey("Content-Length") && this.content != null) {
      writer.write(this.content);
      // ADD case has encoded content
    }

  }

  /**
   * Returns the string representation of the HTTP response.
   * 
   * @return The string representation of the HTTP response.
   */
  @Override
  public String toString() {
    try (final StringWriter writer = new StringWriter()) {
      this.print(writer);

      return writer.toString();
    } catch (IOException e) {
      throw new RuntimeException("Unexpected I/O exception", e);
    }
  }

// ADITIONAL FUNCTIONS

  /**
   * Parse HTTP response status to "code status" format
   * 
   * @return Parsed HTTP response status in "code status" format
   */
  private String parseHTTPResponseStatus() {
    return this.status.getCode() + " " + this.status.getStatus();
  }

  /**
   * Parse parameter to "name: value" format
   * 
   * @param name Parameter name
   * @return Parsed parameter in "name: value" format
   */
  private String parseParameter(String name) {
    String headerParameter = name + ": " + this.parameters.get(name);
    return headerParameter;
  }

  /**
   * Validate HTTP response status.
   * 
   * @param status HTTP response status to validate
   * @throws IllegalArgumentException if status is null
   */
  private void validateHTTPResponseStatus(HTTPResponseStatus status) {
    // Check status is not null
    if (status == null) {
      throw new IllegalArgumentException("HTTP response status must not be null");
    }
  }

  /**
   * Validate HTTP version.
   * 
   * @param version HTTP version to validate
   * @return Validated HTTP version (Only "HTTP/1.1" supported)
   * @throws IllegalArgumentException if version is null or invalid
   */
  private String validateHTTPVersion(String version) throws IllegalArgumentException {
    // Check version is not null
    if (version == null) {
      throw new IllegalArgumentException("HTTP version must not be null");
    }

    // Check version is invalid
    if (!version.equals(HTTPHeaders.HTTP_1_1.getHeader())) {
      throw new IllegalArgumentException("Invalid HTTP version: " + version);
    }

    // Return valid version
    return HTTPHeaders.HTTP_1_1.getHeader();
  }

  /**
   * Validate parameter name and value.
   * 
   * @param name Parameter name
   * @param value Parameter value
   * @throws IllegalArgumentException if name or value are null or empty
   */
  private void validateParameter(String name, String value) throws IllegalArgumentException {
    // Check name is not null or empty
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Parameter name must not be null or empty");
    }

    // Check value is not null or empty
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Parameter value must not be null or empty");
    }

  }

}
