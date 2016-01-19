package com.karvin.logtail;

import java.io.*;

/**
 * Created by karvin on 16/1/19.
 */
public class DefaultTailListener implements TailListener {

    private Tailer tailer;

    public void handle(String line) {
        System.out.println(line);
    }

    public void handle(Exception e) {
        System.out.println("exception ");
        e.printStackTrace();
    }

    public void rotate() {
        System.out.println("rotating");
    }

    public void fileNotFound() {
        System.out.println("file not found");
    }

    public void init(Tailer tailer) {
        this.tailer = tailer;
    }

    public static void main(String[] args){
        TailListener listener = new DefaultTailListener();
        File file = new File("/Users/karvin/tmp.sql");
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //System.out.println(reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Tailer tailer = Tailer.create(file,1000,false,listener);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
