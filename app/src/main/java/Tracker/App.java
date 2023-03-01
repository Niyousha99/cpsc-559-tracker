/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Tracker;

import Tracker.Infrastructure.HttpServer.Server;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.DatabaseConnectionManager;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class App
{
    private static String databasePath;

    public String getGreeting()
    {
        return "Hello World!";
    }

    public static void main(String[] args)
    {
        HashMap<String, String> params = parseCommandLine(args);
        databasePath = params.getOrDefault("-d", FileSystems.getDefault().getPath("").toAbsolutePath() + "/Database.txt");
        String serverIP = params.getOrDefault("-ip", null);
        int serverPort = Integer.parseInt(params.getOrDefault("-p", "3001")); // server port number

        Runtime.getRuntime().addShutdownHook(new Thread(() -> DatabaseConnectionManager.shutdown(databasePath)));

        System.out.println("Starting the server on port " + serverPort);
        Server server = new Server(serverIP, serverPort);
        // initialize database
        try
        {
            if (Files.exists(Path.of(databasePath)))
                DatabaseConnectionManager.initialize(databasePath, Boolean.parseBoolean(params.getOrDefault("-r", String.valueOf(false))));
            else
            {
                Files.createFile(Path.of(databasePath));
                DatabaseConnectionManager.initialize(databasePath, true);
            }
        } catch (JsonIOException | JsonSyntaxException | SecurityException | IOException e)
        {
            System.out.println("Can't connect to database");
            e.printStackTrace();
        }
        server.listen();
    }

    // parse command line arguments
    private static HashMap<String, String> parseCommandLine(String[] args)
    {
        HashMap<String, String> params = new HashMap<String, String>();

        int i = 0;
        while ((i + 1) < args.length)
        {
            params.put(args[i], args[i + 1]);
            i += 2;
        }

        return params;
    }
}
