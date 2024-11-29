package com.ccms.service.service;

import java.util.List;
import java.util.Map;

import com.ccms.service.model.Transaction;
import com.ccms.service.model.Transaction.TransactionDetail;

public interface TransactionService {

	public Transaction getTransactionsforuser(String username);
	
	public Map<Integer, Double> getMaxExpensesForLastMonth(String username,String statusFilter);
	
	public Map<Integer, List<TransactionDetail>> getHighValueExpensesForUser(String username,String statusFilter,double amountThreshold);

	public Map<Integer, List<TransactionDetail>> getLastXTransactionsForUser(String username, int limit, String statusFilter);

	
}
