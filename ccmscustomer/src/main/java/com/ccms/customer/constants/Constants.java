package com.ccms.customer.constants;

import org.springframework.context.annotation.Configuration;

/**
 * A utility class to hold application constants.
 * <p>
 * This class is designed to store static final constants that are used across the application. 
 * It helps maintain a central place for all constant values, making it easier to update 
 * them in the future without needing to change multiple places in the codebase.
 * </p>
 * 
 * @since 1.0
 */

@Configuration
public class Constants {
	
	
	public static final String CREDIT_CARD_TOGGLE_SERVICE_URL = "http://ccmsservice/api/customer/creditcard/togglecreditcard/";


}
