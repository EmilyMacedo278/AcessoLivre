package br.com.fiap.acessolivre.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos. Verifique os campos.", campos);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> tratarStatus(ResponseStatusException ex) {
        String mensagem = ex.getReason() != null ? ex.getReason() : "Requisição não pôde ser processada.";
        return resposta(ex.getStatusCode(), mensagem, null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarJson(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST,
                "JSON inválido ou valor incompatível com o tipo esperado.", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarIntegridade(DataIntegrityViolationException ex) {
        return resposta(HttpStatus.CONFLICT,
                "Operação não permitida: existe um conflito com os dados armazenados.", null);
    }

    private ResponseEntity<Map<String, Object>> resposta(
            HttpStatusCode status, String mensagem, Map<String, String> campos) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", OffsetDateTime.now().toString());
        corpo.put("status", status.value());
        corpo.put("mensagem", mensagem);
        if (campos != null && !campos.isEmpty()) {
            corpo.put("campos", campos);
        }
        return ResponseEntity.status(status).body(corpo);
    }
}
