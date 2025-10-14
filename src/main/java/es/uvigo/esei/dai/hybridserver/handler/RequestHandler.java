package es.uvigo.esei.dai.hybridserver.handler;

import es.uvigo.esei.dai.hybridserver.controler.PagesController;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

/**
 * Interface defining the contract for handling HTTP requests.
 * Implementing classes should provide specific handling logic
 * for different types of HTTP requests (e.g., GET, POST, DELETE).
 */
public interface RequestHandler {
    
    /**
     * Handles an HTTP request and generates an appropriate HTTP response.
     * 
     * @param request The HTTP request to be handled.
     * @param controller The PagesController to manage page operations.
     * @return The HTTP response generated for the request.
     */
    HTTPResponse handle(HTTPRequest request, PagesController controller);

}
