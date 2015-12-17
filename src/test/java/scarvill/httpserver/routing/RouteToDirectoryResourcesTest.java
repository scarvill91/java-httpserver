package scarvill.httpserver.routing;

import org.junit.Test;
import scarvill.httpserver.request.Method;
import scarvill.httpserver.request.Request;
import scarvill.httpserver.request.RequestBuilder;
import scarvill.httpserver.response.Response;
import scarvill.httpserver.response.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RouteToDirectoryResourcesTest {

    @Test
    public void testReturnsFileInRootDirectory() throws IOException {
        String fileContents = "file contents";
        Path directory = Files.createTempDirectory("directory");
        Path file = createTempFileWithContent(directory, fileContents.getBytes());

        RouteToDirectoryResources router = new RouteToDirectoryResources(directory);
        Request request = new RequestBuilder()
            .setMethod(Method.GET)
            .setURI("/" + file.getFileName())
            .build();

        Response response = router.apply(request);

        assertEquals(fileContents, new String(response.getBody()));

        deleteFiles(Arrays.asList(file, directory));
    }

    @Test
    public void testReturnsFileInNestedDirectory() throws IOException {
        String fileContents = "file contents";
        Path directory = Files.createTempDirectory("directory");
        Path subdirectory = Files.createTempDirectory(directory, "subdirectory");
        Path file = createTempFileWithContent(subdirectory, fileContents.getBytes());

        RouteToDirectoryResources router = new RouteToDirectoryResources(directory);
        Request request = new RequestBuilder()
            .setMethod(Method.GET)
            .setURI("/" + subdirectory.getFileName() + "/" + file.getFileName())
            .build();

        Response response = router.apply(request);

        assertEquals(fileContents, new String(response.getBody()));

        deleteFiles(Arrays.asList(file, subdirectory, directory));
    }

    @Test
    public void testReturnsRootDirectoryContents() throws IOException {
        Path directory = Files.createTempDirectory("directory");
        Path file = createTempFileWithContent(directory, "".getBytes());

        RouteToDirectoryResources router = new RouteToDirectoryResources(directory);
        Request request = new RequestBuilder()
            .setMethod(Method.GET)
            .setURI("/")
            .build();

        Response response = router.apply(request);

        assertTrue(new String(response.getBody()).contains(file.getFileName().toString()));

        deleteFiles(Arrays.asList(file, directory));
    }

    @Test
    public void testReturnsNestedDirectoryContents() throws IOException {
        Path directory = Files.createTempDirectory("directory");
        Path subdirectory = Files.createTempDirectory(directory, "subdirectory");
        Path file = createTempFileWithContent(subdirectory, "".getBytes());

        RouteToDirectoryResources router = new RouteToDirectoryResources(directory);
        Request request = new RequestBuilder()
            .setMethod(Method.GET)
            .setURI("/" + subdirectory.getFileName())
            .build();

        Response response = router.apply(request);

        assertTrue(new String(response.getBody()).contains(file.getFileName().toString()));

        deleteFiles(Arrays.asList(file, subdirectory, directory));
    }

    @Test
    public void testRoutesOptionsRequestsForExtantResources() throws IOException {
        Path directory = Files.createTempDirectory("dir");
        Path file = createTempFileWithContent(directory, "".getBytes());

        RouteToDirectoryResources router = new RouteToDirectoryResources(directory);
        Request request = new RequestBuilder()
            .setMethod(Method.OPTIONS)
            .setURI("/" + file.getFileName())
            .build();

        Response response = router.apply(request);

        assertEquals(Status.OK, response.getStatus());
        assertTrue(response.getHeaders().get("Allow").contains(Method.GET.toString()));
        assertTrue(response.getHeaders().get("Allow").contains(Method.OPTIONS.toString()));

        deleteFiles(Arrays.asList(file, directory));
    }

    @Test
    public void testReturnsMethodNotAllowedForNonGetOrOptionsRequests() throws IOException {
        Path directory = Files.createTempDirectory("dir");
        Path file = createTempFileWithContent(directory, "".getBytes());

        RouteToDirectoryResources router = new RouteToDirectoryResources(directory);
        Request request = new RequestBuilder()
            .setMethod(Method.POST)
            .setURI("/" + file.getFileName())
            .build();

        Response response = router.apply(request);

        assertEquals(Status.METHOD_NOT_ALLOWED, response.getStatus());

        deleteFiles(Arrays.asList(file, directory));
    }

    @Test
    public void testReturns404NotFoundWhenNoRootDirectoryIsSet() {
        RouteToDirectoryResources router = new RouteToDirectoryResources();
        Request request = new RequestBuilder().build();

        Response response = router.apply(request);

        assertEquals(Status.NOT_FOUND, response.getStatus());
    }

    private Path createTempFileWithContent(Path dir, byte[] content) throws IOException {
        Path file = Files.createTempFile(dir, "temp", "");
        Files.write(file, content);

        return file;
    }

    private void deleteFiles(List<Path> paths) throws IOException {
        for (Path path : paths) {
            Files.delete(path);
        }
    }
}