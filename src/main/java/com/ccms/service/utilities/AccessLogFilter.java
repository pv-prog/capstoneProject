package com.ccms.service.utilities;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import com.ccms.service.kafka.AccessLogKafkaProducer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@WebFilter("/*")
public class AccessLogFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);

	// Injecting Kafka producer service to send logs to Kafka
	@Autowired
	private AccessLogKafkaProducer accessLogKafkaProducer;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// No initialization needed
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// Wrap the request and response to capture the body content if needed
		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(
				(HttpServletResponse) response);

		// Capture the start time to calculate response time
		long startTime = System.currentTimeMillis();

		try {
			// Proceed with the next filter in the chain
			chain.doFilter(wrappedRequest, wrappedResponse);
		} catch (Exception ex) {
			// Log the exception
			logger.error("Exception occurred while processing the request", ex);
			// You can rethrow the exception if needed or handle it here
			throw ex;
		}

		// Calculate the response time
		long duration = System.currentTimeMillis() - startTime;

		// Gather the log data
		String clientIp = request.getRemoteAddr();
		String method = wrappedRequest.getMethod();
		String url = wrappedRequest.getRequestURI();
		int statusCode = wrappedResponse.getStatus();
		String userAgent = wrappedRequest.getHeader("User-Agent");
		String referer = wrappedRequest.getHeader("Referer");
		long requestSize = wrappedRequest.getContentLengthLong();
		long responseSize = wrappedResponse.getContentSize();

        // For getting the current timestamp in ISO-8601 format

		// Prepare the log message in JSON format with timestamp
		String logMessageJson = String.format(
		    "{\"timestamp\": \"%s\", \"method\": \"%s\", \"url\": \"%s\", \"ip\": \"%s\", \"status\": %d, \"duration\": %d, " +
		    "\"userAgent\": \"%s\", \"requestSize\": %d, \"responseSize\": %d, \"referer\": \"%s\"}",
		    Instant.now().toString(),  // Add the current timestamp in ISO-8601 format
		    method,
		    url,
		    clientIp,
		    statusCode,
		    duration,
		    (userAgent != null ? userAgent : "N/A"),  // Handle null values for User-Agent
		    requestSize,
		    responseSize,
		    (referer != null ? referer : "N/A")       // Handle null values for Referer
		);

		// Log to console (optional)
		logger.info(logMessageJson);

		// Send the log message to Kafka
		accessLogKafkaProducer.sendLog(logMessageJson);


		// Ensure the response is written to the client
		wrappedResponse.copyBodyToResponse();
	}

	@Override
	public void destroy() {
		// No cleanup needed
	}
}
