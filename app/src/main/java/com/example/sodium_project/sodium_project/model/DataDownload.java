package com.example.sodium_project.sodium_project.model;

/**
 * Class Name: DataDownload
 *
 * Description: A class that manages the states of the data downloading process
 *              (checks whether the download is successful, or it is still in progress).
 *
 */

public class DataDownload {
    private boolean checkedFromServer;
    private boolean needToDownloadNewDataFromServer;

    private boolean success;
    private boolean executing;
    private static DataDownload instance;

    // Default Constructor
    private DataDownload() {
        checkedFromServer = false;
        needToDownloadNewDataFromServer = false;
        success = false;
        executing = false;
    }

    public static DataDownload getInstance() {
        if (instance == null) {
            instance = new DataDownload();
        }
        return instance;
    }

    public boolean isSuccessful() {
        return success;
    }

    public void setSuccess(boolean value) {
        success = value;
    }

    public boolean isCheckedFromServer() {
        return checkedFromServer;
    }

    public void setCheckedFromServer(boolean checkedFromServer) {
        this.checkedFromServer = checkedFromServer;
    }

    public boolean getExecuting() {
        return executing;
    }

    public void setExecuting(boolean value) {
        executing = value;
    }

    public boolean isNeedToDownloadNewDataFromServer() {
        return needToDownloadNewDataFromServer;
    }

    public void setNeedToDownloadNewDataFromServer(boolean needToDownloadNewDataFromServer) {
        this.needToDownloadNewDataFromServer = needToDownloadNewDataFromServer;
    }
}
