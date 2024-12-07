import React, { useState } from 'react';
import Txn from './Txn';
import './PopupTxn.css'; // Styling for the pop-up

const PopupTxn = ({ transactions, isOpen, onClose }) => {
    const [filter, setFilter] = useState('');
    const [filteredTransactions, setFilteredTransactions] = useState(transactions);

    const handleFilterChange = (e) => {
        const value = e.target.value.toLowerCase();
        setFilter(value);
        const filtered = transactions.filter(transaction => 
            transaction.transactionType.toLowerCase().includes(value) ||
            transaction.transactionDesc.toLowerCase().includes(value)
        );
        setFilteredTransactions(filtered);
    };

    if (!isOpen) return null;

    return (
        <div className="popup-overlay">
            <div className="popup-content">
                <button className="close-btn" onClick={onClose}>X</button>
                <h2>Transaction Details</h2>
                
                {/* Filter Input */}
                <div className="filter-section">
                    <input
                        type="text"
                        placeholder="Filter by type or description..."
                        value={filter}
                        onChange={handleFilterChange}
                        className="filter-input"
                    />
                </div>

                {/* Transaction List */}
                <div className="transactions-list">
                    {filteredTransactions.map(transaction => (
                        <Txn key={transaction.transactionId} transaction={transaction} />
                    ))}
                </div>
            </div>
        </div>
    );
};

export default PopupTxn;
