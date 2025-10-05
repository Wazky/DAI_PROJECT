package es.uvigo.esei.dai.hybridserver.handler;

import java.util.Map;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;


public class BaseRequestHandler implements RequestHandler {
    
    /**
     * Handles an HTTP request and generates an appropriate HTTP response.
     * The default implementation of the base handler returns a 501 Not Implemented response.
     * Subclasses should override this method to provide specific handling for different HTTP methods.
     * @param request The HTTP request to be handled.
     * @return The generated HTTP response.
     */
    @Override
    public HTTPResponse handle(HTTPRequest request, Map<String, String> pages) {
        HTTPResponse response = createBaseHTTPResponse(request.getHttpVersion());

        response.setStatus(HTTPResponseStatus.S501);
        response.putParameter("Allow", "GET, POST, PUT, DELETE");
        response.putParameter("Content-Type", "text/plain");
        response.setContent("501 Not Implemented: The requested method is not implemented by the server.");

        return response;

    }

    /**
     * Creates a base HTTPResponse object with default values.
     * The response version is set to match the request version, and the status is set to
     * 200 OK.
     * @param httpVersion The HTTP version from the request.
     * @return The created HTTPResponse object.
     */
    protected HTTPResponse createBaseHTTPResponse(String httpVersion) {
        HTTPResponse response = new HTTPResponse();
        response.setVersion(httpVersion);
        response.setStatus(HTTPResponseStatus.S200);

        return response;
    }

    /**
     * Generates a bad request response.
     * @param response The base HTTP response to be modified.
     * @return The modified HTTP response.
     */
    protected HTTPResponse badRequest(HTTPResponse response) {

        response.setStatus(HTTPResponseStatus.S400);    
        response.putParameter("Content-Type", "text/plain");
        response.setContent("400 Bad Request: The server could not understand the request due to invalid syntax.");

        return response;
    }

    /**
     * Generates a welcome page response.
     * @param response The base HTTP response to be modified.
     * @return The modified HTTP response.
     */
    protected HTTPResponse notFound(HTTPResponse response) {
        response.setStatus(HTTPResponseStatus.S404);
        response.putParameter("Content-Type", "text/plain");
        response.setContent("404 Not Found: The requested resource was not found on the server.");

        return response;
    }

}
