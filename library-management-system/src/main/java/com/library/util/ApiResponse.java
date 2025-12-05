package com.library.util;

/**
 * 统一API响应格式
 */
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int code;

    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setCode(200);
        response.setMessage("操作成功");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    // 失败响应
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(500);
        response.setMessage(message);
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    // 参数错误
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    // 未找到
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message);
    }

    // 未授权
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(401, message);
    }

    // 禁止访问
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(403, message);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}