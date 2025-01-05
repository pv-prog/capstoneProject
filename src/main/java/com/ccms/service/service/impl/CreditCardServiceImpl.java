package com.ccms.service.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ccms.service.exception.CreditCardNotFoundException;
import com.ccms.service.exception.CreditCardProcessingException;
import com.ccms.service.exception.CustomerNotFoundException;
import com.ccms.service.exception.DuplicateCreditCardException;
import com.ccms.service.model.CreditCard;
import com.ccms.service.model.CreditCard.CreditCardDetail;
import com.ccms.service.model.Customer;
import com.ccms.service.repository.CreditCardRepository;
import com.ccms.service.repository.CustomerRepository;
import com.ccms.service.service.CreditCardService;
import com.ccms.service.utilities.CreditCardEnDecryption;
import com.ccms.service.utilities.CreditCardFormatter;

@Service
public class CreditCardServiceImpl implements CreditCardService {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardServiceImpl.class);

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	CreditCardRepository creditCardRepository;

	@Autowired
	CreditCardEnDecryption cardEnDecryption;

	@Autowired
	CreditCardFormatter cardFormatter;

	@Override
	public CreditCard getCreditCardForUser(String username, boolean showFullNumber) {

		Customer customer = customerRepository.findByUsername(username);

		if (customer == null) {
			throw new CustomerNotFoundException("Customer not found" + username);
		}

		CreditCard creditcards = creditCardRepository.findByUsername1(username);

		if (creditcards == null) {
			// Handle case where no credit card was found for the given user
			throw new CreditCardNotFoundException("No credit card found for username: " + username);
		}

		List<CreditCardDetail> activecreditcards = creditcards.getCreditcards();

		for (CreditCardDetail creditCardDetail : activecreditcards) {

			try {
				String decryptedCreditCardNumber = cardEnDecryption.decrypt(creditCardDetail.getCreditCardNumber());
				String formattedCreditCardNumber = showFullNumber
						? cardFormatter.unmaskCreditCardNumber(decryptedCreditCardNumber)
						: cardFormatter.maskCreditCardNumber(decryptedCreditCardNumber);

				creditCardDetail.setCreditCardNumber(formattedCreditCardNumber);

			} catch (Exception e) {

				logger.error("Error processing credit card for user: {}", username, e);
				throw new CreditCardProcessingException("Error processing credit card for user: " + username, e);
			}
		}

		return creditcards;
	}

	@Override
	public CreditCardDetail addCreditCard(String username, CreditCardDetail creditCardDetail) {

		Customer customer = customerRepository.findByUsername(username);
		if (customer == null) {
			throw new CustomerNotFoundException("Customer not found");
		}

		// Validate the credit card details
		validateCreditCardDetail(creditCardDetail);

		// Retrieve all credit cards for the given user

		List<CreditCardDetail> creditCards = getAllCreditCardsForUser(username).getCreditcards();

		// Check if there are any existing credit cards

		if (creditCards == null || creditCards.isEmpty()) {
			// No credit cards found for the user
			logger.info("No existing credit cards found for user: {}", username);
		} else {
			// Check for duplicate credit card by decrypting each stored card number
			for (CreditCardDetail storedCardDetail : creditCards) {
				try {
					// Decrypt the stored credit card number
					String decryptedStoredCardNumber = cardEnDecryption.decrypt(storedCardDetail.getCreditCardNumber());

					// Compare decrypted stored card number with the provided card number
					if (decryptedStoredCardNumber.equals(creditCardDetail.getCreditCardNumber())) {

						// If match found, throw Duplicate exception

						throw new DuplicateCreditCardException("Credit card already associated with this user.");
					}
				} catch (DuplicateCreditCardException dce) {
					// Log the duplicate exception and rethrow it
					logger.warn("Duplicate credit card detected for user {}: {}", username, dce.getMessage());
					throw dce; // Re-throw the DuplicateCreditCardException

				} catch (Exception e) {
					// Handle errors during decryption
					logger.error("Error decrypting credit card number for user {}: {}", username, e.getMessage(), e);
					throw new CreditCardProcessingException("Error decrypting credit card number for user: " + username,
							e);
				}
			}
		}

		CreditCard creditCard = creditCardRepository.findByUsername1(username);

		if (creditCard == null) {

			creditCard = new CreditCard();
			creditCard.setUsername(username);
			creditCard.setNameOnTheCard(customer.getName().getFirst() + " " + customer.getName().getLast());
			creditCard.setCreditcards(new ArrayList<>());
		}

		// Ensure creditCardDetail has a randomly generated creditCardId (if not already
		// set)

		if (creditCardDetail.getCreditCardId() == 0) { // assuming int type, adjust as per your design
			Random random = new Random();
			creditCardDetail.setCreditCardId(random.nextInt(1000000)); // You can adjust the range or use UUID
		}

		try {
			String encryptedCreditCardNumber = cardEnDecryption.encrypt(creditCardDetail.getCreditCardNumber());
			creditCardDetail.setCreditCardNumber(encryptedCreditCardNumber);
		} catch (Exception e) {

			logger.error("Error encrypting credit card number for user: {}", username, e);
			throw new CreditCardProcessingException("Error encrypting credit card number for user: " + username, e);
		}

		creditCard.getCreditcards().add(creditCardDetail);

		creditCard = creditCardRepository.save(creditCard);

		return creditCard.getCreditcards().stream()
				.filter(card -> card.getCreditCardId() == creditCardDetail.getCreditCardId()).findFirst()
				.orElseThrow(() -> new CreditCardNotFoundException("Saved credit card not found"));
	}

	@Override
	public boolean toggleCreditCardStatus(String username, int creditCardId) {

		CreditCard creditCard = creditCardRepository.findByUsername1(username);

		if (creditCard == null) {
			throw new CreditCardNotFoundException("No credit card found for username: " + username);
		}

		return creditCard.getCreditcards().stream().filter(card -> card.getCreditCardId() == creditCardId).findFirst()
				.map(card -> {
					card.setStatus(card.getStatus().equals("enabled") ? "disabled" : "enabled");
					creditCardRepository.save(creditCard);
					return true;
				}).orElseThrow(() -> new CreditCardNotFoundException("Credit card not found for ID: " + creditCardId));

	}

	@Override
	public CreditCard getAllCreditCardsForUser(String username) {

		Customer customer = customerRepository.findByUsername(username);

		if (customer == null) {

			throw new CustomerNotFoundException("Customer not found : " + username);
		}

		CreditCard creditcards = creditCardRepository.findByUsername(username);

		if (creditcards == null) {

			throw new CreditCardNotFoundException("No credit card found for username: " + username);
		}

		return creditcards;
	}

	private void validateCreditCardDetail(CreditCardDetail creditCardDetail) {

		// Validate credit card number (basic length check)
		if (creditCardDetail.getCreditCardNumber().length() != 16) {
			throw new IllegalArgumentException(" Credit card number must be 16 digits");
		}

		// Validate expiry date
		if (creditCardDetail.getExpiryMonth() < 1 || creditCardDetail.getExpiryMonth() > 12) {
			throw new IllegalArgumentException("Invalid expiry month");
		}

		if (creditCardDetail.getExpiryYear() < LocalDate.now().getYear()) {
			throw new IllegalArgumentException("Expiry year must be greater than or equal to current year");
		}

		if (creditCardDetail.getExpiryYear() == LocalDate.now().getYear()
				&& creditCardDetail.getExpiryMonth() < LocalDate.now().getMonthValue()) {
			throw new IllegalArgumentException("Expiry date is in the past");
		}

		// Validate CVV
		if (creditCardDetail.getCvv() < 100 || creditCardDetail.getCvv() > 999) {
			throw new IllegalArgumentException("Invalid CVV");
		}

		// Validate wireTransactionVendor (simple example, adjust to your needs)
		if (creditCardDetail.getWireTransactionVendor() == null
				|| creditCardDetail.getWireTransactionVendor().isEmpty()) {
			throw new IllegalArgumentException("Wire transaction vendor is required");
		}

		// Validate status (basic example)
		if (!creditCardDetail.getStatus().equals("enabled") && !creditCardDetail.getStatus().equals("disabled")) {
			throw new IllegalArgumentException("Status must be either 'enabled' or 'disabled'");
		}

	}

}
