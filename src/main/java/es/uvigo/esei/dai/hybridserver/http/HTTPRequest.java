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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.LinkedHashMap;


public class HTTPRequest {

  private HTTPRequestMethod method;
  private String resourceChain;
  private String[] resourcePath;
  private String resourceName;
  private Map<String, String> resourceParameters = new LinkedHashMap<>();
  private String httpVersion;
  private Map<String, String> headerParameters = new LinkedHashMap<>();
  private int contentLength = 0;
  private String content;

  public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
    // TODO Completar. Cualquier error en el procesado debe lanzar una HTTPParseException
    
    // Wrap it into a bufferedReader
    BufferedReader br = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
    String line = br.readLine();
    System.out.println(line);
    // Parse start line
    parseStartLine(line);
    
    // Parse header lines
    while ((line = br.readLine()) != null && !line.isEmpty()) {
      System.out.println(line);
      parseHeaderLine(line);
    }
    
    // Check if there is content
    if (this.headerParameters.containsKey("Content-Length")) {
      try {
        this.contentLength = Integer.parseInt(this.headerParameters.get("Content-Length"));
      
      } catch (NumberFormatException e) {
        throw new HTTPParseException("ERROR: invalid Content-Length value found in http request header");
      }

      if (this.contentLength < 0) {
        throw new HTTPParseException("ERROR: negative Content-Length value found in http request header");
      }

      // Parse content
      parseContent(br);

    }

  }

  public HTTPRequestMethod getMethod() {
    // Check it
    return this.method;
  }

  public String getResourceChain() {
    // Check it
    return this.resourceChain;
  }

  public String[] getResourcePath() {
    // Check it
    return this.resourcePath;
  }

  public String getResourceName() {
    // Check it
    return this.resourceName;
  }

  public Map<String, String> getResourceParameters() {
    // Check it
    return this.resourceParameters;
  }

  public String getHttpVersion() {
    // Check it 
    return this.httpVersion;
  }

  public Map<String, String> getHeaderParameters() {
    // Check it
    return this.headerParameters;
  }

  public String getContent() {
    // TODO Completar
    return this.content;
  }

  public int getContentLength() {
    // Check it
    return this.contentLength;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder().append(this.getMethod().name()).append(' ')
      .append(this.getResourceChain()).append(' ').append(this.getHttpVersion()).append("\r\n");

    for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
      sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
    }

    if (this.getContentLength() > 0) {
      sb.append("\r\n").append(this.getContent());
    }

    return sb.toString();
  }

  // Additional functions

  /**
   * 
   * @param line the starting line of an HTTP request
   */
  private void parseStartLine(String line) throws HTTPParseException{
    boolean CRLF = true;
    // Check if line is null or empty
    System.out.println("Line content dffd:" + line);
    checkNullOrEmpty(line);
    // Check if line ends with CRLF
    //CRLF = hasCRLFLineEnd(line);  DA ERROR TESTS MIRAR PQ

    String[] parts = line.split(" ");

    // Start line should have 3 parts: Method + /ResourceChain + HTTP/Version
    if (parts.length != 3) {
      throw new HTTPParseException("ERROR: error while parsing start line from http request, 3 fields expected");
      // Upgrade if startline has content check if it fits any of the start line parts for more accurate error msg
    }

    // Parse version first, check if CRLF is needed (NOTE: only supported version is HTTP/1.1)
    parseHttpVersion(parts[2], CRLF);
    parseMethod(parts[0]);
    parseResourceChain(parts[1]);
    

  }

  private void parseHeaderLine(String line) throws HTTPParseException {
    // Check if line is null or empty
    checkNullOrEmpty(line);

    // Header line structure: Key: Value
    if (!line.contains(":")) {
      throw new HTTPParseException("ERROR: invalid header line found in http request");
    }

    // Split line into key and value
    String[] keyValue = line.split(":", 2);
    String key = keyValue[0].trim();
    String value = keyValue[1].trim();
    
    // Key and value must not be empty
    if (key.isEmpty() || value.isEmpty()) {
      throw new HTTPParseException("ERROR: empty key or value found in header line from http request");
    }

    // Add header parameter to map
    this.headerParameters.put(key, value);
  }

  private void parseContent(BufferedReader br) throws IOException, HTTPParseException {
    char[] contentChars = new char[this.contentLength];

    int readChars = br.read(contentChars, 0, this.contentLength);
    if (readChars != this.contentLength) {
      throw new HTTPParseException("ERROR: invalid content length found in http request");
    }

    // Check if content type is application/x-www-form-urlencoded
    String contentType = this.headerParameters.get("Content-Type");
    if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
      Charset charset = StandardCharsets.UTF_8;
      // Check if it includes an especified charset
      if (contentType.contains("charset=")) {
        String charsetName = contentType.substring(contentType.indexOf("charset=") + "charset=".length()).trim();
        try {
          Charset.forName(charsetName);
        } catch (Exception ignored) {} // If charset is not valid, use UTF-8 by default

      }
      
      // Decode content
      this.content = URLDecoder.decode(new String(contentChars), charset);

    } else {
      // Just set content
      this.content = new String(contentChars);
      
    }
    
    if (this.content.contains("=")) {
      // Add content parameters to resource parameters map
      this.resourceParameters = validateResourceParameters(this.content);
    }

    System.out.println("Content: " + this.content);
  }

  /**
   * Parse the method from a string
   * Checks if it is a valid HTTP method
   * @param methodString the string containing the method
   * @throws HTTPParseException if the method is not valid
   */
  private void parseMethod(String methodString) throws HTTPParseException{
    try {
      System.out.println(methodString);
      this.method = HTTPRequestMethod.valueOf(methodString);

    } catch (IllegalArgumentException e) {
      throw new HTTPParseException("ERROR: Method from http request do not match any existing http methods");       
    }
    
  }

  /**
   * Parse the resource chain from a string
   * Validates the resource chain structure and parses it into path, name and parameters
   * @param rchainString the string containing the resource chain
   * @throws HTTPParseException if the resource chain is not valid
   */
  private void parseResourceChain(String rchainString) throws HTTPParseException {
    // Resource chain structure: /path/to/resource?param1=value1&param2=value2 (Parameters are optional)
    String rPathString;

    // Check if structure is valid
    if (!validateResourceChain(rchainString)) {
      throw new HTTPParseException("ERROR: invalid resource chain found in http request");
    }
    // Resource chain is valid (At first check)
    this.resourceChain = rchainString;
    
    // Check if it has no parameters
    if (!rchainString.contains("?")) {
      // No split needed
      rPathString = rchainString;
      // Set empty parameters map
      this.resourceParameters = java.util.Collections.emptyMap();
    
    } else {
      // Split resource chain into path and parameters
      String[] rchainParts = rchainString.split("\\?", 2);
      rPathString = rchainParts[0];

      // Validate and parse parameters
      this.resourceParameters = validateResourceParameters(rchainParts[1]);
    }
    
    // Validate resource path and parse it into array and name
    this.resourcePath = validateResourcePath(rPathString);

    if (this.resourcePath.length == 0) {
      this.resourceName = "";
    } else {
      this.resourceName = String.join("/", this.resourcePath);
    }

  }

  /**
   * Parse the HTTP version from a string
   * @param versionString the string containing the HTTP version
   * @param CRLF true if the line ends with CRLF, false otherwise
   * @throws HTTPParseException if the version is not valid
   */
  private void parseHttpVersion(String versionString, boolean CRLF) throws HTTPParseException {
    // Check if null or empty
    System.out.println("version: " + versionString);
    checkNullOrEmpty(versionString);

    //Validate estructure
    if (!versionString.startsWith("HTTP/")) {
      throw new HTTPParseException("ERROR: http version from http request does not start with HTTP/");
    }

    if (CRLF) {
      /*
      // Check if it is not HTTP/1.1
      if (!versionString.substring(0, versionString.length() - 2).equals("HTTP/1.1")) {
        throw new HTTPParseException("ERROR: http request has CRLF line ending but http version is not HTTP/1.1");
      }
      */
      if (!versionString.equals("HTTP/1.1")) {
        throw new HTTPParseException("ERROR: http request has CRLF line ending but http version is not HTTP/1.1");
      }
      // If it is HTTP/1.1 set it
      this.httpVersion = HTTPHeaders.HTTP_1_1.getHeader();

    } else {
      versionString = versionString.substring(5);
      if (versionString.equals("1.1")) {
        throw new HTTPParseException("ERROR: http version is HTTP/1.1 but line ending is not CRLF");
      
      } else {
        switch (versionString) {
          case "0.9":
          case "1.0":
          case "2":
          case "3":
            throw new HTTPParseException("ERROR: http version " + versionString + " from http request is not supported");
          default:
            throw new HTTPParseException("ERROR: invalid http version found in http request");
        }
      } 
    }

  }

  /**
   * Validate the resource chain string
   * checks if it is null or empty and if it starts with /
   * @param rcString the resource chain string
   * @return true if the resource chain is valid, false otherwise
   * @throws HTTPParseException if the resource chain is null or empty
   */
  private boolean validateResourceChain(String rcString) throws HTTPParseException{
    // Check if null or empty
    System.out.println("Resource chain: " + rcString);
    checkNullOrEmpty(rcString);

    // Resource chain must start with /
    if (!rcString.startsWith("/")) {
      return false;
    }

    // Resource chain must not contain //
    if (rcString.contains("//")) {
      return false;
    }

    // Check for invalid characters
    if (containsInvalidCharacters(rcString)) {
      return false;
    }

    return true;
  }

  /**
   * Validate the resource path string
   * @param rpathString the resource path string
   * @return an array of strings containing the parts of the resource path
   * @throws HTTPParseException if the resource path is not valid
   */
  private String[] validateResourcePath(String rpathString) throws HTTPParseException{
    // Check if path is '/' (root)
    if (rpathString.equals("/")) {
      return new String[0];
    }

    // Split the path by /
    String[] pathParts = rpathString.substring(1).split("/");

    for (String part : pathParts) {
      // Each part must not be empty
      if (part.isEmpty()) {
        throw new HTTPParseException("ERROR: empty part found in resource path from http request");
      }

      if (part.equals(".") || part.equals("..")) {
        throw new HTTPParseException("ERROR: invalid part found in resource path from http request");
      }

    }

    return pathParts;

  }

  /**
   * Validate the resource parameters string
   * @param rparamsString the resource parameters string
   * @return a map containing the resource parameters
   * @throws HTTPParseException if the resource parameters are not valid
   */
  private Map<String, String> validateResourceParameters(String rparamsString) throws HTTPParseException {
    Map<String, String> parameters = new java.util.HashMap<>();

    // Split parameters by &
    String[] params = rparamsString.split("&");

    for (String param: params) {
      // Each parameter must cotain =
      if (!param.contains("=")) {
        throw new HTTPParseException("ERROR: invalid resource parameter found in http request");
      }
      // Split parameter into key and value
      String[] keyValue = param.split("=", 2);
      // Each parameter must have a key and a value
      if (keyValue.length != 2) {
        throw new HTTPParseException("ERROR: invalid resource parameter found in http request");
      }
      // Key and value must not be empty
      if (keyValue[0].isEmpty() || keyValue[1].isEmpty()) {
        throw new HTTPParseException("ERROR: empty key or value found in resource parameter from http request");
      }

      parameters.put(keyValue[0], keyValue[1]);
    }

    return parameters;
  }

  /**
   * Validate if a line ends with \r\n (CRLF)
   * @param line the line to check
   * @return true if the line ends with CRLF, false otherwise
   */
  private boolean hasCRLFLineEnd(String line) {
    return line.endsWith("\r\n");
  }

  /**
   * Check if a string contains invalid characters
   * @param str the string to check
   * @return true if the string contains invalid characters, false otherwise
   */
  private boolean containsInvalidCharacters(String str) {
    for (char c : str.toCharArray()) {
      if (Character.isISOControl(c) || Character.isWhitespace(c) || c == '<' || c == '>' || c == '"' || c == '{' || c == '}' || c == '|' || c == '\\' || c == '^' || c == '~' || c == '`') {
        return true;
      }
    }

    return false;
  }


  /**
   * Check if a string is null or empty
   * @param str the string to check
   * @throws HTTPParseException if the string is null or empty
   */
  private void checkNullOrEmpty(String str) throws HTTPParseException{
    if (str == null || str.isEmpty()) {
      throw new HTTPParseException("ERROR: null or empty string found where content expected");
    }
  }

}
