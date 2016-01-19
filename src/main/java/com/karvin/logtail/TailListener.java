package com.karvin.logtail;

/**
 * Created by karvin on 16/1/18.
 */
public interface TailListener {

    void handle(String line);

    void handle(Exception e);

    void rotate();

    void fileNotFound();

    void init(Tailer tailer);

}
