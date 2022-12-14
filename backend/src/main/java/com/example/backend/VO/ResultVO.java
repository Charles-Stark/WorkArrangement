package com.example.backend.VO;

import lombok.Data;

@Data
public class ResultVO<T> {

    private int code;
    private String message;
    private T data;

}
