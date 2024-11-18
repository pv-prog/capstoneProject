// src/components/Transaction.js
import React from 'react';
import './Transaction.css'; // Import the CSS file for styling

const Transaction = ({ transaction }) => {
    return (
        <div className={`transaction ${transaction.transactionType === "cr" ? "credit" : "debit"}`}>
            <div className="transaction-details">
                <p className="transaction-id">Transaction ID: {transaction.transactionId}</p>
                <p className="transaction-date">{transaction.transactionDate}</p>
                <p className="transaction-time">{transaction.transactionTime}</p>
            </div>
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
