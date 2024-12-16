package com.ccms.service.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccms.service.exception.CreditCardNotFoundException;
import com.ccms.service.model.CreditCard;
import com.ccms.service.model.CreditCard.CreditCardDetail;
import com.ccms.service.model.Customer;
import com.ccms.service.repository.CreditCardRepository;
import com.ccms.service.repository.CustomerRepository;
import com.ccms.service.service.CreditCardService;
import com.ccms.service.utilities.CreditCardEnDecryption;
import com.ccms.service.utilities.CreditCardFormatter;

@Service
public class CreditCardServiceimpl implements CreditCardService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	CreditCardRepository creditCardRepository;

	@Autowired
	CreditCardEnDecryption cardEnDecryption;

	@Autowired
	CreditCardFormatter cardFormatter;

	@Override
	public CreditCard getCreditcardforuser(String username,boolean showFullNumber) {

		CreditCard creditcards = creditCardRepository.findByUsername1(username);

		if (creditcards == null) {
			// Handle case where no credit card was found for the given user
			throw new CreditCardNotFoundException("No credit card found for username: " + username);
		}

		List<CreditCardDetail> activecreditcards = new ArrayList<>();

		activecreditcards = creditcards.getCreditcards();

		for (CreditCardDetail creditCardDetail : activecreditcards) {

			try {

				String decryptedCreditCardNumber = null;

				try {
					decryptedCreditCardNumber = cardEnDecryption.decrypt(creditCardDetail.getCreditCardNumber());
				} catch (Exception e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Set the decrypted credit card number back into the CreditCard object & masking if necessary

				String formattedCreditCardNumber = showFullNumber ? cardFormatter.unmaskCreditCardNumber(decryptedCreditCardNumber) : cardFormatter.maskCreditCardNumber(decryptedCreditCardNumber);
				
				creditCardDetail.setCreditCardNumber(formattedCreditCardNumber);

			} catch (Exception e) {

				// Log and handle decryption error
				throw new RuntimeException("Error decrypting credit card number for user: " + username, e);
			}
		}

		// Step 3: Return the CreditCard object with the Masked credit card number
		
		return creditcards;
	}

	@Override
	public CreditCardDetail addCreditCard(String username, CreditCardDetail creditCardDetail) {

		Customer customer = customerRepository.findByUsername(username);
		if (customer == null) {
			throw new RuntimeException("Customer not found");
		}

		// Validate the credit card details
	    validateCreditCardDetail(creditCardDetail);
	    
		CreditCard creditCard = creditCardRepository.findByUsername1(username);
		if (creditCard == null) {
			creditCard = new CreditCard();
			creditCard.setUsername(username);
			creditCard.setNameOnTheCard(customer.getName().getFirst() + " " + customer.getName().getLast());
			creditCard.setCreditcards(new ArrayList<>());
		}

		 // Ensure creditCardDetail has a randomly generated creditCardId (if not already set)
		
	    if (creditCardDetail.getCreditCardId() == 0) { // assuming int type, adjust as per your design
	        Random random = new Random();
	        creditCardDetail.setCreditCardId(random.nextInt(1000000));  // You can adjust the range or use UUID
	    }
	    
	    try {

			String encryptedCreditCardNumber = null;

			try {
				encryptedCreditCardNumber = cardEnDecryption.encrypt(creditCardDetail.getCreditCardNumber());
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Set the Encrypted credit card number back into the CreditCard object

			creditCardDetail.setCreditCardNumber(encryptedCreditCardNumber);

		} catch (Exception e) {

			// Log and handle decryption error
			throw new RuntimeException("Error decrypting credit card number for user: " + username, e);
		}
		
		creditCard.getCreditcards().add(creditCardDetail);
		
		creditCard = creditCardRepository.save(creditCard);
		
		return creditCard.getCreditcards().stream()
		.filter(card -> card.getCreditCardId() == creditCardDetail.getCreditCardId())
		.findFirst()
		.orElseThrow(()-> new RuntimeException("Saved credit card not found"));
	}

	@Override
	public boolean toggleCreditCardStatus(String username, int creditCardId) {

		CreditCard creditCard = creditCardRepository.findByUsername1(username);

		if (creditCard != null) {

			boolean creditCardFound = false;

			for (CreditCard.CreditCardDetail card : creditCard.getCreditcards()) {
				if (card.getCreditCardId() == creditCardId) {

					creditCardFound = true;

					card.setStatus(card.getStatus().equals("enabled") ? "disabled" : "enabled");
					break;
				}
			}

			if (!creditCardFound) {

				return false;
			}

			creditCardRepository.save(creditCard);

			return true;

		} else {
			return false;
		}

	}

	@Override
	public CreditCard getallCreditcardsforuser(String username) {

		CreditCard creditcards = creditCardRepository.findByUsername(username);

		if (creditcards == null) {

			return creditcards;
		}

		return creditcards;
	}

	
	private void validateCreditCardDetail(CreditCardDetail creditCardDetail) {
		
	    // Validate credit card number (basic length check)
	    if (creditCardDetail.getCreditCardNumber().length() != 16) {
	        throw new IllegalArgumentException("Credit card number must be 16 digits");
	    }
	    
	    // Validate expiry date
	    if (creditCardDetail.getExpiryMonth() < 1 || creditCardDetail.getExpiryMonth() > 12) {
	        throw new IllegalArgumentException("Invalid expiry month");
	    }

	    if (creditCardDetail.getExpiryYear() < LocalDate.now().getYear()) {
	        throw new IllegalArgumentException("Expiry year must be greater than or equal to current year");
	    }

	    // Validate CVV
	    if (creditCardDetail.getCvv() < 100 || creditCardDetail.getCvv() > 999) {
	        throw new IllegalArgumentException("Invalid CVV");
	    }

	    // Validate wireTransactionVendor (simple example, adjust to your needs)
	    if (creditCardDetail.getWireTransactionVendor() == null || creditCardDetail.getWireTransactionVendor().isEmpty()) {
	        throw new IllegalArgumentException("Wire transaction vendor is required");
	    }

	    // Validate status (basic example)
	    if (!creditCardDetail.getStatus().equals("enabled") && !creditCardDetail.getStatus().equals("disabled")) {
	        throw new IllegalArgumentException("Status must be either 'enabled' or 'disabled'");
	    }
	    
}
	
}
