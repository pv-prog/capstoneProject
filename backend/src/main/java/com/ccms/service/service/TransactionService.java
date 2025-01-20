package com.ccms.service.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ccms.service.model.Transaction;
import com.ccms.service.model.Transaction.TransactionDetail;
import com.ccms.service.model.TransactionWithCardId;

public interface TransactionService {

//	public Transaction getTransactionsforuser(String username);
	
	public Page<TransactionWithCardId> getTransactionsForUser(String username, Pageable pageable);
	
	public Page<Map<String, Object>> getMaxExpensesForLastMonth(String username,String statusFilter,Pageable pageable);
	
	public Map<String, Page<Map<String, String>>> getHighValueExpensesForUser(String username,int limit, String statusFilter,double amountThreshold,Pageable pageable);

	public Map<Integer, Page<TransactionDetail>> getLastXTransactionsForUser(String username, int limit, String statusFilter,Pageable pageable);
	
	public  Map<String, Object>  getLastXExpensesForUser(String username, int limit, String statusFilter,Pageable pageable);

	
}
