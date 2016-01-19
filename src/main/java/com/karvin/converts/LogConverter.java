package com.karvin.converts;

import com.karvin.model.LoggerObject;

/**
 * Created by karvin on 16/1/19.
 */
public abstract class LogConverter<T> implements Converter<T>{

    private static final String SPLIT_LETTER = "\t";

    private String[] properties;
    private String splitter;

    public String getSplitter() {
        if(this.splitter == null){
            return SPLIT_LETTER;
        }
        return splitter;
    }

    public void setSplitter(String splitter) {
        this.splitter = splitter;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public LoggerObject<T> convert(String line) {
        LoggerObject object = new LoggerObject();
        String[] splits = line.split(this.getSplitter());
        if(splits.length-1 != this.getProperties().length){
            throw new IllegalStateException("format is not correct");
        }
        object.setContent(parse(splits[splits.length - 1]));
        return object;
    }

    public abstract T parse(String s);
}
