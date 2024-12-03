// src/components/CreditCardContainer.js
import React, { useState } from 'react';
import AddCreditCard from './AddCreditCard'; // Import the AddCreditCard component
import CreditCard from './CreditCard'; // Import the CreditCard component
import './CreditCardContainer.css'; // Optional: Create a CSS file for styling

const CreditCardContainer = () => {
    const [cards, setCards] = useState([]); // State for storing credit cards
    const [showAddCard, setShowAddCard] = useState(false); // State to toggle the add card form

    const handleAddCard = (newCard) => {
        setCards([...cards, newCard]); // Add the new card to the list
        setShowAddCard(false); // Hide the add card form after submission
    };

    const handleToggleTransactions = (card) => {
        // Logic for toggling transactions can be implemented here
        console.log(`Toggle transactions for card: ${card.cardNumber}`);
    };

    return (
        <div className="credit-card-container">
            <button onClick={() => setShowAddCard(!showAddCard)}>
                {showAddCard ? 'Cancel' : 'Add Credit Card'}
            </button>
            {showAddCard && <AddCreditCard onAddCard={handleAddCard} />} {/* Ensure AddCreditCard is used here */}
            <div className="existing-cards">
                {cards.map((card, index) => (
                    <CreditCard
                        key={index}
                        card={card}
                        onToggleTransactions={handleToggleTransactions}
                        showTransactions={false} // You can manage this state as needed
                    />
                ))}
            </div>
        </div>
    );
};

export default CreditCardContainer;