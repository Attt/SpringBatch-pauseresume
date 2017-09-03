package com.atpex.schedule;

import org.springframework.batch.core.SkipListener;

/**
 * Created by Atpex on 2017/9/3.
 */
public class StepSkipListener implements SkipListener {
    @Override
    public void onSkipInRead(Throwable t) {

    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {

    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {

    }
}
