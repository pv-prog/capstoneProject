# API contract between the server and front-end devices
Authorization Roles/Permissions: Must be logged in. To complete this operation successfully.
----------------
Customer Object
```
{
    "id": "6739e4e77544f601a61c7129",
    "username": "achilleyb",
    "name": {
        "first": "Maurita",
        "last": "Baden"
    },
    "dob": "10/16/2007",
    "sex": "Female",
    "email": "mbaden19@guardian.co.uk",
    "customerId": 76795327,
    "address": {
        "street": "3781 Clemons Park",
        "city": "Oklahoma City",
        "state": "Oklahoma",
        "zip": 73129,
        "country": "United States"
    },
    "active": true,
    "createdAt": "2024-10-26T18:30:00.000+00:00"
}
```
CreditCard Object
```
{
    "id": "6739ee447544f601a61c7ce8",
    "username": "achilleyb",
    "nameOnTheCard": "Baldwin Mewhirter",
    "creditcards": [
        {
            "creditCardId": 1,
            "creditCardNumber": "5108758767108221",
            "expiryMonth": 11,
            "expiryYear": 25,
            "cvv": 542,
            "wireTransactionVendor": "rupay",
            "status": "enabled"
        }]
}
```
Transaction Object
```
{
    "id": "6739f28c7544f601a61c80ab",
    "username": "achilleyb",
    "creditcards": [
        {
            "creditCardId": 1,
            "transactions": [
                {
                    "transactionId": "7.884806127E9",
                    "transactionDate": "2024-06-13T18:30:00.000+00:00",
                    "transactionTime": "8:15 AM",
                    "transactionType": "cr",
                    "transactionAmount": 240082.03
                    "transactionDesc": "Clothing"
                }]
}]
}
```
## HTTP Method
### 1. User Story: When customer wants to view last month maximum expenses of all the cards
### GET /api/customer/transactions/maxExpenses/lastMonth
### Url
```
http://{hostname}/api/customer/transactions/maxExpenses/lastMonth/{Base64-Encoded-username}
```
**URL Params:** Base64-Encoded-username

**Success Response:**

Code: 200
Content: 
```
{
  "content": [
    {
      "credit_card": "****-****-****-1230",
      "month": "DEC",
      "amount": 6872.46
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 100,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 100,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}

```
### 2. User Story: list the expenses more than the amount of expenses
### GET /api/customer/transactions/highvalue/expenses
### Url
```
http://{hostname}/api/customer/transactions/highvalue/expenses/{Base64-Encoded-username}?limit={limit}&status={status}&amountThreshold={amountThreshold}
```
**URL Params:** Base64-Encoded-username , limit, status, amountThreshold

**Success Response:**

Code: 200
Content:
```
{
  "1": [
    {
      "transactionId": 1013283302,
      "transactionDate": "12/10/2024",
      "transactionTime": "5:54 AM",
      "transactionType": "cr",
      "transactionAmount": 6872.46,
      "transactionDesc": "Electronics"
    },
    {
      "transactionId": 8174323512,
      "transactionDate": "11/20/2024",
      "transactionTime": "2:45 PM",
      "transactionType": "cr",
      "transactionAmount": 3428.97,
      "transactionDesc": "Food"
    }],
  "2": [
    {
      "transactionId": 4011816105,
      "transactionDate": "11/15/2024",
      "transactionTime": "1:00 PM",
      "transactionType": "cr",
      "transactionAmount": 1235.92,
      "transactionDesc": "Food"
    }
  ]
}
```
### 3. User Story: add a credit card to the customer - valid case
### POST /api/customer/creditcard/{username}
### Url
```
http://{hostname}/api/customer/creditcard/{Base64-Encoded-username}
```
**URL Params:** Base64-Encoded-username

**Request Params:** creditCardNumber , expiryMonth , expiryYear , cvv , wireTransactionVendor

**Request body:**
```
{
    "creditCardId" : 9,
    "creditCardNumber" : "843359134421",
    "expiryMonth" : 8,
    "expiryYear" : 29,
    "cvv" : 654,
    "wireTransactionVendor" : "visa",
    "status" : "enabled"
}
```
**Success Response:**

Code: 200
Content:
```
{
    "id": "6739ee447544f601a61c7ce8",
    "username": "achilleyb",
    "nameOnTheCard": "Baldwin Mewhirter",
    "creditcards": [
       - - - - - - - -
        {
            "creditCardId": 9,
            "creditCardNumber": "843359134421",
            "expiryMonth": 8,
            "expiryYear": 29,
            "cvv": 654,
            "wireTransactionVendor": "visa",
            "status": "enabled"
        }
    ]
}
```
### 4. User Story: when customer wants to view last x number of expenses of all cards
### GET /api/customer/transactions/lastXExpenses/{Base64-Encoded-username}
### Url
```
http://{hostname}/api/customer/transactions/lastXExpenses/achilleyb?limit={limit}&status=both
```
**URL Params:** Base64-Encoded-username , limit value

**Success Response:**

Code: 200
Content:
```
{
  "content": [
    {
      "credit_card": "****-****-****-1230",
      "transactions": [
        {
          "month": "DEC",
          "amount": 6872.46,
          "description": "Electronics"
        }
      ]
    },
    {
      "credit_card": "****-****-****-5692",
      "transactions": [
        {
          "month": "NOV",
          "amount": 1235.92,
          "description": "Food"
        }
      ]
    },
    {
      "credit_card": "****-****-****-1213",
      "transactions": [
        {
          "month": "NOV",
          "amount": 125.92,
          "description": "Food"
        }
      ]
    }
  ],
  "totalElements": 3,
  "totalPages": 1,
  "pageable": {
    "pageSize": 100,
    "pageNumber": 0,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  }
}
```
