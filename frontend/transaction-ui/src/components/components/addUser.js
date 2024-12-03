const fs = require('fs');
const path = require('path');
const prompt = require('prompt-sync')();

const usersFilePath = path.join(__dirname, 'data', 'users.js');

// Function to read existing users data
function readUsers() {
    const data = fs.readFileSync(usersFilePath, 'utf8');
    return JSON.parse(data);
}

// Function to write updated users data
function writeUsers(users) {
    fs.writeFileSync(usersFilePath, JSON.stringify(users, null, 2), 'utf8');
}

// Function to create a new user object
function createNewUser() {
    const newUser = {};

    // Get user input
    newUser._id = prompt('Enter user ID: ');
    newUser.username = prompt('Enter username: ');
    newUser.nameOnTheCard = prompt('Enter name on the card: ');

    newUser.creditcards = [];

    // Get number of credit cards to add
    const numberOfCards = parseInt(prompt('How many credit cards to add? '), 10);

    for (let i = 0; i < numberOfCards; i++) {
        const creditCard = {};
        creditCard.creditCardId = i + 1; // Assuming ID is incremental
        creditCard.cardNumber = prompt(`Enter card number for card ${i + 1}: `);
        creditCard.expiry = prompt(`Enter expiry date for card ${i + 1} (MM/YY): `);
        creditCard.cvv = prompt(`Enter CVV for card ${i + 1}: `);
        creditCard.cardType = prompt(`Enter card type for card ${i + 1}: `);
        creditCard.status = prompt(`Enter status for card ${i + 1} (active/disabled): `);
        creditCard.transactions = []; // Initialize with no transactions

        newUser.creditcards.push(creditCard);
    }

    return newUser;
}

// Main function to add user
function addUser() {
    const users = readUsers();
    const newUser = createNewUser();
    users.push(newUser);
    writeUsers(users);
    console.log('New user added successfully!');
}

// Execute the function
addUser();
