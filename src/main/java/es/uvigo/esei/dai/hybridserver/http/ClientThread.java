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

import java.util.Map;
import es.uvigo.esei.dai.hybridserver.handler.RequestHandler;
import es.uvigo.esei.dai.hybridserver.handler.BaseRequestHandler;
import es.uvigo.esei.dai.hybridserver.handler.GETRequestHandler;
import es.uvigo.esei.dai.hybridserver.handler.POSTRequestHandler;
import es.uvigo.esei.dai.hybridserver.handler.DELETERequestHandler;

public class ClientThread implements Runnable{

    private Socket socket;
    private Map<String, String> pages;

    public ClientThread(Socket socket, Map<String, String> pages) {
        this.socket = socket;
        this.pages = pages;
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
            // Process the request
            HTTPRequestMethod method = request.getMethod();
            
            RequestHandler handler = null;
            switch (method) {
                case GET:
                handler = new GETRequestHandler();
                break;

                case POST:
                handler = new POSTRequestHandler();
                break;

                case PUT:
                // Handle PUT request
                break;
                
                case DELETE:
                handler = new DELETERequestHandler();
                break;
                
                // Handle other methods (Not implemented for the moment)
                case OPTIONS:
                case TRACE:
                case CONNECT:
                case HEAD:
                    handler = new BaseRequestHandler();
                break;

                // Handle unknown method
                default:
                    handler = new BaseRequestHandler();
                break;
            }

            HTTPResponse response = handler.handle(request, this.pages);
            // Send the response back to the client
            sendHTTPResponse(response, writer);

            } catch (HTTPParseException e) {
            //Handle HTTParseException
                System.err.println("HTTP Parse Exception: " + e.getMessage());
            } catch (IOException e) {
            //Handle IOException
                System.err.println("IO Exception: " + e.getMessage());
                e.printStackTrace();
            }
            

        } catch (IOException e) {
            System.err.println("Error in client thread: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void sendHTTPResponse(HTTPResponse response, BufferedWriter writer) throws IOException {
        response.print(writer);
        writer.flush();
    }

}
