package com.ccms.customer.repository;

/**
 * A projection interface for retrieving specific customer details.
 * <p>
 * This interface is used to fetch a subset of fields (e.g., `username` and `password`) from the `Customer` entity.
 * Projections are typically used in Spring Data JPA to reduce the amount of data retrieved from the database,
 * improving performance when only a subset of an entity's fields are needed.
 */

public interface CustomerProjection {

	String getusername();
    String getpassword();
}
