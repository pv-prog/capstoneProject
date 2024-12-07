import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Toastify from 'toastify-js';
import './CreditCard.css'; // Import your CSS for styling
import Txn from './Txn'; // Import the Transaction component
import { FaEye, FaEyeSlash } from 'react-icons/fa';

const getCardIcon = (cardType) => {
    switch (cardType) {
        case 'visa':
            return require('../assets/icons/visa.png');
        case 'mastercard':
            return require('../assets/icons/mastercard.png');
        case 'rupay':
            return require('../assets/icons/rupay.png');
        default:
            return require('../assets/icons/default.png');
    }
};

const getRandomGradient = (cardType) => {
    const colors = {
        visa: ['black', '#40E0D0'],
        mastercard: ['#EB001B', '#FF8C00'],
        'american express': ['#0070BA', '#A7D3E0'],
        rupay: ['black', 'grey'],
        default: ['#CCCCCC', '#AAAAAA'],
    };
    const type = cardType ? cardType.toLowerCase() : 'default';
    const [color1, color2] = colors[type] || colors.default;
    return `linear-gradient(135deg, ${color1} 30%, ${color2} 100%)`;
};

const CreditCard = ({ card, user, isAddCard }) => {
    const [showFullNumber, setShowFullNumber] = useState(false);
    const [showCvv, setShowCvv] = useState(false);
    const [isActive, setIsActive] = useState(card.status === 'enabled');
    const [transactions, setTransactions] = useState([]);
    const [loadingTransactions, setLoadingTransactions] = useState(false);
    const [showTransactions, setShowTransactions] = useState(false);
    const [numTransactions, setNumTransactions] = useState(3); // Default to top 10
    const [filterType, setFilterType] = useState('all'); // Default filter is 'all'
    const [selectedCardNumber, setSelectedCardNumber] = useState(card.creditCardNumber); // Selected card number

    const handleToggleCvv = (e) => {
        e.stopPropagation();
        setShowCvv(!showCvv);
    };

    const handleToggleStatus = async () => {
        try {
            const response = await axios.put(`/api/customer/creditcard/togglecreditcard/${user.username}/${card.creditCardId}/toggle`);
            if (response.status === 200) {
                const newStatus = !isActive;
                setIsActive(newStatus);
                console.log(`${user.username} card ${card.creditCardNumber} ${newStatus ? 'Enabled' : 'Disabled'}`);
            }
        } catch (error) {
            const errorMessage = `Error: Activate/Disable Card, ${error.message}`;
            Toastify({
                text: `${errorMessage}`,
                duration: 3500,
                gravity: 'center',
                position: 'right',
                backgroundColor: '#FFEFD5',
                className: "large-toast",
            }).showToast();
            setIsActive(!isActive);
        }
    };

    const fetchTransactions = async () => {
        setLoadingTransactions(true);
        try {
            let url = '';
            switch (filterType) {
                case 'topX':
                    url = `/api/customer/transactions/lastXTransactions/${user.username}?limit=${numTransactions}&status=enabled`;
                    break;
                case 'maxValue':
                    url = `/api/customer/transactions/maxExpenses/lastMonth/${user.username}?status=both`;
                    break;
                case 'all':
                default:
                    url = `/api/customer/transactions/${user.username}`;
                    break;
                
            }
    
            console.log(`Fetching transactions for URL: ${url}`);
    
            const response = await axios.get(url);
            console.log('API Response:', response.data);
    
            // Explicitly handle each case based on the expected response structure
            if (filterType === 'topX' && response.data && response.data[card.creditCardId]) {
                console.log("Handling Case 1: Response keyed by creditCardId.");
                const transactionsForCard = response.data[card.creditCardId];
                setTransactions(transactionsForCard);
            } else if (filterType === 'all' && response.data && response.data.creditcards) {
                console.log("Handling Case 2: Response contains an array of creditcards.");
                const creditCard = response.data.creditcards.find(item => item.creditCardId === card.creditCardId);
                if (creditCard && creditCard.transactions) {
                    setTransactions(creditCard.transactions);
                } else {
                    console.log("No transactions found for the selected card in Case 2.");
                    setTransactions([]);
                }
            } else if (filterType === 'maxValue' && Array.isArray(response.data)) {
                console.log("Handling Case 3: Response is an array of cards with max transactions.");
                const selectedCardMaxTxn = response.data.find(item => {
                    const responseCardNumber = item.credit_card?.replace(/-/g, ''); // Clean card number
                    const selectedCardNumberCleaned = card.creditCardNumber?.replace(/-/g, ''); // Clean selected card number
                    console.log('Response Card:', responseCardNumber, 'Selected Card:', selectedCardNumberCleaned);
                    return responseCardNumber === selectedCardNumberCleaned; // Compare numbers
                });
    
                console.log("Selected Card Max Transaction:", selectedCardMaxTxn);
                if (selectedCardMaxTxn) {
                    setTransactions([selectedCardMaxTxn]);
                } else {
                    console.log("No matching card with max transaction found in Case 3.");
                    setTransactions([]);
                }
            } else {
                console.log("Handling Default Case: No valid data format matched.");
                setTransactions([]);
            }
        } catch (error) {
            console.error("Error fetching transactions:", error.message);
            Toastify({
                text: `Error fetching transactions: ${error.message}`,
                duration: 3500,
                gravity: 'center',
                position: 'right',
                backgroundColor: '#FFEFD5',
                className: "large-toast",
            }).showToast();
        } finally {
            setLoadingTransactions(false);
        }
    };
    
    
    
    

    const handleToggleTransactions = () => {
        if (!isActive) {
            Toastify({
                text: "Error: Transactions can only be shown for active cards.\n Enable the card to view transactions",
                duration: 3500,
                gravity: 'center',
                position: 'right',
                backgroundColor: '#FFEFD5',
                className: "large-toast",
            }).showToast();
            return;
        }

        setShowTransactions(!showTransactions);
        if (!showTransactions) {
            fetchTransactions();
        }
    };

    const handleTransactionFilterChange = (e) => {
        setFilterType(e.target.value);
    };

    useEffect(() => {
        if (showTransactions) {
            fetchTransactions();  // Trigger fetch when filter changes or when transactions are being displayed
        }
    }, [filterType, numTransactions, showTransactions]);

    // Mask the card number and show only the last 4 digits
    const maskedCardNumber = `**** **** **** ${card.creditCardNumber.slice(-4)}`;

    const toggleCardNumberVisibility = () => {
        setShowFullNumber((prev) => !prev);
    };

    useEffect(() => {
        if (showFullNumber || showCvv) {
            const timer = setTimeout(() => {
                setShowFullNumber(false); // Hide full number after 5 seconds
                setShowCvv(false); // Hide CVV after 5 seconds
            }, 5000);
    
            // Cleanup timer on unmount or when dependencies change
            return () => clearTimeout(timer);
        }
    }, [showFullNumber, showCvv]);
    
    const handleModalClose = () => {
        setShowTransactions(false); // Assuming you're using React state for `showTransactions`
    };
    
    const gradientBackground = getRandomGradient(card.wireTransactionVendor);
    const cardStatusBackground = isActive ? 'green' : 'red';

    return (
        <div className="card-wrapper">
            
            <div className="credit-card" style={{ background: gradientBackground }}>
                <div className="card-details">
                    <img className="card-type" src={getCardIcon(card.wireTransactionVendor)} alt={card.wireTransactionVendor} />
                    <div className="card-status" style={{ backgroundColor: cardStatusBackground }}>
                        {isActive ? 'Active' : 'Disabled'}
                    </div>
                    <div className="card-number">
                        {showFullNumber ? card.creditCardNumber : maskedCardNumber}
                        <button onClick={toggleCardNumberVisibility} className="eye-button">
                            {showFullNumber ? <FaEyeSlash /> : <FaEye />}
                        </button>
                    </div>
                    <div className="card-info">
                        <p className="expiry">Expiry: {card.expiryMonth}/{card.expiryYear}</p>
                        <div className="cvv-container">
                            <p className="cvv">CVV: {showCvv ? card.cvv : '***'}</p>
                            <button className="show-cvv-button" onClick={handleToggleCvv}>
                                {showCvv ? <FaEyeSlash /> : <FaEye />}
                            </button>
                        </div>
                    </div>
                    <div className="card-footer">
                        <p className="cardholder-name">{user.nameOnTheCard || 'Unknown'}</p>
                    </div>
                    <div className="card-buttons">
                        <button onClick={handleToggleStatus} className="toggle-status-button">
                            {isActive ? 'Disable Card' : 'Enable Card'}
                        </button>
                        <button onClick={handleToggleTransactions} className="toggle-transactions-button">
                            {showTransactions ? 'Hide Transactions' : 'Show Transactions'}
                        </button>
                    </div>
                </div>
            </div>
    
            {/* Transactions Modal */}
            {showTransactions && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        {/* Close Button */}
                        <button className="close-modal-button" onClick={handleModalClose}>
                            &times;
                        </button>
    
                    {/* Header */}
                    <h2>Transactions</h2>
                    <p>Card: {maskedCardNumber}</p>

    
                        {/* Filter Section */}
                        <div className="transactions-filter-container">
                            <label htmlFor="transaction-filter">Filter: </label>
                            <select id="transaction-filter" value={filterType} onChange={handleTransactionFilterChange}>
                                <option value="all">All Transactions</option>
                                <option value="topX">Top {numTransactions} Transactions</option>
                                <option value="maxValue">Max Transaction (Last Month)</option>
                            </select>
                        </div>
    
                        {/* Transactions List */}
                        <div className="transactions-list">
                            {loadingTransactions ? (
                                <p>Loading transactions...</p>
                            ) : transactions && transactions.length > 0 ? (
                                filterType === 'maxValue' ? (
                                    transactions
                                        .filter(
                                            (transaction) =>
                                                transaction.credit_card?.replace(/-/g, '') ===
                                                selectedCardNumber?.replace(/-/g, '')
                                        )
                                        .map((transaction, index) => (
                                            <li
                                                key={transaction.credit_card + index}
                                                style={{
                                                    padding: '10px',
                                                    marginBottom: '10px',
                                                    border: '1px solid #ccc',
                                                    borderRadius: '5px',
                                                    backgroundColor: '#f9f9f9',
                                                    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
                                                }}
                                            >
                                                <p>
                                                    <strong>Credit Card:</strong> **** **** ****{' '}
                                                    {transaction.credit_card.slice(-4)}
                                                </p>
                                                <p>
                                                    <strong>Month:</strong> {transaction.month}
                                                </p>
                                                <p>
                                                    <strong>Amount:</strong> $
                                                    {transaction.amount?.toLocaleString() || 'N/A'}
                                                </p>
                                            </li>
                                        ))
                                ) : (
                                    <ul>
                                        {transactions.map((transaction) => (
                                            <li key={transaction.transactionId}>
                                                <Txn transaction={transaction} />
                                            </li>
                                        ))}
                                    </ul>
                                )
                            ) : (
                                <p>No transactions found for this card.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
    
};

export default CreditCard;
