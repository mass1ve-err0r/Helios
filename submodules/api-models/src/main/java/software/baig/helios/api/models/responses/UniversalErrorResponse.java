package software.baig.helios.api.models.responses;

import java.time.Instant;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;


public class UniversalErrorResponse {
	
	private final Instant timestamp;
	private final int statusCode;
	private final String message;
	private final String path;


	public UniversalErrorResponse(HttpStatusCode httpStatus, WebRequest request, Exception exception) {
		this.timestamp = now();

		this.statusCode = httpStatus.value();
		this.message = exception.getMessage();
		this.path = ((ServletWebRequest) request).getRequest().getRequestURI();
	}

	Instant now() {
		return Instant.now();
	}
	
	public Instant getTimestamp() {
		return timestamp;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getPath() {
		return path;
	}
}
