package com.atpex.schedule;

/**
 *
 * Created by Atpex on 2017/7/1.
 */
@Retry(exceptions = Exception.class,method = "nice")
public class Test {


    public void nice(){

    }
}
