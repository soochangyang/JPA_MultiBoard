package scyang.mutilboard.global.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static scyang.mutilboard.global.common.MessageUtil.*;


public class ApiResponse<T> extends ResponseEntity<ApiResponse.Payload<T>> {

    @Getter
    @AllArgsConstructor
    public static class Payload<T>{
        private boolean success;
        private T data;
        private String message;
    }


    protected ApiResponse(Payload<T> body, HttpStatus status) {
        super(body, status);
    }

    //200
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(new Payload(true, data, message), HttpStatus.OK);
    }


    //201
    public static <T> ApiResponse <T> created(T data, String message) {
        return new ApiResponse<>(new Payload(true, data, message), HttpStatus.CREATED);
    }

    //error
    public static ApiResponse error(String message) {
        return new ApiResponse<>(new Payload(false, null, getMessage("error.internal")), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
