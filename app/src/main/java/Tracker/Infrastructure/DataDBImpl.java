package Tracker.Infrastructure;

import Tracker.Infrastructure.Database.DatabaseConnection.DatabaseConnection;
import Tracker.BusinessLogic.DataDB;;

public class DataDBImpl implements DataDB {
    private final DatabaseConnection connection;

    public DataDBImpl(DatabaseConnection connection) {
        this.connection = connection;
    }

    
    
}
