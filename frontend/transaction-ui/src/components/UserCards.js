// src/components/UserCards.js
import React, { useState } from 'react';
import { users } from '../data/users'; // Import your users data
import CardTransactions from './CardTransactions'; // Import CardTransactions component
import CreditCard from './CreditCard'; // Import CreditCard component
import './UserCards.css'; // Import CSS for UserCards component

const UserCards = ({ userId }) => {
    const user = users.find(u => u._id === userId); // Find user by userId
    const [currentCard, setCurrentCard] = useState(null); // State to track the currently selected card

    const handleToggleTransactions = (card) => {
        if (currentCard && currentCard.creditCardId === card.creditCardId) {
            setCurrentCard(null); // Hide transactions
        } else {
            setCurrentCard(card); // Show transactions for the selected card
        }
    };

    return (
        <div className="user-cards-container">
            <div className="credit-cards">
                {user.creditcards.map(card => (
                    <CreditCard 
                        key={card.creditCardId} 
                        card={card} 
                        userId={userId} 
                        onToggleTransactions={handleToggleTransactions}
                        showTransactions={currentCard && currentCard.creditCardId === card.creditCardId}
                    />
                ))}
            </div>
            {currentCard && (
                <div className="transactions-container">
                    <CardTransactions 
                        transactions={currentCard.transactions} 
                        cardNumber={currentCard.cardNumber} 
                        cardType={currentCard.cardType} 
                        cardStatus={currentCard.status} 
                    />
                </div>
            )}
        </div>
    );
};

export default UserCards;
