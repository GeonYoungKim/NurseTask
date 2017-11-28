package skuniv.ac.kr.nursetask.Core.domain;

/**
 * Created by gunyoungkim on 2017-11-02.
 */
public class SharedMemory {
    private static SharedMemory sharedMemory = null;
    public static synchronized SharedMemory getInstance() {
        if (sharedMemory == null) {
            sharedMemory = new SharedMemory();
        }
        return sharedMemory;
    }
    private String resultString;

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

}