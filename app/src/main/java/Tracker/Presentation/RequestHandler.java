package Tracker.Presentation;


import Tracker.BusinessLogic.DataDB;
import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponse;
import Tracker.BusinessLogic.Utiles.HttpResponseBuilder;
import Tracker.Infrastructure.DataDBImpl;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.DatabaseConnectionManager;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;
import Tracker.Infrastructure.ToyDatabaseServer.Model.User;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Map;

public class RequestHandler
{
    private final DataDB dataDB;

    private final HttpResponseBuilder successResponse = HttpResponse.builder().withStatus("OK").withStatusCode(200).withBody("Success");
    private final HttpResponseBuilder badRequestResponse = HttpResponse.builder().withStatus("BAD REQUEST").withStatusCode(400).withBody("Request could not be understood");
    private final HttpResponseBuilder serverErrorResponse = HttpResponse.builder().withStatus("INTERNAL SERVER ERROR").withStatusCode(500).withBody("Failed to process request");

    public RequestHandler()
    {
        this.dataDB = new DataDBImpl(DatabaseConnectionManager.getConnection());
    }

    public HttpResponse handleRequest(HttpRequestObject httpRequest)
    {
        String[] requestPath = httpRequest.getPath().split("\\?");
        Map<String, String> requestParameters = null;
        if (requestPath.length > 1)
            requestParameters = Splitter.on('&').trimResults().withKeyValueSeparator('=').split(requestPath[1]);

        if (httpRequest.getHttpMethod().equals("GET")) return handleGet(httpRequest, requestPath, requestParameters);
        else if (httpRequest.getHttpMethod().equals("POST"))
            return handlePost(httpRequest, requestPath, requestParameters);
        else return serverErrorResponse.build();
    }

    private HttpResponse handleGet(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        return switch (requestPath[0])
                {
                    case "/getFile" -> getFile(httpRequest, requestPath, requestParameters);
                    case "/getFiles" -> getFiles(httpRequest, requestPath, requestParameters);
                    default -> badRequestResponse.build();
                };
    }

    private HttpResponse handlePost(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        return switch (requestPath[0])
                {
                    case "/join" -> join(httpRequest, requestPath, requestParameters);
                    case "/removeOwner" -> removeOwner(httpRequest, requestPath, requestParameters);
                    case "/upload" -> upload(httpRequest, requestPath, requestParameters);
                    case "/exit" -> exit(httpRequest, requestPath, requestParameters);
                    default -> badRequestResponse.build();
                };
    }

    private HttpResponse join(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /join endpoint");
        return switch (dataDB.join(httpRequest.getHeaders().getOrDefault("Host", httpRequest.getHeaders().get("host"))))
                {
                    case 0 -> successResponse.build();
                    default -> serverErrorResponse.build();
                };
    }

    private HttpResponse exit(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /exit endpoint");
        return switch (dataDB.exit(httpRequest.getHeaders().getOrDefault("Host", httpRequest.getHeaders().get("host"))))
                {
                    case 0 -> successResponse.build();
                    default -> serverErrorResponse.build();
                };
    }

    private HttpResponse getFiles(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /getFiles endpoint");
        record StrippedFile(String filename, String hash, long size) {}
        ArrayList<StrippedFile> strippedFiles = new ArrayList<>();

        if (dataDB.getFiles() != null)
        {
            dataDB.getFiles().forEach(file -> strippedFiles.add(new StrippedFile(file.filename(), file.hash(), file.size())));
            return successResponse.withBody("{\n\"files\": " + new GsonBuilder().setPrettyPrinting().create().toJson(strippedFiles) + "\n}").build();
        } else return serverErrorResponse.build();
    }

    private HttpResponse getFile(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /getFile endpoint");
        File file = dataDB.getFile(requestParameters.get("hash"));
        if (file != null)
        {
            File strippedFile = new File(file.filename(), file.hash(), file.size(), new ArrayList<User>());
            file.owners().forEach(owner -> strippedFile.owners().add(new User(owner.ipAddress().replaceAll(":.*", ""))));
            return successResponse.withBody("{\n\"peers\": " + new GsonBuilder().setPrettyPrinting().create().toJson(strippedFile.owners()) + "\n}").build();
        } else return serverErrorResponse.build();
    }

    private HttpResponse removeOwner(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /removeOwner endpoint");
        return switch (dataDB.removeOwner(httpRequest.getHeaders().getOrDefault("Host", httpRequest.getHeaders().get("host")), requestParameters.get("hash")))
                {
                    case 0 -> successResponse.build();
                    default -> serverErrorResponse.build();
                };
    }

    private HttpResponse upload(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /upload endpoint");
        ArrayList<LinkedTreeMap> requestData = (ArrayList) new Gson().fromJson(httpRequest.getBody(), LinkedTreeMap.class).get("files");
        ArrayList<File> newFiles = new ArrayList<>();
        requestData.forEach(rawFile -> newFiles.add(new File(rawFile.get("filename").toString(), rawFile.get("hash").toString(), ((Double) Double.parseDouble(rawFile.get("size").toString())).longValue(), new ArrayList<User>())));

        return switch (dataDB.upload(httpRequest.getHeaders().getOrDefault("Host", httpRequest.getHeaders().get("host")), newFiles))
                {
                    case 0 -> successResponse.build();
                    default -> serverErrorResponse.build();
                };
    }
}
