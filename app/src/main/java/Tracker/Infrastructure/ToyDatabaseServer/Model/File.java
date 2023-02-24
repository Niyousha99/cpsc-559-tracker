package Tracker.Infrastructure.ToyDatabaseServer.Model;

import java.util.ArrayList;

public class File {
    private final String filename;
    private final String hash;
    private final long size;
    private ArrayList<String> owners;

    public File(String filename, String hash, long size, ArrayList<String> owners) {
        this.filename = filename;
        this.hash = hash;
        this.size = size;
        this.owners = owners;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getHash() {
        return this.hash;
    }

    public long getSize() {
        return this.size;
    }

    public ArrayList<String> getOwners() {
        return this.owners;
    }


    public void setOwners(ArrayList<String> owners) {
        this.owners = owners;
    }
}
