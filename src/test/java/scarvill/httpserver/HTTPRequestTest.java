package scarvill.httpserver;

import org.junit.Test;

import static org.junit.Assert.*;
import static scarvill.httpserver.constants.Method.*;

public class HTTPRequestTest {
    @Test
    public void testParsesRawHTTPRequest() {
        Request request = new HTTPRequest("GET /uri HTTP/1.1").parse();

        assertEquals(GET, request.getMethod());
    }

    @Test
    public void testParsesInvalidRawHTTPRequestMethod() {
        Request request = new HTTPRequest("FOO /uri HTTP/1.1").parse();

        assertEquals(NULL_METHOD, request.getMethod());
    }

    @Test
    public void testParsesRawHTTPRequestURI() {
        Request request = new HTTPRequest("GET /uri HTTP/1.1").parse();

        assertEquals("/uri", request.getURI());
    }

    @Test
    public void testParsesRawHTTPRequestWithNoBody() {
        Request request = new HTTPRequest("GET /uri HTTP/1.1").parse();

        assertEquals("", request.getBody());
    }

    @Test
    public void testParsesRawHTTPRequestWithBody() {
        Request request = new HTTPRequest("GET /uri HTTP/1.1\r\n\r\nbody").parse();

        assertEquals("body", request.getBody());
    }
}