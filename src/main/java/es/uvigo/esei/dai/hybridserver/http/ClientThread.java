package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class ClientThread implements Runnable{

    private Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // Create input and output streams
        try (InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream()) {

            // Wrap with inputStreamReader to translate bytes to chars, then wrap it with bufferedReader for efficiency
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            try {
            // Parse the HTTP request
            HTTPRequest request = new HTTPRequest(reader);

            // Create a base HTTP response
            HTTPResponse response = createBaseHTTPResponse(request.getHttpVersion());
            // Process the request
            HTTPRequestMethod method = request.getMethod();
            switch (method) {
                case GET:
                // Handle GET request
                response = handleGETRequest(request, response);
                break;

                case POST:
                // Handle POST request
                break;

                case PUT:
                // Handle PUT request
                break;
                
                case DELETE:
                // Handle DELETE request
                break;
                
                // Handle other methods
                case OPTIONS:
                case TRACE:
                case CONNECT:
                case HEAD:
                
                break;
                default:
                // Handle unknown method
                break;
            }

            // Send the response back to the client
            sendHTTPResponse(response, writer);

            } catch (HTTPParseException e) {
            //Handle HTTParseException
            } catch (IOException e) {
            //Handle IOException
            }
            

        } catch (IOException e) {
            System.err.println("Error in client thread: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private HTTPResponse handleGETRequest(HTTPRequest request, HTTPResponse response) throws IOException {
        // Check if it is a request for the root resource
        if (request.getResourceChain() == "/" ) {
            return this.defaultHTTPResponse(response);
        }

        return response;
    }


    private void sendHTTPResponse(HTTPResponse response, BufferedWriter writer) throws IOException {
        response.print(writer);
        writer.flush();
    }

    private HTTPResponse defaultHTTPResponse(HTTPResponse response) {
        response.setStatus(HTTPResponseStatus.S200);
        response.putParameter("Content-Type", "text/html");
        response.setContent("<html><body><h1>Welcome to the Hybrid Server</h1></body></html>");
        return response;
    }

    /**
     * Creates a base HTTPResponse object with default values.
     * The response version is set to match the request version, and the status is set to
     * 200 OK.
     * @param request The HTTPRequest object to base the response on.
     * @return The created HTTPResponse object.
     */
    private HTTPResponse createBaseHTTPResponse(String requestVersion) {
        HTTPResponse response = new HTTPResponse();
        // Set response version to match request version
        response.setVersion(requestVersion);
        // Set default status to 200 OK
        response.setStatus(HTTPResponseStatus.S200);
        return response;
    }
}
