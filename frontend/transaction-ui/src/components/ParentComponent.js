import React, { useState } from 'react';
import CreditCard from './CreditCard';

const ParentComponent = ({ cards, user }) => {
    const [activeCardId, setActiveCardId] = useState(null); // Track the card with open transactions

    // This function will close the currently open transactions when another card is clicked
    const handleToggleTransactions = (cardId) => {
        setActiveCardId((prevId) => (prevId === cardId ? null : cardId)); // Toggle the active card
    };

    return (
        <div className="credit-card-list">
            {cards.map((card) => (
                <CreditCard
                    key={card.creditCardId}
                    card={card}
                    user={user}
                    activeCardId={activeCardId}
                    setActiveCardId={setActiveCardId}
                    handleToggleTransactions={handleToggleTransactions}
                />
            ))}
        </div>
    );
};

export default ParentComponent;
