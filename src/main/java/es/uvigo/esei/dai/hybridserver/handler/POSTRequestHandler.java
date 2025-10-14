package es.uvigo.esei.dai.hybridserver.handler;

import java.util.UUID;
import es.uvigo.esei.dai.hybridserver.model.entity.Page;
import es.uvigo.esei.dai.hybridserver.controler.PagesController;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

import es.uvigo.esei.dai.hybridserver.model.dao.DAOException;

/**
 * Handler for POST HTTP requests to create a new HTML page.
 * This class extends the BaseRequestHandler to provide specific handling
 * for POST requests, including content validation and page creation.
 */
public class POSTRequestHandler extends BaseRequestHandler {
    
    /**
     * Creates a new instance of the POSTRequestHandler.
     */
    public POSTRequestHandler() {}

    /**
     * Handles POST requests to create a new HTML page.
     * Validates the request content and parameters, generates a unique UUID for the new page,
     * and stores it using the PagesController. Returns appropriate HTTP responses based on
     * the outcome of the operation.
     * 
     * @param request The HTTP request to be handled.
     * @param controller The PagesController to manage page operations.
     * @return The HTTP response generated for the request.
     */
    @Override
    public HTTPResponse handle(HTTPRequest request, PagesController controller) {
        // Check if its a request for a html resource
        if (request.getResourceName().equals("html")) {

            // Check for a uuid parameter
            if (request.getResourceParameters().containsKey("uuid")) {
                // POST does not support edit actions
                return badRequest(createBaseHTTPResponse(request.getHttpVersion()));

            }
            
            // Check for content validity
            if (isContentValid(request)) {
                // Generate a new uuid to store the new page
                String uuid = UUID.randomUUID().toString();
                try {
                    // Check if the uuid already exists (very unlikely)
                    while (controller.exists(uuid)) {
                        uuid = UUID.randomUUID().toString();
                    }
                    // Store the new page
                    controller.create(new Page(uuid, request.getResourceParameters().get("html")));                

                } catch (DAOException e) {
                    // Return internal server error response
                    return internalServerError(createBaseHTTPResponse(request.getHttpVersion()));
                } 
                
                return created(createBaseHTTPResponse(request.getHttpVersion()), uuid);
            }

            return badRequest(createBaseHTTPResponse(request.getHttpVersion()));
        }

        // For other resources (initially only html files are supported)

        // No supported resource, 400 Bad Request
        return badRequest(createBaseHTTPResponse(request.getHttpVersion()));

    }

    /**
     * Generates a created page response.
     * 
     * @param response The base HTTP response to be modified.
     * @param uuid The UUID of the created page.
     * @return The modified HTTP response.
     */
    private HTTPResponse created(HTTPResponse response, String uuid) {

        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", "text/html");

        response.setContent(createdPageHTML(uuid));

        return response;
    }

    /**
     * Validates the content of the POST request.
     * Ensures that required headers are present and that the content is not empty.
     * Also checks that the content includes an 'html' parameter.
     * 
     * @param request The HTTP request to validate.
     * @return true if the content is valid, false otherwise.
     */
    private boolean isContentValid(HTTPRequest request) {
        // Check for content absence of required headers
        if (!request.getHeaderParameters().containsKey("Content-Type") 
            || !request.getHeaderParameters().containsKey("Content-Length")) {                
            return false;
        }

        // Check if content is not empty
        if (request.getContent() == null || request.getContent().isEmpty()) {            
            return false;
        }
        
        // Valid content, send as form parameter (html = <content>)
        return request.getResourceParameters().get("html") != null;

    }

    /**
     * Generates the HTML content for a created page response.
     * 
     * @param uuid The UUID of the created page.
     * @return The HTML content for the response.
     */
    private String createdPageHTML(String uuid) {
        return 
        "<html>" +
            "<head><title>200 OK</title></head>" +
            "<body>" +
                "<h1>Page Created</h1>" +
                "<h2>The page <a href=\"html?uuid=" + uuid + "\">" + uuid + "</a> has been successfully created.</h2>" +
            "</body>" +
        "</html>";

    }

}
