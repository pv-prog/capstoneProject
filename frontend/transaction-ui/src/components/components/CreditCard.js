// src/components/CreditCard.js
import axios from 'axios';
import React, { useState } from 'react';
import Toastify from 'toastify-js';
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

const AddCreditCard = ({ onAddCard }) => {
    const [cardNumber, setCardNumber] = useState('');
    const [cardType, setCardType] = useState('Visa');
    const [expiry, setExpiry] = useState('');
    const [cvv, setCvv] = useState('');
    const [nameOnCard, setNameOnCard] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!cardNumber || !expiry || !cvv || !nameOnCard) {
            Toastify({
                text: "Please fill in all fields.",
                duration: 3000,
                gravity: "top",
                position: "center",
                backgroundColor: "#FF6347",
            }).showToast();
            return;
        }
        onAddCard({ cardNumber, cardType, expiry, cvv, nameOnCard });
        setCardNumber('');
        setExpiry('');
        setCvv('');
        setNameOnCard('');
    };

    return (
        <div className="add-credit-card">
            <h2>Add Credit Card</h2>
            <form onSubmit={handleSubmit}>
                <input type="text" placeholder="Card Number" value={cardNumber} onChange={(e) => setCardNumber(e.target.value)} required />
                <select value={cardType} onChange={(e) => setCardType(e.target.value)}>
                    <option value="Visa">Visa</option>
                    <option value="Master">MasterCard</option>
                    <option value="Rupay">Rupay</option>
                </select>
                <input type="text" placeholder="Expiry (MM/YY)" value={expiry} onChange={(e) => setExpiry(e.target.value)} required />
                <input type="text" placeholder="CVV" value={cvv} onChange={(e) => setCvv(e.target.value)} required />
                <input type="text" placeholder="Name on Card" value={nameOnCard} onChange={(e) => setNameOnCard(e.target.value)} required />
                <button type="submit">Add Card</button>
            </form>
        </div>
    );
};


const CreditCard = ({ card, onToggleTransactions, showTransactions }) => {
    const [showCvv, setShowCvv] = useState(false);
    const [isActive, setIsActive] = useState(card.status === 'active'); // Initialize based on card status

    const handleToggleCvv = (e) => {
        e.stopPropagation();
        setShowCvv(!showCvv);
    };

    // const handleToggleStatus = () => {
    //    ; // Toggle active status
    // };

    const handleToggleStatus = async () => {
        try {
          // Make the API call to toggle the card status
          const response = await axios.put('/api/customer/creditcard/togglecreditcard/user1/2/toggle');
          console.log(response);
          
          // Handle the response accordingly
          if (response.status === 200) {
            setIsActive(!isActive);
          }
        } catch (error) {
          // Display the error message and code as a toast message
          const errorMessage = `Error: Activate/Disable Card, ${error.message}`;
          //const errorCode = `Error Code: ${error.code}`;
          Toastify({
            text: `${errorMessage}`,
            duration: 3500,
            gravity: 'center',
            position: 'right',
            backgroundColor: '#FFEFD5',
            className: "large-toast", // This changes the size to medium
          }).showToast();
          setIsActive(!isActive);
        }
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
