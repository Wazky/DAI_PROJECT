package es.uvigo.esei.dai.hybridserver.handler;

import java.util.Map;
import java.util.UUID;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class POSTRequestHandler extends BaseRequestHandler {
    
    @Override
    public HTTPResponse handle(HTTPRequest request, Map<String, String> pages) {
        HTTPResponse r = null;
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
                pages.put(uuid, request.getContent());

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
        response.putParameter("Content-Type", "text/plain");
        response.setContent("200 OK: (Should be 201 created) The resource has been created with UUID: " + uuid);

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

        // Check if content type is text/html
        String contentType = request.getHeaderParameters().get("Content-Type");
        if (contentType == null) {
            return false;
        }

        return true;
    }

}
