// src/components/UserCreditCards.js
import React from 'react';
import CreditCard from './CreditCard'; // Import CreditCard component

const UserCreditCards = ({ user }) => {
    return (
        <div>
            <h3>Your Credit Cards</h3>
            
            {user.creditcards.map((card) => (
                <CreditCard key={card.creditCardId} card={card} /> // Render each credit card
            ))}
        </div>
    );
};

export default UserCreditCards;
