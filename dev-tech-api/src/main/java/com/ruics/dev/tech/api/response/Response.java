package com.ruics.dev.tech.api.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private String code;

    private String message;

    private Object data;

    public static Response success(Object data) {
        return new Response("0000", "操作成功", data);
    }

    public static Response error(String code, String message) {
        return new Response(code, message, null);
    }
}
