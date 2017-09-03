package com.atpex.schedule;

import org.springframework.batch.core.ItemReadListener;

/**
 * Created by Atpex on 2017/8/31.
 */
public class StepReadListener implements ItemReadListener {
    @Override
    public void beforeRead() {
    }

    @Override
    public void afterRead(Object item) {
//        System.out.println(item);
    }

    @Override
    public void onReadError(Exception ex) {

    }
}
