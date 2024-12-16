package com.ccms.service.service;

import com.ccms.service.model.CreditCard;
import com.ccms.service.model.CreditCard.CreditCardDetail;

public interface CreditCardService {

	public CreditCard getCreditcardforuser(String username,boolean showFullNumber);
	
	public CreditCardDetail addCreditCard (String username, CreditCard.CreditCardDetail creditCardDetail);
	
	public boolean toggleCreditCardStatus(String username, int creditCardId);

	public CreditCard getallCreditcardsforuser(String username);
	
}
