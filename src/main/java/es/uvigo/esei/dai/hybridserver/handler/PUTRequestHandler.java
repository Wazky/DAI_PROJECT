package es.uvigo.esei.dai.hybridserver.handler;

import es.uvigo.esei.dai.hybridserver.model.dao.DAOException;
import es.uvigo.esei.dai.hybridserver.model.entity.Page;
import es.uvigo.esei.dai.hybridserver.PageNotFoundException;
import es.uvigo.esei.dai.hybridserver.controler.PagesController;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class PUTRequestHandler extends BaseRequestHandler {
    

    public PUTRequestHandler() {}

    @Override
    public HTTPResponse handle(HTTPRequest request, PagesController controller) {
        // Check if it is a request for a html resource
        if (request.getResourceName().equals("html")) {

            // Check for uuid parameter
            if (request.getResourceParameters().containsKey("uuid")) {

                // Obtain the uuid
                String uuid = request.getResourceParameters().get("uuid");

                try {
                    // Check if uuid exists
                    if (controller.exists(uuid)) {
                        // Check content validity
                        if (isContentValid(request)) {

                            // Update the page
                            controller.update(new Page(uuid, request.getResourceParameters().get("html")));
                            
                            // Return OK response with updated page link
                            return updated(createBaseHTTPResponse(request.getHttpVersion()), uuid);
                        }

                        // Invalid content
                        return badRequest(createBaseHTTPResponse(request.getHttpVersion()));

                    }

                    // Uuid does not exist
                    return notFound(createBaseHTTPResponse(request.getHttpVersion()));
                
                } catch (PageNotFoundException e) {
                    // Return not found response
                    return notFound(createBaseHTTPResponse(request.getHttpVersion()));
                } catch (DAOException e) {
                    // Return internal server error response
                    return internalServerError(createBaseHTTPResponse(request.getHttpVersion()));
                }
                
            }

            // No uuid parameter provided
            return badRequest(createBaseHTTPResponse(request.getHttpVersion()));
        }

        // For others resources (just html supported for now)

        //No supported resource
        return badRequest(createBaseHTTPResponse(request.getHttpVersion()));
    }

    private HTTPResponse updated(HTTPResponse response, String uuid) {
        
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", "text/html");

        response.setContent(updatedPageHTML(uuid));

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
     * Generates the HTML content for a updated page response.
     * 
     * @param uuid The UUID of the updated page.
     * @return The HTML content for the response.
     */
    private String updatedPageHTML(String uuid) {
        return 
        "<html>" +
            "<head><title>200 OK</title></head>" +
            "<body>" +
                "<h1>Page Updated</h1>" +
                "<h2>The page <a href=\"html?uuid=" + uuid + "\">" + uuid + "</a> has been successfully updated.</h2>" +
            "</body>" +
        "</html>";

    }

}
