package es.uvigo.esei.dai.hybridserver.handler;

import java.util.Map;
import java.util.UUID;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class POSTRequestHandler extends BaseRequestHandler {
    
    public POSTRequestHandler() {}

    @Override
    public HTTPResponse handle(HTTPRequest request, Map<String, String> pages) {
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
                // Check if the uuid already exists (very unlikely)
                while (pages.containsKey(uuid)) {
                    uuid = UUID.randomUUID().toString();
                }
                // Store the new page
                pages.put(uuid, request.getResourceParameters().get("html"));

                return created(createBaseHTTPResponse(request.getHttpVersion()), uuid);
            }

            return badRequest(createBaseHTTPResponse(request.getHttpVersion()));
        }

        // For other resources (initially only html files are supported)

        // No supported resource, 400 Bad Request
        return badRequest(createBaseHTTPResponse(request.getHttpVersion()));

    }

    private HTTPResponse created(HTTPResponse response, String uuid) {
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", "text/html");

        response.setContent(createdPageHTML(uuid));

        return response;
    }

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
