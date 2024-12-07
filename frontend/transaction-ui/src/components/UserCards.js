import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Toastify from 'toastify-js';
import CreditCard from './CreditCard'; // Import CreditCard component
import Transaction from './Transaction'; // Import Transaction component
import './UserCards.css'; // Import CSS for UserCards component

const UserCards = ({ username }) => {
    const [user, setUser] = useState(null); // Store the user data
    const [loading, setLoading] = useState(true); // Loading state for API request
    const [transactions, setTransactions] = useState([]); // Store transactions data
    const [loadingTransactions, setLoadingTransactions] = useState(false); // Loading state for transactions
    const [showForm, setShowForm] = useState(false); // Show the Add Card form
    const [newCardData, setNewCardData] = useState({
        creditCardId: 1,
        creditCardNumber: '',
        expiryMonth: '',
        expiryYear: '',
        cvv: '',
        wireTransactionVendor: 'visa', // Default value
        status: "enabled",
    });
    const [formErrors, setFormErrors] = useState({
        creditCardNumber: '',
        expiryMonth: '',
        expiryYear: '',
        cvv: '',
        wireTransactionVendor: '',
        status: "enabled",
    });

    // Fetch user data and credit card details
      // Function to fetch user data and credit card details
      const fetchUserData = async () => {
        try {
            const response = await axios.get(`/api/customer/creditcard/listcreditcards/${username}`);
            setUser(response.data); // Store the user data after fetching
            setLoading(false); // Set loading to false once data is fetched

            // If user has credit cards, set the next creditCardId
            if (response.data && response.data.creditcards.length > 0) {
                const latestCardId = Math.max(...response.data.creditcards.map(card => card.creditCardId));
                setNewCardData(prevData => ({
                    ...prevData,
                    creditCardId: latestCardId + 1, // Set new ID as the max + 1
                }));
            } else {
                // If no cards exist, start with 1
                setNewCardData(prevData => ({
                    ...prevData,
                    creditCardId: 1,
                }));
            }
        } catch (error) {
            Toastify({
                text: 'Error fetching user data.',
                duration: 3000,
                gravity: 'top',
                position: 'center',
                backgroundColor: '#FF6347',
            }).showToast();
            setLoading(false);
        }
    };

    // Fetch top 10 transactions for all cards combined
    const fetchTransactions = async () => {
        setLoadingTransactions(true);
        try {
            const response = await axios.get(`/api/customer/transactions/lastXTransactions/${username}?limit=10&status=both`);
            console.log('Transactions Response:', response.data);

            const allTransactions = [];
            Object.entries(response.data).forEach(([cardId, cardTransactions]) => {
                allTransactions.push(...cardTransactions.map(transaction => ({
                    ...transaction,
                    creditCardId: cardId,
                })));
            });

            allTransactions.sort((a, b) => new Date(b.transactionDate) - new Date(a.transactionDate));

            setTransactions(allTransactions.slice(0, 10)); // Get the top 10 transactions
        } catch (error) {
            Toastify({
                text: `Error fetching transactions: ${error.message}`,
                duration: 3500,
                gravity: 'center',
                position: 'right',
                backgroundColor: '#FF6347',
            }).showToast();
        } finally {
            setLoadingTransactions(false); // Reset loading state
        }
    };

    // Re-fetch user data and transactions when username changes
    useEffect(() => {
        if (username) {
            fetchUserData(); // Fetch user data when username is available
        }
    }, [username]);

    useEffect(() => {
        if (user && username) {
            fetchTransactions(); // Fetch top 10 transactions for all cards when the user data is available
        }
    }, [user, username]);

    // Handle adding a new card
    const handleAddNewCard = () => {
        setShowForm(true);
    };

   // Handle form input changes
const handleInputChange = (e) => {
    const { name, value } = e.target;

    // Handle formatting of card numbers (including the confirmation card number)
    if (name === 'creditCardNumber' || name === 'confirmCreditCardNumber') {
        let formattedValue = value.replace(/\D/g, ''); // Remove non-digit characters
        formattedValue = formattedValue.replace(/(\d{4})(?=\d)/g, '$1-'); // Insert dash after every 4 digits
        setNewCardData((prevData) => ({
            ...prevData,
            [name]: formattedValue,
        }));
    } else {
        setNewCardData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    }
};


    // Validate the form inputs
    const validateForm = () => {
        const errors = {};
        const { creditCardNumber, expiryMonth, expiryYear, cvv, wireTransactionVendor } = newCardData;

        // Validate Credit Card Number
        if (!creditCardNumber || !/^\d{4}-\d{4}-\d{4}-\d{4}$/.test(creditCardNumber)) {
            errors.creditCardNumber = 'Credit card number must be in the format 1234-5678-1234-5678.';
        }

        // Validate Expiry Month (1-12)
        if (!expiryMonth || !/^(0[1-9]|1[0-2])$/.test(expiryMonth)) {
            errors.expiryMonth = 'Expiry month must be between 01 and 12.';
        }

        // Validate Expiry Year (must be in the future)
        if (!expiryYear || !/^\d{4}$/.test(expiryYear) || parseInt(expiryYear) < new Date().getFullYear()) {
            errors.expiryYear = 'Expiry year must be a valid future year.';
        }

        // Validate CVV
        if (!cvv || !/^\d{3,4}$/.test(cvv)) {
            errors.cvv = 'CVV must be 3 or 4 digits.';
        }

        // Validate Wire Transaction Vendor (Card Type)
        if (!wireTransactionVendor) {
            errors.wireTransactionVendor = 'Please select a card type.';
        }

        setFormErrors(errors);
        return Object.keys(errors).length === 0;
    };

// Handle form submission
const handleSubmit = async (e) => {
    e.preventDefault();

    // First, validate the form inputs
    if (!validateForm()) {
        return;
    }

    // Remove dashes from the credit card number for submission
    const formattedCardData = { ...newCardData };
    formattedCardData.creditCardNumber = formattedCardData.creditCardNumber.replace(/-/g, '');

    // Check if the credit card number already exists in the current list of cards
    const cardExists = user?.creditcards?.some(
        (card) => card.creditCardNumber.replace(/-/g, '') === formattedCardData.creditCardNumber
    );

    if (cardExists) {
        Toastify({
            text: 'This credit card number already exists!',
            duration: 3500,
            gravity: 'center',
            position: 'right',
            backgroundColor: '#FFEFD5',
            className: "large-toast",
        }).showToast();
        return; // Don't submit if the card already exists
    }

    try {
        // Post the data with the modified creditCardNumber
        const response = await axios.post(`/api/customer/creditcard/addcreditcard/${username}`, formattedCardData);

        // Show success toast
        Toastify({
            text: 'Credit card added successfully!',
            duration: 3000,
            gravity: 'center',
            position: 'right',
            backgroundColor: 'green',
            className: "large-toast",
        }).showToast();

        // Close the form
        setShowForm(false);

        // After submitting, fetch the updated user data to ensure the new card is included
        fetchUserData(); // Re-fetch the user's credit cards to ensure they are synced

        // Reset the newCardData so the form is cleared
        setNewCardData({
            creditCardId: (user?.creditcards?.length || 0) + 1, // Dynamically update based on the existing cards
            creditCardNumber: '',
            expiryMonth: '',
            expiryYear: '',
            cvv: '',
            wireTransactionVendor: 'visa', // Default value
            status: "enabled",
        });

    } catch (error) {
        Toastify({
            text: 'Error adding card. Please try again.',
            duration: 3500,
            gravity: 'center',
            position: 'right',
            backgroundColor: '#FFEFD5',
            className: "large-toast",
        }).showToast();
    }
};





    // Helper function to find the card by creditCardId
    const getCardDetailsById = (cardId) => {
        return user?.creditcards?.find(card => card.creditCardId === parseInt(cardId));
    };

    if (loading) {
        return <p>Loading...</p>;
    }


    if (!user || !user.creditcards || user.creditcards.length === 0) {
        return (
          <div style={{ 
            backgroundColor: 'rgba(0, 0, 0, 0.5)', 
            position: 'absolute', 
            top: 0, 
            left: 0, 
            right: 0, 
            bottom: 0, 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center', 
            color: 'white', 
            fontSize: '24px', 
            zIndex: 9999 
          }}>
            <p>Oops! Backend systems are down.Try again after sometime</p>
          </div>
        );
      }
    

    return (
        
        <div className="user-cards-container">
             <div className="credit-card-container add-card" onClick={handleAddNewCard}>
                    <div className="add-card-content">
                        <span className="text">Add New Card</span>
                    </div>
                </div>
            <div className="credit-cards">
                {user.creditcards.map((card) => (
                    <div key={card.creditCardId} className="credit-card-container">
                        <CreditCard card={card} user={user} />
                    </div>
                ))}

               
            </div>

            {/* Add New Card Form Modal */}
            {showForm && (
    <div className="form-modal">
        <div className="form-container">
            <h2>Add New Credit Card</h2>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="creditCardNumber">Card Number</label>
                    <input
                        type="text"
                        id="creditCardNumber"
                        name="creditCardNumber"
                        value={newCardData.creditCardNumber}
                        onChange={handleInputChange}
                        maxLength="19"
                        placeholder="Enter 16-digit card number"
                    />
                    {formErrors.creditCardNumber && <span className="error">{formErrors.creditCardNumber}</span>}
                </div>

                <div className="form-group">
                    <label htmlFor="confirmCreditCardNumber">Confirm Card Number</label>
                    <input
                        type="text"
                        id="confirmCreditCardNumber"
                        name="confirmCreditCardNumber"
                        value={newCardData.confirmCreditCardNumber}
                        onChange={handleInputChange}
                        maxLength="19"
                        placeholder="Confirm 16-digit card number"
                    />
                    {formErrors.confirmCreditCardNumber && <span className="error">{formErrors.confirmCreditCardNumber}</span>}
                </div>

                {/* Check if both card numbers match */}
                {newCardData.creditCardNumber && newCardData.confirmCreditCardNumber && newCardData.creditCardNumber !== newCardData.confirmCreditCardNumber && (
                    <span className="error">Card numbers do not match!</span>
                )}

                <div className="form-group">
                    <label htmlFor="expiryMonth">Expiry Month</label>
                    <input
                        type="text"
                        id="expiryMonth"
                        name="expiryMonth"
                        value={newCardData.expiryMonth}
                        onChange={handleInputChange}
                        maxLength="2"
                        placeholder="MM"
                    />
                    {formErrors.expiryMonth && <span className="error">{formErrors.expiryMonth}</span>}
                </div>

                <div className="form-group">
                    <label htmlFor="expiryYear">Expiry Year</label>
                    <input
                        type="text"
                        id="expiryYear"
                        name="expiryYear"
                        value={newCardData.expiryYear}
                        onChange={handleInputChange}
                        maxLength="4"
                        placeholder="YYYY"
                    />
                    {formErrors.expiryYear && <span className="error">{formErrors.expiryYear}</span>}
                </div>

                <div className="form-group">
                    <label htmlFor="cvv">CVV</label>
                    <input
                        type="text"
                        id="cvv"
                        name="cvv"
                        value={newCardData.cvv}
                        onChange={handleInputChange}
                        maxLength="4"
                        placeholder="CVV"
                    />
                    {formErrors.cvv && <span className="error">{formErrors.cvv}</span>}
                </div>

                <div className="form-group">
                    <label htmlFor="wireTransactionVendor">Card Type</label>
                    <select
                        id="wireTransactionVendor"
                        name="wireTransactionVendor"
                        value={newCardData.wireTransactionVendor}
                        onChange={handleInputChange}
                    >
                        <option value="visa">Visa</option>
                        <option value="mastercard">Mastercard</option>
                        <option value="rupay">Rupay</option>
                        <option value="americanexpress">American Express</option>
                    </select>
                    {formErrors.wireTransactionVendor && <span className="error">{formErrors.wireTransactionVendor}</span>}
                </div>

                <div className="form-actions">
                    <button type="button" className="cancel-btn" onClick={() => setShowForm(false)}>Cancel</button>
                    {/* Disable submit if card numbers don't match */}
                    <button 
                        type="submit" 
                        className="submit-btn" 
                        disabled={newCardData.creditCardNumber !== newCardData.confirmCreditCardNumber}
                    >
                        Add Card
                    </button>
                </div>
            </form>
        </div>
    </div>
)}


            <div className="transactions-container">
                <h3>Top 10 Transactions</h3>
                {transactions.length === 0 ? (
                    <p>No transactions available.</p>
                ) : (
                    <ul className="transactions-list">
                        {transactions.map((transaction) => {
                            const matchedCard = getCardDetailsById(transaction.creditCardId);
                            if (!matchedCard) {
                                return <p key={transaction.transactionId}>Card data is missing for this transaction.</p>;
                            }

                            return (
                                <li key={transaction.transactionId}>
                                    <Transaction transaction={transaction} card={matchedCard} />
                                </li>
                            );
                        })}
                    </ul>
                )}
            </div>
        </div>
    );
};

export default UserCards;
