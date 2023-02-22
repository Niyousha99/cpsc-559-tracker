package Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.Model.Database;

public class DatabaseBuilder {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private DatabaseBuilder() {}

    public static Database buildDatabase(String path) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        return gson.fromJson(new JsonReader(new FileReader(path)), Database.class);
    }

}
