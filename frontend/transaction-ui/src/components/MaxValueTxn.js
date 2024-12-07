import React from 'react';
import './MaxValueTxn.css'; // Import the CSS file for styling

const MaxValueTxn = ({ transaction }) => {
    return (
        <div className="txn">
            {/* Credit Card Number */}
            <div className="txn-id">
                <p className="txn-label">Credit Card:</p>
                <p className="txn-value">{transaction.credit_card}</p>
            </div>

            {/* Month */}
            <div className="txn-date">
                <p className="txn-label">Month:</p>
                <p className="txn-value">{transaction.month}</p>
            </div>

            {/* Amount */}
            <div className="txn-amount">
                <p className="txn-label">Amount:</p>
                <p className="txn-value">₹{transaction.amount ? transaction.amount.toLocaleString() : 'N/A'}</p>
            </div>

            {/* Separator */}
            <div className="separator"></div>
        </div>
    );
};

export default MaxValueTxn;
