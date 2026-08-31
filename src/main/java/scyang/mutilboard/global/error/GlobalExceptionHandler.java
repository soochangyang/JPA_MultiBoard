package scyang.mutilboard.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import scyang.mutilboard.global.common.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException
            (MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();

        String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
        log.warn("Validation Error: {}", errorMessage);

        return ApiResponse.error(errorMessage, HttpStatus.BAD_REQUEST);
    }

    //400
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgumentException
            (IllegalArgumentException e){
        log.warn("Business Exception: {}", e.getMessage());

        return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException e){
        log.warn("Access Denied: {}", e.getMessage());

        return ApiResponse.error("Access si denied. : "+e.getMessage(), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleAllUncaughtException(Exception e){
        log.error("Uncaught Server Error occurred: ", e);

        return ApiResponse.error("A temporary issue has occurred. Please try again in a moment.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
