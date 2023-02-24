package Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import Tracker.Infrastructure.ToyDatabaseServer.DatabaseEngine;
import Tracker.Infrastructure.ToyDatabaseServer.DatabaseConnection.Model.Database;

public class DatabaseConnectionManager  {
    private DatabaseConnectionManager() {}

    private static Tracker.Infrastructure.ToyDatabaseServer.Database database;

    public static void initialize(String path) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        Database databaseModel = DatabaseBuilder.buildDatabase(path);
        ArrayList<Tracker.Infrastructure.ToyDatabaseServer.Model.User> users  = new ArrayList<>();
        ArrayList<Tracker.Infrastructure.ToyDatabaseServer.Model.File> files  = new ArrayList<>();

        databaseModel.getUsers().forEach(
            (user) -> {
                users.add(new Tracker.Infrastructure.ToyDatabaseServer.Model.User(user.getIpAddress()));
            }
        );

        databaseModel.getFiles().forEach(
            (file) -> {
                files.add(new Tracker.Infrastructure.ToyDatabaseServer.Model.File(file.getFilename(), file.getHash(), file.getSize(), file.getOwners()));
            }
        );

        database = new Tracker.Infrastructure.ToyDatabaseServer.Database(users, files);
        DatabaseEngine.setDatabase(database);
    }

    public static void shutdown(String path)
    {
        try
        {
            FileWriter myWriter = new FileWriter(path.substring(0, path.length() - 4) + " Modified.txt");
            myWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(database));
            myWriter.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static DatabaseConnection getConnection() {
        return DatabaseConnection.getConnection();
    }













}
