package Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection;

import Tracker.Infrastructure.ToyDatabaseServer.Database;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class DatabaseBuilder
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private DatabaseBuilder() {}

    // Builds a DB object from a file
    public static Database buildDatabaseFromFile(String path) throws JsonIOException, JsonSyntaxException, FileNotFoundException
    {
        return gson.fromJson(new JsonReader(new FileReader(path)), Database.class);
    }

    // Builds a DB object from a JSON
    public static Database buildDatabaseFromJSON(String database) throws JsonIOException, JsonSyntaxException
    {
        return gson.fromJson(database, Database.class);
    }
}
