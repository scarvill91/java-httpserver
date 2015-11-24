package scarvill.httpserver;

import org.junit.Test;
import scarvill.httpserver.constants.StatusTwo;

import static org.junit.Assert.*;

public class ResponseTest {
    @Test
    public void testHasStatus() {
        Response response = new Response.Builder().setStatus(StatusTwo.OK).build();

        assertEquals(StatusTwo.OK, response.getStatus());
    }

    @Test
    public void testHasHeaders() {
        String[] headers = new String[]{"Foo: a header", "Bar: another header"};
        Response response = new Response.Builder().setHeaders(headers).build();

        for (String header : headers) {
            assertTrue(response.getHeaders().contains(header));
        }
    }

    @Test
    public void testHasBody() {
        Response response = new Response.Builder().setBody("body").build();

        assertEquals("body", response.getBody());
    }
}