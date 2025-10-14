package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import es.uvigo.esei.dai.hybridserver.controler.PagesController;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

import es.uvigo.esei.dai.hybridserver.handler.RequestHandler;
import es.uvigo.esei.dai.hybridserver.handler.BaseRequestHandler;
import es.uvigo.esei.dai.hybridserver.handler.GETRequestHandler;
import es.uvigo.esei.dai.hybridserver.handler.POSTRequestHandler;
import es.uvigo.esei.dai.hybridserver.handler.DELETERequestHandler;

/**
 * Class representing a client thread that handles HTTP requests.
 * This class implements Runnable and manages the lifecycle of a client connection,
 * including reading the request, processing it, and sending the response.
 */
public class ClientThread implements Runnable{

    private Socket socket;  // Socket for client connection
    private PagesController controller; // Controller to manage pages

    /**
     * Constructs a ClientThread with the specified socket and pages controller.
     * 
     * @param socket The socket representing the client connection.
     * @param controller The PagesController to manage page operations.
     */
    public ClientThread(Socket socket, PagesController controller) {
        this.socket = socket;
        this.controller = controller;
    }

    /**
     * The main execution method for the client thread.
     * This method handles the entire lifecycle of a client connection,
     * including reading the request, processing it, and sending the response.
     */
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
                
                case DELETE:
                handler = new DELETERequestHandler();
                break;
                
                // Handle other methods (Not implemented for the moment)
                case PUT:
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

            HTTPResponse response = handler.handle(request, this.controller);
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

    /**
     * Sends the HTTP response to the client.
     * 
     * @param response The HTTP response to be sent.
     * @param writer The BufferedWriter to write the response to.
     * @throws IOException If an I/O error occurs.
     */
    private void sendHTTPResponse(HTTPResponse response, BufferedWriter writer) throws IOException {
        response.print(writer);
        writer.flush();
    }

}
