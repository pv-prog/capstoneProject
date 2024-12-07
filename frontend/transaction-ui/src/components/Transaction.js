import React from 'react';
import './Transaction.css'; // Import the CSS file for styling

const Transaction = ({ transaction, card }) => {

    if (!card || !card.creditCardNumber || !card.wireTransactionVendor) {
        return <p>Card data is missing</p>; // Display a message if card info is missing
    }

    return (
        <div className={`transaction ${transaction.transactionType === "cr" ? "credit" : "debit"}`}>
            {/* Transaction Header */}
            <div className="transaction-header">
                <h4>Card Number: **** **** **** {card.creditCardNumber.slice(-4)}</h4> {/* Display Card Number */}
                

                <p>Type: {card.wireTransactionVendor.toUpperCase()}</p> {/* Display Vendor in uppercase */}
            </div>

            {/* Transaction Details */}
            <div className="transaction-details">
                <p className="transaction-id">Transaction ID: {transaction.transactionId}</p>
                <p className="transaction-date">{transaction.transactionDate}</p>
                <p className="transaction-time">{transaction.transactionTime}</p>
                <p className="transaction">Category: {transaction.transactionDesc}</p>      
            </div>

            {/* Transaction Summary */}
            <div className="transaction-summary">
                <p className="transaction-type">{transaction.transactionType === "cr" ? "Credit" : "Debit"}</p>
                <p className="transaction-amount">
                    {transaction.transactionType === "cr" ? '+' : '-'} ₹{parseFloat(transaction.transactionAmount).toFixed(2)}
                </p>
            </div>
        </div>
    );
};

export default Transaction;
