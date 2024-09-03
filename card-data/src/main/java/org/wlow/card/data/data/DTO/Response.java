package org.wlow.card.data.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private int code;
    private String message;
    private Object data;

    public static Response ok() {
        return new Response(200, "ok", null);
    }

    public static Response success(String message, Object data) {
        return new Response(200, message, data);
    }

    public static Response success(Object data) {
        return new Response(200, "ok", data);
    }

    public static Response failure(int code, String message) {
        return new Response(code, message, null);
    }

    public static Response error(String message) {
        return new Response(500, message, null);
    }

    public static Response forbidden(String message) {
        return new Response(403, message, null);
    }

    public static Response forbidden() {
        return new Response(403, "无权限", null);
    }
}
