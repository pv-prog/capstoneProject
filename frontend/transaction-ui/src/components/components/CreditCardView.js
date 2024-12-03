// src/components/CreditCardView.js
import PropTypes from 'prop-types'; // Import PropTypes for type checking
import React, { useState } from 'react';
import AddCreditCard from './AddCreditCard'; // Import the AddCreditCard component
import CreditCard from './CreditCard'; // Import the CreditCard component
import './CreditCardView.css'; // CSS for styling

const CreditCardView = ({ cards = [] }) => { // Set default value for cards
    const [showAddCard, setShowAddCard] = useState(false); // State to toggle the add card form

    const handleAddCard = (newCard) => {
        // Here you would typically also update the parent state
        // to include the new card in the cards array.
        // For now, we'll just hide the add card form.
        setShowAddCard(false); // Hide the add card form after submission
    };

    return (
        <div className="credit-card-view">
            {showAddCard && <AddCreditCard onAddCard={handleAddCard} />}
            <div className="cards-container">
                {cards.length === 0 ? (
                    <div className="no-cards-message">Add Credit Card</div>
                ) : (
                    cards.map((card, index) => (
                        <CreditCard key={index} card={card} />
                    ))
                )}
            </div>
            <div className="add-card-button" onClick={() => setShowAddCard(!showAddCard)}>
                <span className="plus-icon">+</span>
            </div>
        </div>
    );
};

// Add PropTypes for validation
CreditCardView.propTypes = {
    cards: PropTypes.array, // Expect cards to be an array
};

export default CreditCardView;