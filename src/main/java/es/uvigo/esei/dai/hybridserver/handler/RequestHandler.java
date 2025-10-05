package es.uvigo.esei.dai.hybridserver.handler;

import java.util.Map;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

public interface RequestHandler {
    
    HTTPResponse handle(HTTPRequest request, Map<String, String> pages);

}
