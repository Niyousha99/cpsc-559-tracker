package Tracker.Infrastructure.Database.DatabaseConnection;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import Tracker.Infrastructure.Database.DatabaseEngine;
import Tracker.Infrastructure.Database.DatabaseConnection.Model.Database;

public class DatabaseConnectionManager  {
    private DatabaseConnectionManager() {}

    private static Tracker.Infrastructure.Database.Database database;

    public static void initialize(String path) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        Database databaseModel = DatabaseBuilder.buildDatabase(path);
        ArrayList<Tracker.Infrastructure.Database.Model.User> users  = new ArrayList<>();
        ArrayList<Tracker.Infrastructure.Database.Model.File> files  = new ArrayList<>();

        databaseModel.getUsers().forEach(
            (user) -> {
                users.add(new Tracker.Infrastructure.Database.Model.User(user.getIpAddress()));
            }
        );

        databaseModel.getFiles().forEach(
            (file) -> {
                files.add(new Tracker.Infrastructure.Database.Model.File(file.getFilename(), file.getHash(), file.getOwners()));
            }
        );
        
        database = new Tracker.Infrastructure.Database.Database(users, files);
        DatabaseEngine.setDatabase(database);      
    }

    public static DatabaseConnection getConnection() {
        return DatabaseConnection.getConnection();
    }



    
    
    







}
