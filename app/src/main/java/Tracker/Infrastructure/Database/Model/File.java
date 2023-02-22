package Tracker.Infrastructure.Database.Model;

import java.util.ArrayList;

public class File {
    private final String filename;
    private final String hash;
    private ArrayList<String> owners;

    public File(String filename, String hash, ArrayList<String> owners) {
        this.filename = filename;
        this.hash = hash;
        this.owners = owners;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getHash() {
        return this.hash;
    }

    public ArrayList<String> getOwners() {
        return this.owners;
    }


    public void setOwners(ArrayList<String> owners) {
        this.owners = owners;
    }
}