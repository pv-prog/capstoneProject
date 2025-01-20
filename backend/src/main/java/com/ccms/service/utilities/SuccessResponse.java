package com.ccms.service.utilities;

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
