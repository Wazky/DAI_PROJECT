package es.uvigo.esei.dai.hybridserver.handler;

import java.util.Map;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class DELETERequestHandler extends BaseRequestHandler {
    
    public DELETERequestHandler() {}

    @Override
    public HTTPResponse handle(HTTPRequest request, Map<String, String> pages) {

        // Check if it is a request for a html resource
        if (request.getResourceName().equals("html") && request.getResourceParameters().containsKey("uuid")) {
            String uuid = request.getResourceParameters().get("uuid");
            // Check if the requested page exists
            if (pages.containsKey(uuid)) {
                pages.remove(uuid);

                return removed(createBaseHTTPResponse(request.getHttpVersion()), uuid);
            } else {
                // Return not found response
                return notFound(createBaseHTTPResponse(request.getHttpVersion()));
            }
        }

        // For other resources (initially only html files are supported)


        // No supported resource, 400 Bad Request
        return badRequest(createBaseHTTPResponse(request.getHttpVersion()));
    }

    private HTTPResponse removed(HTTPResponse response, String uuid) {
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", "text/html");

        response.setContent(removedPageHTML(uuid));

        return response;
    }

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
