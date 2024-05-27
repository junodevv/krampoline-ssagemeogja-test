package project.back.etc.commonException;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import project.back.dto.ApiResponse;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 유효하지 않은 메서드 인자를 처리
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ApiResponse<String> response = ApiResponse.fail(errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 메서드 호출이 부적절한 상태일 때 발생
     * HTTP 403 Forbidden
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalStateException(IllegalStateException ex) {
        return new ResponseEntity<>(ApiResponse.fail(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * 접근이 거부될 때 발생
     * HTTP 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(ApiResponse.fail(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * 엔티티를 찾을 수 없을 때 발생
     * HTTP 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ApiResponse.fail(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * 클라이언트의 잘못된 요청으로 발생
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ApiResponse.fail("서버 오류가 발생했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 예상치 못한 모든 예외를 처리
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAllUncaughtException(Exception ex) {
        return new ResponseEntity<>(ApiResponse.fail("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 사용자 정의 예외 클래스
     * 데이터가 존재하지 않거나 비어있을 때 발생
     * HTTP 404 Not Found
     */
    @ExceptionHandler(NoContentFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoContentFoundException(NoContentFoundException ex) {
        return new ResponseEntity<>(ApiResponse.fail(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * 사용자 정의 예외 클래스
     * 데이터가 이미 존재하여 중복되는 값일 때 발생
     * HTTP 409 Conflict
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<String>> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>(ApiResponse.fail(ex.getMessage()), HttpStatus.CONFLICT);
    }
}
