package com.ccms.service.service;

import java.util.List;
import java.util.Map;

import com.ccms.service.model.Transaction;
import com.ccms.service.model.Transaction.TransactionDetail;

public interface TransactionService {

	public Transaction getTransactionsforuser(String username);
	
	public List<Map<String, Object>> getMaxExpensesForLastMonth(String username,String statusFilter);
	
	public Map<String, List<Map<String, String>>> getHighValueExpensesForUser(String username,int limit, String statusFilter,double amountThreshold);

	public Map<Integer, List<TransactionDetail>> getLastXTransactionsForUser(String username, int limit, String statusFilter);
	
	public List<Map<String, Object>> getLastXExpensesForUser(String username, int limit, String statusFilter);

	
}
