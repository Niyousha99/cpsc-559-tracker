package Tracker.Infrastructure.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import Tracker.Infrastructure.Database.Model.File;
import Tracker.Infrastructure.Database.Model.User;
import Tracker.Infrastructure.Utils.Result;

public final class DatabaseEngine {
    private static final HashMap<String, HashMap<String, Boolean>> USER_FILES = new HashMap<String, HashMap<String, Boolean>>();
    private static final HashMap<String, HashMap<String, Boolean>> FILE_OWNERS = new HashMap<String, HashMap<String, Boolean>>();
    private static final HashMap<String, User> USERS = new HashMap<String, User>();
    private static final HashMap<String, File> FILES = new HashMap<String, File>();

    private static Database database;

    public static synchronized void setDatabase(Database db) {
        database = db;
        initialize();
    }

    private static synchronized void initialize() {
        database.getUsers().stream().forEach(
            (user) -> {
                USERS.put(user.getIpAddress(), user);
                USER_FILES.put(user.getIpAddress(), new HashMap<String, Boolean>());
            }
        );

        database.getFiles().stream().forEach(
            (file) -> {
                FILES.put(file.getFilename(), file);
                FILE_OWNERS.put(file.getFilename(), new HashMap<String, Boolean>());

                file.getOwners().forEach(
                    (ipAddress) -> {
                        FILE_OWNERS.get(file.getFilename()).put(ipAddress, true);
                        USER_FILES.get(ipAddress).put(file.getFilename(), true);
                    }
                );
            }
        );
    }

    public static synchronized Result<ArrayList> getObject(String objectType, HashMap<String, String> kv) {
        objectType = new String(objectType);
        if (objectType.equals("user")) {
            if (kv.containsKey("ipAddress")) {
                User user = USERS.get(kv.get("ipAddress"));
                if (user != null) {
                    user = user.deepClone();
                }
                return Result.success(new ArrayList(Arrays.asList(user)));
            }

            return Result.fail();
        }
        return Result.fail();

    }

    public static synchronized int addNewUser(String ipAddress) {
        
        ipAddress = new String(ipAddress);

        if (USERS.get(ipAddress) != null) {
            return -1;
        }

        User user = new User(ipAddress);
        database.getUsers().add(user);
        USERS.put(user.getIpAddress(), user);
        USER_FILES.put(user.getIpAddress(), new HashMap<String, Boolean>());
        return 0;
    }

    public static synchronized int addNewFile(String filename, String hash, ArrayList<String> owners) {
        filename= new String(filename);
        if (FILES.get(filename) != null) {
            return -1;
        }

        owners = new ArrayList<String>(
            owners.stream().map(
                (ipAddress) -> {
                return new String(ipAddress);}
            ).collect(Collectors.toList())
        );

        boolean allValidOwners = owners.stream().anyMatch(
            (ipAddress) -> {
                return USERS.getOrDefault(ipAddress, null) != null;
            }
        );

        if (!allValidOwners) {
            return -1;
        }

        File file = new File(filename, hash, owners);
        database.getFiles().add(file);
        FILES.put(file.getFilename(), file);
        FILE_OWNERS.put(file.getFilename(), new HashMap<String, Boolean>());
        
        file.getOwners().forEach(
            (ipAddress) -> {
                FILE_OWNERS.get(file.getFilename()).put(ipAddress, true);
            }
        );

        return 0;
    }

    public static synchronized int removeFileOwners(String requestedFilename, ArrayList<String> formerOwners) {
        String filename = new String(requestedFilename);
        if (FILES.get(filename) == null) {
            return -1;
        }

        formerOwners = new ArrayList<String>(
            formerOwners.stream().map(
                (ipAddress) -> {
                return new String(ipAddress);}
            ).collect(Collectors.toList())
        );

        boolean everyOwnerExistsAndOwnsFile = formerOwners.stream().allMatch(
            (ipAddress) -> {
                return (USERS.get(ipAddress) != null) && (
                    USER_FILES.get(ipAddress).getOrDefault(filename, false)
                );
            }
        );

        if (!everyOwnerExistsAndOwnsFile) {
            return -1;
        }

        formerOwners.forEach(
            (ipAddress) -> {
                FILE_OWNERS.get(filename).put(ipAddress, false);
                USER_FILES.get(ipAddress).put(filename, false);
            }
        ); 

       FILES.get(filename).setOwners(new ArrayList<String>(FILES.get(filename).getOwners().stream().filter(
            (ipAddress) -> {
                return USER_FILES.get(ipAddress).getOrDefault(filename, false);
            }
        ).collect(Collectors.toList())));

        return 0;
    }





}