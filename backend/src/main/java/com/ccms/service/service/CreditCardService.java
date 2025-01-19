package com.ccms.service.service;

import com.ccms.service.model.CreditCard;
import com.ccms.service.model.CreditCard.CreditCardDetail;

/**
 * Service interface for managing credit cards associated with a user.
 * <p>
 * This service provides methods for performing operations such as fetching credit cards
 * for a user, adding a new credit card, and toggling the status of a credit card.
 * </p>
 */

public interface CreditCardService {

	public CreditCard getCreditCardForUser(String username,boolean showFullNumber);
	
	public CreditCardDetail addCreditCard (String username, CreditCard.CreditCardDetail creditCardDetail);
	
	public boolean toggleCreditCardStatus(String username, int creditCardId);

	public CreditCard getAllCreditCardsForUser(String username);
	
}
