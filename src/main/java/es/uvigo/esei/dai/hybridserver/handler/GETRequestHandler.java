package es.uvigo.esei.dai.hybridserver.handler;

import java.util.Map;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class GETRequestHandler extends BaseRequestHandler {
    
    public GETRequestHandler() {}

    @Override
    public HTTPResponse handle(HTTPRequest request, Map<String, String> pages) {

        // Check if its a request for the root resource
        if (request.getResourceChain().equals("/")) {
            return welcomePage(createBaseHTTPResponse(request.getHttpVersion()));
        }

        // Check if its a request for a html resource
        if (request.getResourceName().equals("html")) {
            // Check if it doesnt request an especific page
            if (request.getResourceParameters().isEmpty()) {
                return mainPage(createBaseHTTPResponse(request.getHttpVersion()), pages);
            }
            
            // Check for an uuid parameter
            if (request.getResourceParameters().containsKey("uuid")) {
                String uuid = request.getResourceParameters().get("uuid");
                // Check if the requested page exists
                if (pages.containsKey(uuid)) {
                    return requestedPage(createBaseHTTPResponse(request.getHttpVersion()), pages.get(uuid));
                } else {
                    // Return not found response
                    return notFound(createBaseHTTPResponse(request.getHttpVersion()));
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

    private HTTPResponse requestedPage(HTTPResponse response, String pageContent) {
        response.putParameter("Content-Type", "text/html");
        response.setContent(pageContent);

        return response;
    }

    /**
     * Generates a main page response listing available HTML pages.
     * @param response The base HTTP response to be modified.
     * @param pages A map of available pages.
     * @return The modified HTTP response.
     */
    private HTTPResponse mainPage(HTTPResponse response, Map<String, String> pages) {
        response.putParameter("Content-Type", "text/html");

        String content = "<html><body><h1>Available Pages</h1><ul>";
        for (String key : pages.keySet()) {
            content += "<li><a href='html?uuid=" + key + "'>" + key + "</a></li>";
        }
        content += "</ul></body></html>";

        response.setContent(content);

        return response;
    }

    /**
     * Generates a welcome page response.
     * @param response The base HTTP response to be modified.
     * @return The modified HTTP response.
     */
    private HTTPResponse welcomePage(HTTPResponse response) {

        response.putParameter("Content-Type", "text/html");
        response.setContent("<html><body><h1>Welcome to the Hybrid Server</h1><h2>Ismael Salgado Lopez</h2></body></html>");

        return response;
    }

}
