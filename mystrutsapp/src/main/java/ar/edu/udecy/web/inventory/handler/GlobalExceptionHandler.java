package ar.edu.udecy.web.inventory.handler;

import ar.edu.udecy.web.inventory.dto.ErrorDTO;
import ar.edu.udecy.web.inventory.handler.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NegativeQuantityException.class)
    public ResponseEntity<ErrorDTO> handleNegativeQuantityException(NegativeQuantityException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                ex.getMessage(),
                "Negative Quantity Error",
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleStockNotFoundException(StockNotFoundException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                ex.getMessage(),
                "Stock Not Found",
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorDTO> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                ex.getMessage(),
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                ex.getMessage(),
                "User not found",
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                ex.getMessage(),
                "Resource Not Found",
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<Object> handleProductAlreadyExistsException(ProductAlreadyExistsException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                ex.getMessage(),
                "Product Already Exists",
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDTO);
    }
    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ErrorDTO> handleTokenInvalidException(TokenInvalidException ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                ex.getMessage(),
                "Token inválido",
                HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        ErrorDTO errorDTO = new ErrorDTO(
                "An unexpected error occurred",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }
}
