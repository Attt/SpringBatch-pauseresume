package com.atpex.config.wrapper;

import lombok.Data;

/**
 * 将rest接口返回结果进行包装
 * 
 * 考虑到AOP太heavy，基于AOP的wrapper已经废弃
 * Created by Atpex on 2017/5/15.
 * Updated on 2017/8/5
 */
//@Component
//@Aspect
//@Configuration
public class ResponseWrapper {

//    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
//    public void controllerMethod(){}
//
//
//    @Around("controllerMethod()")
//    public Object around(ProceedingJoinPoint pjp) throws Throwable{
//        Object o = pjp.proceed();
//        return new Response(o);
//    }
    
    
    public static <T> Response<T> of(T o){
        return new Response<>(o);
    }

    public static <T> Response<T> ofSuccess(T o){
        return of(o);
    }

    public static Response ofFail(){
        return new Response<>().failure();
    }

    public static Response ofFail(String message){
        return new Response<>().failure(message);
    }

    public static <T> Response ofFail(T o, String message){
        return of(o).failure(message);
    }

    /**
     * Rest接口的返回包装
     * <p>
     * Created by Atpex on 2017/5/15.
     */
    @Data
    static class Response<DATA> {

        private static final String OK = "ok";
        private static final String ERROR = "error";

        /**
         * meta：完成状态
         * data：接口返回内容
         */
        private Meta meta;
        private DATA data;

        Response() {
            this.meta = new Meta(true, OK);
        }

        Response(DATA data) {
            this.meta = new Meta(true, OK);
            this.data = data;
        }

        Response success() {
            this.meta = new Meta(true, OK);
            return this;
        }

        Response success(DATA data) {
            this.meta = new Meta(true, OK);
            this.data = data;
            return this;
        }

        Response failure() {
            this.meta = new Meta(false, ERROR);
            return this;
        }

        Response failure(String message) {
            this.meta = new Meta(false, message);
            return this;
        }

        @Data
        static class Meta {

            private boolean success;
            private String message;

            Meta(boolean success) {
                this.success = success;
            }

            Meta(boolean success, String message) {
                this.success = success;
                this.message = message;
            }
        }
    }

}
