// src/components/AddCreditCard.js
import PropTypes from 'prop-types';
import React, { useState } from 'react';
import './AddCreditCard.css'; // Import the CSS file

const AddCreditCard = ({ onAddCard }) => {
    const [cardNumber, setCardNumber] = useState('');
    const [expirationDate, setExpirationDate] = useState('');
    const [cvv, setCvv] = useState('');
    const [cardholderName, setCardholderName] = useState('');
    const [error, setError] = useState('');

    const validateCardNumber = (number) => {
        const regex = /^\d{13,19}$/; // Validates 13 to 19 digits
        return regex.test(number);
    };

    const validateExpirationDate = (date) => {
        const regex = /^(0[1-9]|1[0-2])\/?([0-9]{2})$/; // MM/YY format
        const [month, year] = date.split('/');
        const currentDate = new Date();
        const expiryDate = new Date(`20${year}`, month - 1); // Adjust year to be four digits
        return regex.test(date) && expiryDate > currentDate;
    };

    const validateCvv = (cvv) => {
        const regex = /^\d{3,4}$/; // Validates 3 or 4 digits
        return regex.test(cvv);
    };

    const validateCardholderName = (name) => {
        return name.trim() !== ''; // Cardholder name should not be empty
    };

    const handleCardNumberChange = (e) => {
        const value = e.target.value;
        const formattedValue = formatCardNumber(value);
        setCardNumber(formattedValue);
        if (!validateCardNumber(formattedValue)) {
            setError('Invalid card number. It should be 13 to 19 digits.');
        } else {
            setError(''); // Clear error if valid
        }
    };
    

    const handleExpirationDateChange = (e) => {
        const value = e.target.value;
        setExpirationDate(value);
        if (!validateExpirationDate(value)) {
            setError('Invalid expiration date. It should be in MM/YY format and not expired.');
        } else {
            setError(''); // Clear error if valid
        }
    };

    const handleCvvChange = (e) => {
        const value = e.target.value;
        setCvv(value);
        if (!validateCvv(value)) {
            setError('Invalid CVV. It should be 3 or 4 digits.');
        } else {
            setError(''); // Clear error if valid
        }
    };

    const formatCardNumber = (number) => {
        // Remove all non-digit characters
        const cleaned = number.replace(/\D/g, '');
        // Split into groups of 4 and join with hyphens
        const parts = cleaned.match(/.{1,4}/g);
        return parts ? parts.join('-') : cleaned;
    };

    const handleCardholderNameChange = (e) => {
        const value = e.target.value;
        const formattedValue = formatCardNumber(value);
        setCardNumber(formattedValue);
        if (!validateCardNumber(formattedValue)) {
            setError('Invalid card number. It should be 16 digits.');
        } else {
            setError(''); // Clear error if valid
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!error && cardNumber && expirationDate && cvv && cardholderName) {
            const newCard = {
                cardNumber,
                expirationDate,
                cvv,
                cardholderName,
            };
            onAddCard(newCard); // Call the parent function to add the new card
            // Reset form fields
            setCardNumber('');
            setExpirationDate('');
            setCvv('');
            setCardholderName('');
        }
    };

    return (
        <div className="add-credit-card">
            <form onSubmit={handleSubmit}>
                <h2>Add Credit Card</h2>
                {error && <div className="error-message">{error}</div>}
                <div>
                    <label>Card Number:</label>
                    <input
                        type="text"
                        value={cardNumber}
                        onChange={handleCardNumberChange}
                        required
                    />
                </div>
                <div>
                    <label>Expiration Date (MM/YY):</label>
                    <input
                        type="text"
                        value={expirationDate}
                        onChange={handleExpirationDateChange}
                        required
                    />
                </div>
                <div>
                    <label>CVV:</label>
                    <input
                        type="text"
                        value={cvv}
                        onChange={handleCvvChange}
                        required
                    />
                </div>
                <div>
                    <label>Cardholder Name:</label>
                    <input
                        type="text"
                        value={cardholderName}
                        onChange={handleCardholderNameChange}
                        required
                    />
                </div>
                <button type="submit">Add Card</button>
            </form>
        </div>
    );
};

// PropTypes for validation
AddCreditCard.propTypes = {
    onAddCard: PropTypes.func.isRequired,
};

export default AddCreditCard;