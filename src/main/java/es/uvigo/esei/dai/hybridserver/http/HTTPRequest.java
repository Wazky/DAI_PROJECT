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
import java.util.Map;


public class HTTPRequest {

  private HTTPRequestMethod method;
  private String ResourceChain;
  private String httpVersion;


  public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
    // TODO Completar. Cualquier error en el procesado debe lanzar una HTTPParseException
    
    // Wrap it into a bufferedReader
    BufferedReader br = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
    
    parseStartLine(br.readLine());
    
    //Web resource for http request structure: https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Messages


  }

  public HTTPRequestMethod getMethod() {
    // TODO Completar
    return this.method;
  }

  public String getResourceChain() {
    // TODO Completar
    return null;
  }

  public String[] getResourcePath() {
    // TODO Completar
    return null;
  }

  public String getResourceName() {
    // TODO Completar
    return null;
  }

  public Map<String, String> getResourceParameters() {
    // TODO Completar
    return null;
  }

  public String getHttpVersion() {
    // TODO Completar
    return null;
  }

  public Map<String, String> getHeaderParameters() {
    // TODO Completar
    return null;
  }

  public String getContent() {
    // TODO Completar
    return null;
  }

  public int getContentLength() {
    // TODO Completar
    return -1;
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
    String[] parts = line.split(" ");

    // Start line should have 3 parts: Method + /ResourceChain + HTTP/Version
    if (parts.length != 3) {
      throw new HTTPParseException("ERROR: error while parsing start line from http request, 3 fields expected");
      // Upgrade if startline has content check if it fits any of the start line parts for more accurate error msg
    }
    parseMethod(parts[0]);
    //parseResourceChain();
    //parseHttpVersion();

  }

  
  private void parseMethod(String methodString) throws HTTPParseException{
    try {
      this.method = HTTPRequestMethod.valueOf(methodString);

    } catch (IllegalArgumentException e) {
      throw new HTTPParseException("ERROR: Method from http request do not match any existing http methods");       
    }
    
  }

  private void parseResourceChain(String resource) {

  }

}
