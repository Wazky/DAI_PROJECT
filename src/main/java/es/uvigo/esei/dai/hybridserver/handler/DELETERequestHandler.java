package es.uvigo.esei.dai.hybridserver.handler;

import es.uvigo.esei.dai.hybridserver.controler.PagesController;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

import es.uvigo.esei.dai.hybridserver.PageNotFoundException;
import es.uvigo.esei.dai.hybridserver.model.dao.DAOException;

/**
 * Handler for DELETE HTTP requests to remove a page by its UUID.
 * This class extends the BaseRequestHandler to provide specific handling
 * for DELETE requests, including checking for page existence and returning
 * appropriate HTTP responses.
 */
public class DELETERequestHandler extends BaseRequestHandler {
    
    /**
     * Constructs a DELETERequestHandler.
     */
    public DELETERequestHandler() {}

    /**
     * Handles DELETE requests to remove a page by its UUID.
     * If the page exists, it is deleted and a success response is returned.
     * If the page does not exist, a 404 Not Found response is returned.
     * For unsupported resources, a 400 Bad Request response is returned.
     * 
     * @param request The HTTP request to be handled.
     * @param controller The PagesController to manage page operations.
     * @return The HTTP response generated for the request.
     */
    @Override
    public HTTPResponse handle(HTTPRequest request, PagesController controller) {

        // Check if it is a request for a html resource
        if (request.getResourceName().equals("html") && request.getResourceParameters().containsKey("uuid")) {
            
            String uuid = request.getResourceParameters().get("uuid");
            
            try {
                // Check if the requested page exists
                if (controller.exists(uuid)) {

                    // Delete the page and return success response
                    controller.delete(uuid);
                    return removed(createBaseHTTPResponse(request.getHttpVersion()), uuid);
                
                } else {
                    // Return not found response
                    return notFound(createBaseHTTPResponse(request.getHttpVersion()));
                }

            } catch (PageNotFoundException e) {
                // Return not found response
                return notFound(createBaseHTTPResponse(request.getHttpVersion()));
            } catch (DAOException e) {
                // Return internal server error response
                return internalServerError(createBaseHTTPResponse(request.getHttpVersion()));
            }
            
        }

        // For other resources (initially only html files are supported)

        // No supported resource, 400 Bad Request
        return badRequest(createBaseHTTPResponse(request.getHttpVersion()));
    }

    /**
     * Generates a page removed response.
     * 
     * @param response The base HTTP response to be modified.
     * @param uuid The UUID of the removed page.
     * @return The modified HTTP response.
     */
    private HTTPResponse removed(HTTPResponse response, String uuid) {
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", "text/html");

        response.setContent(removedPageHTML(uuid));

        return response;
    }

    /**
     * Generates the HTML content for a removed page response.
     * 
     * @param uuid The UUID of the removed page.
     * @return The HTML content as a string.
     */
    private String removedPageHTML(String uuid) {
        return 
        "<html>" +
            "<head><title>Page Removed</title></head>" +
            "<body>" +
                "<h1>Page Removed</h1>" +
                "<p>The page with UUID: " + uuid + " has been successfully removed.</p>" +
                "<a href=\"/html\">Back to Main Page</a>" +
            "</body>" +
        "</html>";
    }

}
