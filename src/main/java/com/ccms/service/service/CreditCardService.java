package com.ccms.service.service;

import com.ccms.service.model.CreditCard;

public interface CreditCardService {

	public CreditCard getCreditcardforuser(String username);
	
	public CreditCard addCreditCard (String username, CreditCard.CreditCardDetail creditCardDetail);
	
	public void toggleCreditCardStatus(String username, int creditCardId);

	public CreditCard getallCreditcardsforuser(String username);
	
}
