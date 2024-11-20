// src/components/CreditCard.js
import React, { useState } from 'react';
import { users } from '../data/users'; // Ensure you're importing the users data
import './CreditCard.css'; // Import your CSS for styling

const getCardIcon = (cardType) => {
    switch (cardType) {
        case 'Visa':
            return require('../assets/icons/visa.png');
        case 'Master':
            return require('../assets/icons/mastercard.png');
        case 'Rupay':
            return require('../assets/icons/rupay.png');
        default:
            return require('../assets/icons/default.png');
    }
};

const getNameOnCard = (users, cardNumber) => {
    // Iterate through each user to find the card
    for (const user of users) {
        // Check if the user has credit cards
        if (user.creditcards) {
            // Find the card with the matching card number
            const card = user.creditcards.find(c => c.cardNumber === cardNumber);
            // If found, return the user's name on the card
            if (card) {
                return user.nameOnTheCard; // Return the cardholder name
            }
        }
    }
    return 'Card not found'; // Return not found message if no card matches
};

const getRandomGradient = (cardType) => {
    const colors = {
        visa: ['black', '#40E0D0'],
        master: ['#EB001B', '#FF8C00'],
        "american express": ['#0070BA', '#A7D3E0'],
        rupay: ['black', 'grey'],
        default: ['#CCCCCC', '#AAAAAA'],
    };
    const type = cardType ? cardType.toLowerCase() : 'default';
    const [color1, color2] = colors[type] || colors.default;
    return `linear-gradient(135deg, ${color1} 30%, ${color2} 100%)`;
};


const CreditCard = ({ card, onToggleTransactions, showTransactions }) => {
    const [showCvv, setShowCvv] = useState(false);
    const [isActive, setIsActive] = useState(card.status === 'active'); // Initialize based on card status

    const handleToggleCvv = (e) => {
        e.stopPropagation();
        setShowCvv(!showCvv);
    };

    const handleToggleStatus = () => {
        setIsActive(!isActive); // Toggle active status
    };

    const gradientBackground = getRandomGradient(card.cardType);
    const cardholderName = getNameOnCard(users, card.cardNumber);
    const cardStatusBackground = isActive ? 'green' : 'red';

    return (
        <div className="credit-card" style={{ background: gradientBackground }}>
            <div className="card-details">
                <img className="card-type" src={getCardIcon(card.cardType)} alt={card.cardType} />
                <div className="card-status" style={{ backgroundColor: cardStatusBackground }}>
                    {isActive ? 'Active' : 'Disabled'}
                </div>
                <p className="card-number">{card.cardNumber}</p>
                <div className="card-info">
                    <p className="expiry">Expiry: {card.expiry}</p>
                    <div className="cvv-container">
                        <p className="cvv">CVV: {showCvv ? card.cvv : '***'}</p>
                        <button className="show-cvv-button" onClick={handleToggleCvv}>
                            {showCvv ? 'Hide CVV' : 'Show CVV'}
                        </button>
                    </div>
                </div>
                <div className="card-footer">
                    <p className="cardholder-name">{cardholderName || 'Unknown'}</p>
                </div>
            </div>
            <div className="transactions-container">
                <button onClick={() => onToggleTransactions(card)} className="toggle-transactions-button">
                    {showTransactions ? 'Hide Transactions' : 'Show Transactions'}
                </button>
                {/* Button to toggle card status */}
                <button onClick={handleToggleStatus} className="toggle-status-button">
                    {isActive ? 'Disable Card' : 'Activate Card'}
                </button>
            </div>
        </div>
    );
};


export default CreditCard;
