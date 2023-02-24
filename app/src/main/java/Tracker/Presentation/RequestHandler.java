package Tracker.Presentation;


import Tracker.BusinessLogic.DataDB;
import Tracker.BusinessLogic.HttpRequestObject;
import Tracker.BusinessLogic.HttpResponse;
import Tracker.Infrastructure.DataDBImpl;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.DatabaseConnectionManager;
import Tracker.Infrastructure.ToyDatabaseServer.Model.File;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class RequestHandler
{
    private final DataDB dataDB;

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
        return handleError(httpRequest, requestPath, requestParameters);
    }

    private HttpResponse handleGet(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        return switch (requestPath[0])
                {
                    case "/getFile" -> getFile(httpRequest, requestPath, requestParameters);
                    case "/getFiles" -> getFiles(httpRequest, requestPath, requestParameters);
                    default -> handleError(httpRequest, requestPath, requestParameters);
                };
    }

    private HttpResponse join(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        dataDB.join(httpRequest.getHeaders().get("Host"));
        return HttpResponse.builder().withStatus("OK").withStatusCode(200).withBody("Success").build();
    }

    private HttpResponse upload(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        ArrayList<LinkedTreeMap> requestData = (ArrayList) new Gson().fromJson(httpRequest.getBody(), LinkedTreeMap.class).get("files");
        ArrayList<File> newFiles = new ArrayList<>();
        requestData.forEach(rawFile -> newFiles.add(new File(rawFile.get("filename").toString(), rawFile.get("hash").toString(), ((Double) Double.parseDouble(rawFile.get("size").toString())).longValue(), new ArrayList<String>(Arrays.asList(httpRequest.getHeaders().get("Host"))))));
        dataDB.upload(newFiles, httpRequest.getHeaders().get("Host"));
        return HttpResponse.builder().withStatus("OK").withStatusCode(200).withBody("Success").build();
    }

    private HttpResponse getFile(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        return HttpResponse.builder().withStatus("OK").withStatusCode(200).withBody("{\n\"peers\": " + new GsonBuilder().setPrettyPrinting().create().toJson(dataDB.getFile(requestParameters.get("filename")).getOwners()) + "\n}").build();
    }

    private HttpResponse getFiles(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        return HttpResponse.builder().withStatus("OK").withStatusCode(200).withBody("{\n\"files\": " + new GsonBuilder().setPrettyPrinting().create().toJson(dataDB.getFiles()) + "\n}").build();
    }

    private HttpResponse exit(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        dataDB.exit(httpRequest.getHeaders().get("Host"));
        return HttpResponse.builder().withStatus("OK").withStatusCode(200).withBody("Success").build();
    }

    private HttpResponse handlePost(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        return switch (requestPath[0])
                {
                    case "/join" -> join(httpRequest, requestPath, requestParameters);
                    case "/upload" -> upload(httpRequest, requestPath, requestParameters);
                    case "/exit" -> exit(httpRequest, requestPath, requestParameters);
                    default -> handleError(httpRequest, requestPath, requestParameters);
                };
    }

    private HttpResponse handleError(HttpRequestObject httpRequest, String[] requestPath, Map<String, String> requestParameters)
    {
        return HttpResponse.builder().withStatus("BAD REQUEST").withStatusCode(400).withBody("Request could not be processed").build();
    }
}
