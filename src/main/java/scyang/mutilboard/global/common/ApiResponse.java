package scyang.mutilboard.global.common;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true, data, "success");
    }
    public static <T> ApiResponse<T> success(T data, String message){
        return new ApiResponse<>(true, data, message);
    }

    public static <Void> ApiResponse<Void> success(){
        return new ApiResponse<>(true, null, "success");
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(false, null, message);
    }
}
