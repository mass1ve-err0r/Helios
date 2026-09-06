package software.baig.helios.api.advices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import software.baig.helios.api.models.responses.UniversalErrorResponse;


@RestControllerAdvice
public class UniversalControllerLoggingAdvice extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UniversalControllerLoggingAdvice.class);


	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		LOG.error(ex.getLocalizedMessage(), ex);

		UniversalErrorResponse response = new UniversalErrorResponse(statusCode, request, ex);
		return ResponseEntity.status(statusCode).headers(headers).body(response);
	}
	
}
