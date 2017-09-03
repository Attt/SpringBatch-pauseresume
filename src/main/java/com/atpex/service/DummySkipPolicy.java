package com.atpex.service;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

/**
 * Created by Atpex on 2017/9/3.
 */
public class DummySkipPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        if(t instanceof DummyException)
            return true;
        return false;
    }
}
