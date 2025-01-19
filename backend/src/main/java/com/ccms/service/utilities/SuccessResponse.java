package com.ccms.service.utilities;

/**
 * A generic class that represents a standard structure for a successful API response.
 * <p>
 * This class is designed to wrap the response data along with a fixed status field ("success").
 * It can be used to return any type of data from a service or API endpoint in a consistent format.
 * </p>
 * 
 * @param <T> the type of data being returned in the response
 * @since 1.0
 */

public class SuccessResponse<T> {
    
	private String status = "success";
    
	private T data;

    public SuccessResponse(T data) {
        this.data = data;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SuccessResponse [status=" + status + ", data=" + data + "]";
	}
 
}
