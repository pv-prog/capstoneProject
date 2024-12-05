import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Toastify from 'toastify-js';
import './CreditCard.css'; // Import your CSS for styling
import Txn from './Txn'; // Import the Transaction component

// Function to get the appropriate card icon based on card type
const getCardIcon = (cardType) => {
    switch (cardType) {
        case 'visa':
            return require('../assets/icons/visa.png');
        case 'mastercard':
            return require('../assets/icons/mastercard.png');
        case 'rupay':
            return require('../assets/icons/rupay.png');
        default:
            return require('../assets/icons/default.png');
    }
};

// Function to generate a random gradient background based on card type
const getRandomGradient = (cardType) => {
    const colors = {
        visa: ['black', '#40E0D0'],
        mastercard: ['#EB001B', '#FF8C00'],
        'american express': ['#0070BA', '#A7D3E0'],
        rupay: ['black', 'grey'],
        default: ['#CCCCCC', '#AAAAAA'],
    };
    const type = cardType ? cardType.toLowerCase() : 'default';
    const [color1, color2] = colors[type] || colors.default;
    return `linear-gradient(135deg, ${color1} 30%, ${color2} 100%)`;
};

const CreditCard = ({ card, user, isAddCard }) => {
    const [showCvv, setShowCvv] = useState(false); // State to toggle CVV visibility
    const [isActive, setIsActive] = useState(card.status === 'enabled'); // Initialize based on card status
    const [transactions, setTransactions] = useState([]); // State for storing fetched transactions
    const [loadingTransactions, setLoadingTransactions] = useState(false); // Loading state for transactions
    const [showTransactions, setShowTransactions] = useState(false); // State to toggle showing transactions

    // Function to toggle CVV visibility
    const handleToggleCvv = (e) => {
        e.stopPropagation();
        setShowCvv(!showCvv);
    };

    // Function to toggle card status (enabled/disabled)
    const handleToggleStatus = async () => {
        try {
            // Toggle the card status via the API
            const response = await axios.put(`/api/customer/creditcard/togglecreditcard/${user.username}/${card.creditCardId}/toggle`);
            if (response.status === 200) {
                // Toggle the card's active state
                const newStatus = !isActive;
                setIsActive(newStatus);  // Update state
    
                // Log the new status after updating the state
                console.log(`${user.username} card ${card.creditCardNumber} ${newStatus ? 'Enabled' : 'Disabled'}`);
            }
        } catch (error) {
            const errorMessage = `Error: Activate/Disable Card, ${error.message}`;
            Toastify({
                text: `${errorMessage}`,
                duration: 3500,
                gravity: 'center',
                position: 'right',
                backgroundColor: '#FFEFD5',
                className: "large-toast",
            }).showToast();
            
            // If the API call fails, revert the state
            setIsActive(!isActive);  // Revert to the original state
        }
    };

    // Function to fetch transactions for the selected card
    const fetchTransactions = async () => {
        setLoadingTransactions(true);
        try {
            const response = await axios.get(`/api/customer/transactions/${user.username}`);
            console.log("Fetched response data: ", response.data);

            // Find the credit card that matches the selected card's creditCardId
            const creditCard = response.data.creditcards.find(cardItem => cardItem.creditCardId === card.creditCardId);

            if (creditCard && creditCard.transactions) {
                // No need to filter by cardId in transactions because it's already inside the correct creditCardId block
                const filteredTransactions = creditCard.transactions;
                setTransactions(filteredTransactions); // Set filtered transactions for this card
            } else {
                // No transactions found for the current card
                setTransactions([]);
            }
        } catch (error) {
            Toastify({
                text: `Error fetching transactions: ${error.message}`,
                duration: 3500,
                gravity: 'center',
                position: 'right',
                backgroundColor: '#FFEFD5',
                className: "large-toast",
            }).showToast();
        } finally {
            setLoadingTransactions(false); // Reset loading state
        }
    };

    // Handle toggle for showing/hiding transactions
    const handleToggleTransactions = () => {
        if (!isActive) {
            Toastify({
                text: "Error: Transactions can only be shown for active cards.\n Enable the card to view transactions",
                duration: 3500,
                gravity: 'center',
                position: 'right',
                backgroundColor: '#FFEFD5',
                className: "large-toast",
            }).showToast();
            return; // Don't show transactions for a disabled card
        }
    
        setShowTransactions(!showTransactions);
        if (!showTransactions) {
            fetchTransactions(); // Fetch transactions only when showing them
        }
    };

    // If this is the "Add New Card" card, render the Add New Card UI
    if (isAddCard) {
        return (
            <div className="credit-card add-card" onClick={() => alert("Add a new card")}>
                <div className="plus-icon">+</div>
                <div className="text">Add New Card</div>
            </div>
        );
    }

    // Get the background gradient based on card vendor type
    const gradientBackground = getRandomGradient(card.wireTransactionVendor);
    const cardStatusBackground = isActive ? 'green' : 'red'; // Green for active, red for disabled

    return (
        <div className="credit-card" style={{ background: gradientBackground }}>
            <div className="card-details" onClick={handleToggleTransactions}>
                <img className="card-type" src={getCardIcon(card.wireTransactionVendor)} alt={card.wireTransactionVendor} />
                <div className="card-status" style={{ backgroundColor: cardStatusBackground }}>
                    {isActive ? 'Active' : 'Disabled'}
                </div>
                <p className="card-number">{card.creditCardNumber}</p>
                <div className="card-info">
                    <p className="expiry">Expiry: {card.expiryMonth}/{card.expiryYear}</p>
                    <div className="cvv-container">
                        <p className="cvv">CVV: {showCvv ? card.cvv : '***'}</p>
                        <button className="show-cvv-button" onClick={handleToggleCvv}>
                            {showCvv ? 'Hide CVV' : 'Show CVV'}
                        </button>
                    </div>
                </div>
                <div className="card-footer">
                    <p className="cardholder-name">{user.nameOnTheCard || 'Unknown'}</p> {/* Display user name on the card */}
                </div>
            </div>
            <div className="transactions-container">
                <button onClick={handleToggleStatus} className="toggle-status-button">
                    {isActive ? 'Disable Card' : 'Enable Card'}
                </button>
                <button 
                    onClick={handleToggleTransactions} 
                    className="show-transactions-button"
                >
                    {showTransactions ? 'Hide Transactions' : 'Show Transactions'}
                </button>
            </div>

            {showTransactions && (
                <div className="transactions">
                    <h4>Transactions(Top 10):</h4>
                    {loadingTransactions ? (
                        <p>Loading transactions...</p>
                    ) : (
                        <ul>
                            {transactions.length > 0 ? (
                                transactions.map((transaction) => (
                                    <li key={transaction.transactionId}>
                                        <Txn transaction={transaction} />
                                    </li>
                                ))
                            ) : (
                                <p>No transactions found for this card.</p>
                            )}
                        </ul>
                    )}
                </div>
            )}
        </div>
    );
};

export default CreditCard;
