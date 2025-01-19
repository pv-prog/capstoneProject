package com.ccms.service.model;

import com.ccms.service.model.Transaction.TransactionDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a transaction associated with a specific credit card number.
 * <p>
 * This class encapsulates a single transaction and associates it with
 * a credit card, identified by its `creditCardNumber`. The transaction
 * details are stored in the {@link TransactionDetail} object.
 * </p>
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionWithCreditCardInfo {
	
	
	private String creditCardNumber;  
    private TransactionDetail transactionDetail;

}
