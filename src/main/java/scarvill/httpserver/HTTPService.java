package scarvill.httpserver;

import scarvill.httpserver.request.HTTPRequest;
import scarvill.httpserver.request.Request;
import scarvill.httpserver.response.HTTPResponse;
import scarvill.httpserver.response.Response;
import scarvill.httpserver.routing.Router;

import java.io.*;
import java.net.Socket;

public class HTTPService implements Serveable {
    private final Router router;
    private final Logger logger;

    public HTTPService(Logger logger, Router router) {
        this.router = router;
        this.logger = logger;
    }

    public Runnable serve(Socket clientSocket) {
        return () -> {
            try (BufferedReader in =
                     new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedOutputStream out =
                     new BufferedOutputStream(clientSocket.getOutputStream())
            ) {
                serveRequest(in, out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void serveRequest(BufferedReader in, BufferedOutputStream out) throws IOException {
        Request request = readRequest(in);
        Response response = router.routeRequest(request);
        sendResponse(out, response);
        logTransaction(request, response);
    }

    private Request readRequest(BufferedReader in) throws IOException {
        String requestLineAndHeaders = readRequestLineAndHeaders(in);
        byte[] body = readBody(in);

        return new HTTPRequest(requestLineAndHeaders, body).parse();
    }

    private String readRequestLineAndHeaders(BufferedReader in) throws IOException {
        String requestLineAndHeaders = "";

        while (!requestLineAndHeaders.contains("\r\n\r\n")) {
            requestLineAndHeaders += in.readLine() + "\r\n";
        }

        return requestLineAndHeaders;
    }

    private byte[] readBody(BufferedReader in) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();

        while (in.ready()) {
            body.write(in.read());
        }

        return body.toByteArray();
    }

    private void sendResponse(OutputStream out, Response response) throws IOException {
        byte[] rawResponse = new HTTPResponse().generate(response);

        out.write(rawResponse, 0, rawResponse.length);
    }

    private void logTransaction(Request request, Response response) {
        logger.logRequest(request);
        logger.logResponse(response);
    }
}
