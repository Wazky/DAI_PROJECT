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

public class HTTPResponse {

  private HTTPResponseStatus status;
  private String version;
  private String content;
  private Map<String, String> parameters = new java.util.HashMap<>();

  private String CRLF = "\r\n";

  public HTTPResponse() {
    // TODO Completar
  }

  public HTTPResponseStatus getStatus() {
    return this.status;
  }

  public void setStatus(HTTPResponseStatus status) {
    // Check it
    validateHTTPResponseStatus(status);
    this.status = status;
  }

  public String getVersion() {
    // Check it
    return this.version;
  }

  public void setVersion(String version) {
    // Check it
    try {
      this.version = validateHTTPVersion(version);
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      this.version = null;
    }
  }

  public String getContent() {
    // Check it
    return this.content;
  }

  public void setContent(String content) {
    // Check it
    if (content != null) {
      this.putParameter("Content-Length", String.valueOf(content.length()));
    }

    this.content = content;
  }

  public Map<String, String> getParameters() {
    // Check it
    return this.parameters;
  }

  public String putParameter(String name, String value) {
    // Check it
    try {
      validateParameter(name, value);      
    } catch (IllegalArgumentException e) {
    
      System.err.println(e.getMessage());
      return null;
    }

    this.parameters.put(name, value);
    return parseParameter(name);
  }

  public boolean containsParameter(String name) {
    // Check it
    return this.parameters.containsKey(name);
  }

  public String removeParameter(String name) {
    // Check it
    if (containsParameter(name)) {
      return new String(name + ": " + this.parameters.remove(name));
    }
    return null;
  }

  public void clearParameters() {
    // Check it
    this.parameters.clear();
  }

  public List<String> listParameters() {
    // Check it
    if (this.parameters.size() == 0) {
      return new ArrayList<String>();
    }
    List<String> params = new ArrayList<String>();
    for (String key : this.parameters.keySet()) {
      params.add(parseParameter(key));
    }
    return params;
  }

  public void print(Writer writer) throws IOException {
    // Check if http response base is valid (status and version)


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
   * Parse HTTP Response status
   * @return Parsed HTTP Response status
   */
  private String parseHTTPResponseStatus() {
    return this.status.getCode() + " " + this.status.getStatus();
  }

  /**
   * Parse HTTP Header parameter
   * @param name Parameter name
   * @return Parsed HTTP Header parameter
   */
  private String parseParameter(String name) {
    String headerParameter = name + ": " + this.parameters.get(name);
    return headerParameter;
  }

  /**
   * Validate HTTP Response status
   * @param status
   */
  private void validateHTTPResponseStatus(HTTPResponseStatus status) {
    // Check status is not null
    if (status == null) {
      throw new IllegalArgumentException("HTTP response status must not be null");
    }
  }

  /**
   * Validate HTTP version
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
   * Validate parameter name and value
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
