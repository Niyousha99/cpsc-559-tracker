package Tracker.Presentation;


import Tracker.BusinessLogic.DataDB;
import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponse;
import Tracker.BusinessLogic.Utiles.HttpResponseBuilder;
import Tracker.Infrastructure.DataDBImpl;
import Tracker.Infrastructure.Election.ElectionManager;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.DatabaseConnectionManager;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;
import Tracker.Infrastructure.ToyDatabaseServer.Model.User;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler
{
    private final DataDB dataDB;

    private final HttpResponseBuilder successResponse = HttpResponse.builder().withStatus("OK").withStatusCode(200).withBody("Success");
    private final HttpResponseBuilder redirectResponse = HttpResponse.builder().withStatus("TEMPORARY REDIRECT").withStatusCode(307);
    private final HttpResponseBuilder badRequestResponse = HttpResponse.builder().withStatus("BAD REQUEST").withStatusCode(400).withBody("Request could not be understood");
    private final HttpResponseBuilder serverErrorResponse = HttpResponse.builder().withStatus("INTERNAL SERVER ERROR").withStatusCode(500).withBody("Failed to process request");

    public RequestHandler()
    {
        this.dataDB = new DataDBImpl(DatabaseConnectionManager.getConnection());
    }

    // Calls the GET/POST method depending on the request, otherwise informs it that it send a bad requests
    public HttpResponse handleRequest(HttpRequestObject httpRequest)
    {
        String[] requestPath = httpRequest.path().split("\\?");
        Map<String, String> requestParameters = null;
        if (requestPath.length > 1)
            requestParameters = Splitter.on('&').trimResults().withKeyValueSeparator('=').split(requestPath[1]);

        if (httpRequest.httpMethod().equals("GET")) return handleGet(httpRequest, requestPath, requestParameters);
        else if (httpRequest.httpMethod().equals("POST"))
            return handlePost(httpRequest, requestPath, requestParameters);
        else return serverErrorResponse.build();
    }

    // Handles all GET requests
    private HttpResponse handleGet(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        return switch (requestPath[0])
        {
            case "/getDB" -> getDB(httpRequest, requestPath, requestParameters);
            case "/getFile" -> getFile(httpRequest, requestPath, requestParameters);
            case "/getFiles" -> getFiles(httpRequest, requestPath, requestParameters);
            default -> badRequestResponse.build();
        };
    }

    // Handles all POST requests
    private HttpResponse handlePost(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        if (ElectionManager.getLeader() == null) return serverErrorResponse.build();
        else if (ElectionManager.getLeader().equalsIgnoreCase("self"))
        {
            HttpResponse response = switch (requestPath[0])
            {
                case "/removeOwner" -> removeOwner(httpRequest, requestPath, requestParameters);
                case "/upload" -> upload(httpRequest, requestPath, requestParameters);
                case "/exit" -> exit(httpRequest, requestPath, requestParameters);
                default -> badRequestResponse.build();
            };
            ElectionManager.syncFollowers();
            return response;
        } else
        {
            System.out.println("Redirecting " + requestPath[0] + " endpoint call");
            HashMap<String, String> redirectHeaders = ((HashMap<String, String>) httpRequest.headers());
            redirectHeaders.put("Location", "http://" + ElectionManager.getLeader() + ":" + ElectionManager.getPort() + requestPath[0]);
            return redirectResponse.withHeaders(redirectHeaders).withBody(ElectionManager.getLeader()).build();
        }
    }

    // Get a serialized version of the local DB
    private HttpResponse getDB(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /getDB endpoint");
        return successResponse.withBody(new GsonBuilder().setPrettyPrinting().create().toJson(dataDB.getDB())).build();
    }

    // Removes an IP from being the host of any file
    private HttpResponse exit(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /exit endpoint");
        LinkedTreeMap requestData = new Gson().fromJson(httpRequest.body(), LinkedTreeMap.class);
        return switch (dataDB.exit((String) requestData.get("ip")))
        {
            case 0 -> successResponse.build();
            default -> serverErrorResponse.build();
        };
    }

    // Get a list of all the available files in the DB
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

    // Get a list of peers for a specific file
    private HttpResponse getFile(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /getFile endpoint");
        File file = dataDB.getFile(requestParameters.get("hash"));
        if (file != null)
        {
            File strippedFile = new File(file.filename(), file.hash(), file.size(), new ArrayList<>());
            file.owners().forEach(owner -> strippedFile.owners().add(new User(owner.ipAddress().replaceAll(":.*", ""))));
            return successResponse.withBody("{\n\"peers\": " + new GsonBuilder().setPrettyPrinting().create().toJson(strippedFile.owners()) + "\n}").build();
        } else return serverErrorResponse.build();
    }

    // Remove IP from being the host of a specific file
    private HttpResponse removeOwner(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /removeOwner endpoint");
        LinkedTreeMap requestData = new Gson().fromJson(httpRequest.body(), LinkedTreeMap.class);
        return switch (dataDB.removeOwner((String) requestData.get("ip"), (String) requestData.get("hash")))
        {
            case 0 -> successResponse.build();
            default -> serverErrorResponse.build();
        };
    }

    // Adds the IP to be the host of all the files provided
    private HttpResponse upload(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        System.out.println("Called /upload endpoint");
        LinkedTreeMap requestData = new Gson().fromJson(httpRequest.body(), LinkedTreeMap.class);
        ArrayList<LinkedTreeMap> filesData = (ArrayList) requestData.get("files");
        ArrayList<File> newFiles = new ArrayList<>();
        filesData.forEach(rawFile -> newFiles.add(new File(rawFile.get("filename").toString(), rawFile.get("hash").toString(), ((Double) Double.parseDouble(rawFile.get("size").toString())).longValue(), new ArrayList<>())));

        return switch (dataDB.upload((String) requestData.get("ip"), newFiles))
        {
            case 0 -> successResponse.build();
            default -> serverErrorResponse.build();
        };
    }
}
