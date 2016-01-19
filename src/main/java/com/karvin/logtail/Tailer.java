package com.karvin.logtail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by karvin on 16/1/18.
 */
public class Tailer implements Runnable{

    private File file;
    private int delay;
    private boolean end;
    private boolean run=true;
    private TailListener listener;

    public Tailer(File file,int delay,boolean end,TailListener listener){
        this.file = file;
        this.delay = delay;
        this.end = end;
        this.listener = listener;
        listener.init(this);
    }

    public Tailer(File file,TailListener listener){
        this(file, 1000, true, listener);
    }

    public File getFile(){
        return this.file;
    }

    public static Tailer create(File file,int delay,boolean end,TailListener listener){
        Tailer tailer = new Tailer(file,delay,end,listener);
        Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }

    public void stop(){
        this.run = false;
    }

    public void run() {
        RandomAccessFile accessFile = null;
        try{
            long position = 0;
            long lastTimestamp = 0;
            while (run && accessFile == null) {
                try {
                    accessFile = new RandomAccessFile(this.file, "r");
                }catch (FileNotFoundException e) {
                    listener.fileNotFound();
                }
                if(accessFile == null){
                    Thread.sleep(this.delay);
                }
                position = end ? file.length():0;
                lastTimestamp = System.currentTimeMillis();
                accessFile.seek(position);
            }
            while(run){
                long length = file.length();
                if(length<position){
                    //rotate
                    try {
                        RandomAccessFile old = accessFile;
                        accessFile = new RandomAccessFile(file, "r");
                        position = 0;
                        closeQuietly(old);
                    }catch (FileNotFoundException e){
                        listener.fileNotFound();
                    }
                    continue;
                }else{
                    if(length>position){
                        readLines(accessFile);
                        lastTimestamp = System.currentTimeMillis();
                    }else if(isNewer(file,lastTimestamp)){
                        position = 0;
                        accessFile.seek(position);
                        lastTimestamp = System.currentTimeMillis();
                        this.readLines(accessFile);
                    }
                }
            }
        }catch (Exception e){
            listener.handle(e);
        }finally {
            closeQuietly(accessFile);
        }
    }

    private long readLines(RandomAccessFile reader) throws IOException {
        long pos = reader.getFilePointer();
        String line = readLine(reader);
        while(line != null){
            listener.handle(line);
            line = readLine(reader);
            pos = reader.getFilePointer();
        }
        reader.seek(pos);
        return pos;
    }

    private String readLine(RandomAccessFile reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        boolean seenCR = false;
        while((ch = reader.read()) != -1){
            switch (ch){
                case '\r':
                    seenCR = true;
                    break;
                case  '\n':
                    return sb.toString();
                default:
                    if(seenCR){
                        sb.append('\r');
                    }
                    sb.append((char)ch);
            }
        }
        return null;
    }

    private void closeQuietly(RandomAccessFile accessFile){
        if(accessFile != null){
            try {
                accessFile.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    private boolean isNewer(File file,long lastTimestamp){
        if(file == null){
            throw new IllegalArgumentException("file not exist");
        }
        if(!file.exists()){
            return false;
        }
        return file.lastModified()>lastTimestamp;
    }

}
