package es.uvigo.esei.dai.hybridserver.handler;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;
import es.uvigo.esei.dai.hybridserver.controler.PagesController;

/**
 * Base implementation of the RequestHandler interface.
 * This class provides default behavior for handling HTTP requests,
 * returning a 400 Bad Request response for unhandled requests.
 */
public class BaseRequestHandler implements RequestHandler {
    
    /**
     * Default handler that returns a 400 Bad Request response.
     * 
     * @param request The HTTP request to be handled.
     * @return The HTTP response generated for the request.
     */
    @Override
    public HTTPResponse handle(HTTPRequest request, PagesController controller) {
        HTTPResponse response = createBaseHTTPResponse(request.getHttpVersion());

        return badRequest(response);

    }

    /**
     * Creates a base HTTPResponse object with default values.
     * The response version is set to match the request version,
     * and the status is set to 200 OK.
     *
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
     * 
     * @param response The base HTTP response to be modified.
     * @return The modified HTTP response.
     */
    protected HTTPResponse badRequest(HTTPResponse response) {

        response.setStatus(HTTPResponseStatus.S400);    
        response.putParameter("Content-Type", "text/plain");
        response.setContent("400 Bad Request: The server could not understand the request.");

        return response;
    }

    /**
     * Generates a not found response.
     * 
     * @param response The base HTTP response to be modified.
     * @return The modified HTTP response.
     */
    protected HTTPResponse notFound(HTTPResponse response) {
        response.setStatus(HTTPResponseStatus.S404);
        response.putParameter("Content-Type", "text/plain");
        response.setContent("404 Not Found: The requested resource was not found on the server.");

        return response;
    }

    /**
     * Generates an internal server error response.
     * 
     * @param response The base HTTP response to be modified.
     * @return The modified HTTP response.
     */
    protected HTTPResponse internalServerError(HTTPResponse response) {
        response.setStatus(HTTPResponseStatus.S500);
        response.putParameter("Content-Type", "text/plain");
        response.setContent("500 Internal Server Error: The server got an unexpected error couldn't fulfill the request.");

        return response;
    }

}
