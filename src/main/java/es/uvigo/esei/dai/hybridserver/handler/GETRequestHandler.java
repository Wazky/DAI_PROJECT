package es.uvigo.esei.dai.hybridserver.handler;

import es.uvigo.esei.dai.hybridserver.model.entity.Page;
import es.uvigo.esei.dai.hybridserver.controler.PagesController;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

import es.uvigo.esei.dai.hybridserver.PageNotFoundException;
import es.uvigo.esei.dai.hybridserver.model.dao.DAOException;

/**
 * Handler for GET HTTP requests to retrieve pages or the welcome/main page.
 * This class extends the BaseRequestHandler to provide specific handling
 * for GET requests, including serving the welcome page, main page listing,
 * and individual HTML pages by UUID.
 */
public class GETRequestHandler extends BaseRequestHandler {

    /**
     * Creates a new instance of the GETRequestHandler.
     */
    public GETRequestHandler() {}

    /**
     * Handles GET requests to retrieve pages or the welcome/main page.
     * Supports requests for the root resource ("/"), specific HTML pages by UUID,
     * and lists all available HTML pages.
     * For unsupported resources or parameters, a 400 Bad Request response is returned.
     * 
     * @param request The HTTP request to be handled.
     * @param controller The PagesController to manage page operations.
     * @return The HTTP response generated for the request.
     */
    @Override
    public HTTPResponse handle(HTTPRequest request, PagesController controller) {

        // Check if its a request for the root resource
        if (request.getResourceChain().equals("/")) {
            return welcomePage(createBaseHTTPResponse(request.getHttpVersion()));
        }

        // Check if its a request for a html resource
        if (request.getResourceName().equals("html")) {
            // Check if it doesnt request an especific page
            if (request.getResourceParameters().isEmpty()) {
                return mainPage(createBaseHTTPResponse(request.getHttpVersion()), controller);
            }
            
            // Check for an uuid parameter
            if (request.getResourceParameters().containsKey("uuid")) {
                String uuid = request.getResourceParameters().get("uuid");
                try {
                    // Check if the requested page exists
                    if (controller.exists(uuid)) {                        
                        
                        return requestedPage(createBaseHTTPResponse(request.getHttpVersion()), controller.get(uuid).getContent());

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
            
            // Logic for other parameters here

            // No supported parameter, 400 Bad Request
            return badRequest(createBaseHTTPResponse(request.getHttpVersion()));
        }

        // For other resources (initially only html files are supported)

        // No supported resource, 400 Bad Request
        return badRequest(createBaseHTTPResponse(request.getHttpVersion()));

    }

    /**
     * Generates a requested page response.
     * 
     * @param response The base HTTP response to be modified.
     * @param pageContent The content of the requested page.
     * @return The modified HTTP response.
     */
    private HTTPResponse requestedPage(HTTPResponse response, String pageContent) {
        response.putParameter("Content-Type", "text/html");
        response.setContent(pageContent);

        return response;
    }

    /**
     * Generates a main page response listing available HTML pages.
     * 
     * @param response The base HTTP response to be modified.
     * @param pages A map of available pages.
     * @return The modified HTTP response.
     */
    private HTTPResponse mainPage(HTTPResponse response, PagesController controller) {
        response.putParameter("Content-Type", "text/html");

        
        String content = "<html><body><h1>Available Pages</h1><ul>";

        try {
            for (Page page : controller.list()) {
                content += "<li><a href='html?uuid=" + page.getUuid() + "'>" + page.getUuid() + "</a></li>";
            }
        } catch (DAOException e) {
            // Return internal server error response
            return internalServerError(createBaseHTTPResponse(response.getVersion()));
        }

        content += "</ul></body></html>";

        response.setContent(content);

        return response;
    }

    /**
     * Generates a welcome page response.
     * 
     * @param response The base HTTP response to be modified.
     * @return The modified HTTP response.
     */
    private HTTPResponse welcomePage(HTTPResponse response) {

        response.putParameter("Content-Type", "text/html");
        response.setContent("<html><body><h1>Welcome to the Hybrid Server</h1><h2>Ismael Salgado Lopez</h2></body></html>");

        return response;
    }

}
