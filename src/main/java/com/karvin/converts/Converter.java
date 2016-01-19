package com.karvin.converts;

import com.karvin.model.LoggerObject;

/**
 * Created by karvin on 16/1/19.
 */
public interface Converter<T> {

    LoggerObject<T> convert(String line);

}
