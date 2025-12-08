package com.nhnacademy.frontserver.common;

public record Page(int pageNumber,int pageSize) {
    public static Page of(int pageNumber,int pageSize){
        return new Page(pageNumber,pageSize);
    }
}
