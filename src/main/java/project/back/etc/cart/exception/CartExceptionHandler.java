package project.back.etc.cart.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.back.controller.CartController;
import project.back.dto.ApiResponse;

@RestControllerAdvice(basePackageClasses = CartController.class)
public class CartExceptionHandler {
    /**
     * 도메인 검증시 클라이언트 측에서 유효하지 않은 값을 넘겼을 경우
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ApiResponse.fail(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    /**
     * 컨트롤러 검증시 클라이언트 측에서 유효하지 않은 값(parameter)을 넘겼을 경우
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(ConstraintViolationException ex) {
        return new ResponseEntity<>(
                ApiResponse.fail(ex.getMessage().split(":")[1].trim()), HttpStatus.BAD_REQUEST);
    }
}
