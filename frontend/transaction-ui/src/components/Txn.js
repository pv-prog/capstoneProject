// Transaction.js
import React from 'react';
import './Txn.css'; // Import the CSS file for styling

const Txn = ({ transaction }) => {
    return (
        <div className={`txn ${transaction.transactionType === 'cr' ? 'credit' : 'debit'}`}>
            {/* Transaction ID */}
            <div className="txn-id">
                <p className="txn-label">TxnID:</p>
                <p className="txn-value">{transaction.transactionId}</p>
            </div>

            {/* Date and Time */}
            <div className="txn-date">
                <p className="txn-label">Date:</p>
                <p className="txn-value">{transaction.transactionDate}{transaction.transactionTime}</p>
            </div>

            

            {/* Transaction Type */}
            <div className="txn-type">
                <p className="txn-label">Type:</p>
                <p className="txn-value">{transaction.transactionType === 'cr' ? 'Credit' : 'Debit'}</p>
            </div>

            {/* Amount */}
            <div className="txn-amount">
                <p className="txn-label">Amount:</p>
                <p className="txn-value">₹{transaction.transactionAmount}</p>
            </div>

             {/* Amount */}
             <div className="txn-amount">
                <p className="txn-label">Desc:</p>
                <p className="txn-value">{transaction.transactionDesc}</p>
            </div>

            {/* Separator */}
            <div className="separator"></div>
        </div>
    );
};

export default Txn;
