package scarvill.httpserver.routing;

import scarvill.httpserver.html.HtmlPageGenerator;
import scarvill.httpserver.request.Request;
import scarvill.httpserver.response.Response;
import scarvill.httpserver.response.ResponseBuilder;
import scarvill.httpserver.response.Status;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.function.Function;

public class GetDirectoryIndex implements Function<Request, Response> {
    private Path rootDirectory;

    public GetDirectoryIndex(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public Response apply(Request request) {
        Path directoryPath = Paths.get(rootDirectory + request.getURI());
        HashMap<String, String> directoryFileNamesAndPaths =
            mapDirectoryFileNamesToPaths(request.getURI(), getDirectoryContents(directoryPath));
        String indexPage = new HtmlPageGenerator().indexPage(directoryFileNamesAndPaths);

        return new ResponseBuilder()
            .setStatus(Status.OK)
            .setHeader("Content-Length", String.valueOf(indexPage.length()))
            .setBody(indexPage.getBytes())
            .build();
    }

    private DirectoryStream<Path> getDirectoryContents(Path directoryPath) {
        try {
            return Files.newDirectoryStream(directoryPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, String> mapDirectoryFileNamesToPaths(String uri,
                                                                 DirectoryStream<Path> contents) {
        HashMap<String, String> fileNamesAndPaths = new HashMap<>();

        for (Path path : contents) {
            String fileName = path.getFileName().toString();
            fileNamesAndPaths.put(fileName, filePath(uri, fileName));
        }

        return fileNamesAndPaths;
    }

    private String filePath(String uri, String fileName) {
        if (uri.equals("/")) {
            return "/" + fileName;
        } else {
            return uri + "/" + fileName;
        }
    }
}
