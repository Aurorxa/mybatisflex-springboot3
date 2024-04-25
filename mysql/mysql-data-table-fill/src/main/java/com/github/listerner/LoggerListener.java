package com.github.listerner;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerListener implements InsertListener, UpdateListener {
    @Override
    public void onInsert(Object entity) {
        log.info("LoggerListener.onInsert.entity ==> {}", entity);
    }

    @Override
    public void onUpdate(Object entity) {
        log.info("LoggerListener.onUpdate.entity ==> {}", entity);
    }
}
