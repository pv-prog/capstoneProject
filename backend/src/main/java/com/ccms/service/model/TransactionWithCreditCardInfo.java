package com.ccms.service.model;

import com.ccms.service.model.Transaction.TransactionDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionWithCreditCardInfo {
	
	
	private String creditCardNumber;  
    private TransactionDetail transactionDetail;

}
