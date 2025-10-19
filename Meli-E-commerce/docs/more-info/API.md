# API Endpoints Documentation

## Overview

This document provides comprehensive documentation for all REST API endpoints available in the MeliECommerce application. The API follows RESTful conventions and returns standardized JSON responses.

**Base URL**: `http://localhost:8080/api`

**API Version**: 1.0.0

**Date**: October 19, 2025

## Table of Contents

- [Response Format](#response-format)
- [Error Handling](#error-handling)
- [User Endpoints](#user-endpoints)
- [Product Endpoints](#product-endpoints)
- [Order Endpoints](#order-endpoints)
- [Query Parameters](#query-parameters)
- [HTTP Status Codes](#http-status-codes)

---

## Response Format

### Success Response

All successful responses follow this standardized format:

```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Operation completed successfully",
  "data": {}
}
```

**Response Fields**:
- `timestamp` (string): ISO 8601 format datetime of response
- `status` (number): HTTP status code
- `message` (string): Human-readable success message
- `data` (object/array): Actual response payload

### Error Response

All error responses follow this standardized format:

```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Input validation failed",
  "path": "/api/user",
  "details": [
    "email: must be a valid email address",
    "name: must not be blank"
  ]
}
```

**Response Fields**:
- `timestamp` (string): ISO 8601 format datetime of error
- `status` (number): HTTP status code
- `error` (string): Error type/category
- `message` (string): Human-readable error message
- `path` (string): The API endpoint that was called
- `details` (array): Optional field validation details

---

## Error Handling

### Error Response Types

The API uses standard HTTP status codes and provides detailed error information:

| Status | Error Type | Description |
|--------|-----------|-------------|
| 200 | OK | Successful GET request |
| 201 | Created | Successful resource creation |
| 400 | Bad Request | Invalid input or validation error |
| 404 | Not Found | Resource does not exist |
| 500 | Internal Server Error | Server-side error |

### Common Error Scenarios

**Resource Not Found**
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: '999'",
  "path": "/api/user/999"
}
```

**Validation Error**
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Input validation failed",
  "path": "/api/user",
  "details": [
    "email: must be a valid email address",
    "name: must not be blank"
  ]
}
```

**Inactive Resource Operation**
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot perform operation on inactive Order with id: '1'",
  "path": "/api/orders/1"
}
```

---

## User Endpoints

### Get All Users

Retrieves all users (active and inactive by default).

```http
GET /api/user?activeOnly=false
```

**Query Parameters**:
- `activeOnly` (boolean, optional): Default `false`. Set to `true` to retrieve only active users.

**Example Request**:
```bash
curl http://localhost:8080/api/user?activeOnly=true
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Retrieved 3 active users successfully",
  "data": [
    {
      "id": 1,
      "name": "Jorge",
      "lastName": "Avila",
      "email": "jorge@example.com",
      "createDate": "2025-10-19",
      "active": true
    },
    {
      "id": 2,
      "name": "Benjamin",
      "lastName": "Lopez",
      "email": "benjamin@example.com",
      "createDate": "2025-10-19",
      "active": true
    }
  ]
}
```

---

### Get User by ID

Retrieves a specific user by their ID.

```http
GET /api/user/{id}
```

**Path Parameters**:
- `id` (long, required): The user's unique identifier

**Example Request**:
```bash
curl http://localhost:8080/api/user/1
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "User 1 retrieved successfully",
  "data": {
    "id": 1,
    "name": "Jorge",
    "lastName": "Avila",
    "email": "jorge@example.com",
    "createDate": "2025-10-19",
    "active": true
  }
}
```

**Error Response** (404 Not Found):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: '999'",
  "path": "/api/user/999"
}
```

---

### Create User

Creates a new user account.

```http
POST /api/user
Content-Type: application/json
```

**Request Body**:
```json
{
  "name": "Jorge",
  "lastName": "Avila",
  "email": "jorge@example.com"
}
```

**Request Fields**:
- `name` (string, required): User's first name
- `lastName` (string, required): User's last name
- `email` (string, required): User's email address (must be unique)

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/user \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jorge",
    "lastName": "Avila",
    "email": "jorge@example.com"
  }'
```

**Success Response** (201 Created):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 201,
  "message": "User 'Jorge Avila' created successfully with ID: 1",
  "data": {
    "id": 1,
    "name": "Jorge",
    "lastName": "Avila",
    "email": "jorge@example.com",
    "createDate": "2025-10-19",
    "active": true
  }
}
```

**Error Response** (400 Bad Request):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed",
  "path": "/api/user",
  "details": [
    "name: must not be blank",
    "email: must be a valid email address"
  ]
}
```

---

### Update User

Updates an existing user's information.

```http
PUT /api/user/{id}
Content-Type: application/json
```

**Path Parameters**:
- `id` (long, required): The user's ID

**Request Body**:
```json
{
  "name": "Jorge",
  "lastName": "Avila",
  "email": "jorge.avila@example.com",
  "active": true
}
```

**Request Fields**:
- `name` (string, required): Updated first name
- `lastName` (string, required): Updated last name
- `email` (string, required): Updated email address
- `active` (boolean, optional): User active status

**Example Request**:
```bash
curl -X PUT http://localhost:8080/api/user/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jorge",
    "lastName": "Avila",
    "email": "jorge.avila@example.com",
    "active": true
  }'
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "User 1 updated successfully",
  "data": {
    "id": 1,
    "name": "Jorge",
    "lastName": "Avila",
    "email": "jorge.avila@example.com",
    "createDate": "2025-10-19",
    "active": true
  }
}
```

---

### Delete User (Soft Delete)

Soft deletes a user by marking them as inactive.

```http
DELETE /api/user/{id}
```

**Path Parameters**:
- `id` (long, required): The user's ID

**Example Request**:
```bash
curl -X DELETE http://localhost:8080/api/user/1
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "User 1 has been successfully deactivated",
  "data": null
}
```

**Note**: The user record is not deleted from the database; instead, it is marked as inactive (`is_active = FALSE`). The user can be reactivated by setting `active: true` via the PUT endpoint.

---

## Product Endpoints

### Get All Products

Retrieves all products (active and inactive by default).

```http
GET /api/products?activeOnly=true
```

**Query Parameters**:
- `activeOnly` (boolean, optional): Default `true`. Set to `false` to retrieve all products including inactive ones.

**Example Request**:
```bash
curl http://localhost:8080/api/products?activeOnly=true
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Retrieved 5 active products successfully",
  "data": [
    {
      "id": 1,
      "name": "Laptop Gamer ASUS",
      "description": "Laptop con RTX 4060 y 16GB RAM",
      "price": 28999.99,
      "created_at": "09:15:30",
      "active": true
    },
    {
      "id": 2,
      "name": "iPhone 15 Pro",
      "description": "128GB, Titanio Azul",
      "price": 24999.99,
      "created_at": "09:15:31",
      "active": true
    }
  ]
}
```

---

### Get Product by ID

Retrieves a specific product by its ID.

```http
GET /api/products/{id}
```

**Path Parameters**:
- `id` (long, required): The product's unique identifier

**Example Request**:
```bash
curl http://localhost:8080/api/products/1
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Product 1 retrieved successfully",
  "data": {
    "id": 1,
    "name": "Laptop Gamer ASUS",
    "description": "Laptop con RTX 4060 y 16GB RAM",
    "price": 28999.99,
    "created_at": "09:15:30",
    "active": true
  }
}
```

---

### Create Product

Creates a new product.

```http
POST /api/products
Content-Type: application/json
```

**Request Body**:
```json
{
  "name": "MacBook Pro 16",
  "description": "M3 Max, 36GB Unified Memory, 512GB SSD",
  "price": 3499.99,
  "active": true
}
```

**Request Fields**:
- `name` (string, required): Product name
- `description` (string, optional): Detailed product description
- `price` (double, required): Product price
- `active` (boolean, optional): Product active status (default: true)

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16",
    "description": "M3 Max, 36GB Unified Memory, 512GB SSD",
    "price": 3499.99,
    "active": true
  }'
```

**Success Response** (201 Created):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 201,
  "message": "Product 'MacBook Pro 16' created successfully with ID: 6",
  "data": {
    "id": 6,
    "name": "MacBook Pro 16",
    "description": "M3 Max, 36GB Unified Memory, 512GB SSD",
    "price": 3499.99,
    "created_at": "14:30:45",
    "active": true
  }
}
```

---

### Update Product

Updates an existing product's information.

```http
PUT /api/products/{id}
Content-Type: application/json
```

**Path Parameters**:
- `id` (long, required): The product's ID

**Request Body**:
```json
{
  "name": "MacBook Pro 16",
  "description": "M3 Max, 36GB Unified Memory, 1TB SSD",
  "price": 3599.99,
  "active": true
}
```

**Example Request**:
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16",
    "description": "M3 Max, 36GB Unified Memory, 1TB SSD",
    "price": 3599.99,
    "active": true
  }'
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Product 1 updated successfully",
  "data": {
    "id": 1,
    "name": "MacBook Pro 16",
    "description": "M3 Max, 36GB Unified Memory, 1TB SSD",
    "price": 3599.99,
    "created_at": "14:30:45",
    "active": true
  }
}
```

---

### Delete Product (Soft Delete)

Soft deletes a product by marking it as inactive.

```http
DELETE /api/products/{id}
```

**Path Parameters**:
- `id` (long, required): The product's ID

**Example Request**:
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Product 1 has been successfully deactivated",
  "data": null
}
```

**Note**: The product is marked as inactive but remains in the database. Existing orders will retain product snapshots even if the product is deactivated.

---

## Order Endpoints

### Create Order

Creates a new order for a specific user.

```http
POST /api/orders/{userId}
Content-Type: application/json
```

**Path Parameters**:
- `userId` (long, required): The ID of the user creating the order

**Request Body**:
```json
[
  {
    "productId": 1,
    "quantity": 2
  },
  {
    "productId": 3,
    "quantity": 1
  }
]
```

**Request Fields** (Array of OrderDetailsDTO):
- `productId` (long, required): ID of the product to order
- `quantity` (int, required): Quantity of the product

**Example Request**:
```bash
curl -X POST http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '[
    {"productId": 1, "quantity": 2},
    {"productId": 3, "quantity": 1}
  ]'
```

**Success Response** (201 Created):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 201,
  "message": "Order created successfully with ID: 1",
  "data": {
    "id": 1,
    "userId": 1,
    "createdAt": "2025-10-19T14:30:45",
    "total": 65999.97,
    "details": [
      {
        "productId": 1,
        "productName": "Laptop Gamer ASUS",
        "descriptionSnap": "Laptop con RTX 4060 y 16GB RAM",
        "quantity": 2,
        "unitPrice": 28999.99
      },
      {
        "productId": 3,
        "productName": "Audífonos Sony WH-1000XM5",
        "descriptionSnap": "Cancelación de ruido premium",
        "quantity": 1,
        "unitPrice": 7499.99
      }
    ],
    "active": true
  }
}
```

**Note**: The total is automatically calculated. Product snapshots are created at order time.

---

### Get All Orders

Retrieves all orders or only active orders.

```http
GET /api/orders?activeOnly=true
```

**Query Parameters**:
- `activeOnly` (boolean, optional): Default `true`. Set to `false` to retrieve all orders.

**Example Request**:
```bash
curl http://localhost:8080/api/orders?activeOnly=true
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Retrieved 3 active orders successfully",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "createdAt": "2025-10-19T10:00:00",
      "total": 65999.97,
      "details": [
        {
          "productId": 1,
          "productName": "Laptop Gamer ASUS",
          "descriptionSnap": "Laptop con RTX 4060 y 16GB RAM",
          "quantity": 2,
          "unitPrice": 28999.99
        }
      ],
      "active": true
    }
  ]
}
```

---

### Get Orders by User

Retrieves all orders for a specific user.

```http
GET /api/orders/user/{userId}?activeOnly=true
```

**Path Parameters**:
- `userId` (long, required): The user's ID

**Query Parameters**:
- `activeOnly` (boolean, optional): Default `true`. Set to `false` to retrieve all user's orders.

**Example Request**:
```bash
curl http://localhost:8080/api/orders/user/1?activeOnly=true
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Retrieved 2 orders for user 1 successfully",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "createdAt": "2025-10-19T10:00:00",
      "total": 65999.97,
      "details": [],
      "active": true
    },
    {
      "id": 2,
      "userId": 1,
      "createdAt": "2025-10-19T11:30:00",
      "total": 8999.99,
      "details": [],
      "active": true
    }
  ]
}
```

---

### Get Order by ID

Retrieves a specific order with all its details.

```http
GET /api/orders/{id}
```

**Path Parameters**:
- `id` (long, required): The order's ID

**Example Request**:
```bash
curl http://localhost:8080/api/orders/1
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Order 1 retrieved successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "createdAt": "2025-10-19T10:00:00",
    "total": 65999.97,
    "details": [
      {
        "productId": 1,
        "productName": "Laptop Gamer ASUS",
        "descriptionSnap": "Laptop con RTX 4060 y 16GB RAM",
        "quantity": 2,
        "unitPrice": 28999.99
      },
      {
        "productId": 3,
        "productName": "Audífonos Sony WH-1000XM5",
        "descriptionSnap": "Cancelación de ruido premium",
        "quantity": 1,
        "unitPrice": 7499.99
      }
    ],
    "active": true
  }
}
```

---

### Update Order

Updates an existing active order with new order details.

```http
PUT /api/orders/{id}
Content-Type: application/json
```

**Path Parameters**:
- `id` (long, required): The order's ID

**Request Body**:
```json
[
  {
    "productId": 2,
    "quantity": 3
  },
  {
    "productId": 4,
    "quantity": 2
  }
]
```

**Example Request**:
```bash
curl -X PUT http://localhost:8080/api/orders/1 \
  -H "Content-Type: application/json" \
  -d '[
    {"productId": 2, "quantity": 3},
    {"productId": 4, "quantity": 2}
  ]'
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Order 1 updated successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "createdAt": "2025-10-19T10:00:00",
    "total": 93999.96,
    "details": [
      {
        "productId": 2,
        "productName": "iPhone 15 Pro",
        "descriptionSnap": "128GB, Titanio Azul",
        "quantity": 3,
        "unitPrice": 24999.99
      },
      {
        "productId": 4,
        "productName": "Monitor LG UltraWide 34''",
        "descriptionSnap": "Resolución 3440x1440, HDR10",
        "quantity": 2,
        "unitPrice": 8999.99
      }
    ],
    "active": true
  }
}
```

**Note**: The total is automatically recalculated. Old order details are replaced with new ones.

**Error Response** (400 Bad Request - Inactive Order):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Cannot perform operation on inactive Order with id: '1'",
  "path": "/api/orders/1"
}
```

---

### Delete Order (Soft Delete)

Soft deletes an order by marking it as inactive (cancelled).

```http
DELETE /api/orders/{id}
```

**Path Parameters**:
- `id` (long, required): The order's ID

**Example Request**:
```bash
curl -X DELETE http://localhost:8080/api/orders/1
```

**Success Response** (200 OK):
```json
{
  "timestamp": "2025-10-19T14:30:45",
  "status": 200,
  "message": "Order 1 has been successfully deactivated",
  "data": null
}
```

**Note**: The order is marked as inactive but the order details remain preserved in the database.

---

## Query Parameters

### Common Query Parameters

All GET endpoints support the `activeOnly` query parameter:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `activeOnly` | boolean | Varies | Filter to show only active resources |

**Users**: `activeOnly=false` (default retrieves all)
**Products**: `activeOnly=true` (default retrieves active only)
**Orders**: `activeOnly=true` (default retrieves active only)

### Example Queries

```bash
# Get only active products
GET /api/products?activeOnly=true

# Get all products including inactive
GET /api/products?activeOnly=false

# Get all users
GET /api/user?activeOnly=false

# Get only active users
GET /api/user?activeOnly=true
```

---

## HTTP Status Codes

### Success Codes

| Code | Name | Description |
|------|------|-------------|
| 200 | OK | Successful GET, PUT, or DELETE |
| 201 | Created | Successful POST (resource created) |

### Client Error Codes

| Code | Name | Description |
|------|------|-------------|
| 400 | Bad Request | Invalid input, validation error, or invalid operation (e.g., updating inactive resource) |
| 404 | Not Found | Requested resource does not exist |

### Server Error Codes

| Code | Name | Description |
|------|------|-------------|
| 500 | Internal Server Error | Unexpected server-side error |

---

## Best Practices

### Request Guidelines

1. **Always include Content-Type header** for POST/PUT requests:
   ```bash
   -H "Content-Type: application/json"
   ```

2. **Use appropriate HTTP methods**:
    - GET for retrieval
    - POST for creation
    - PUT for updates
    - DELETE for deactivation

3. **Handle errors gracefully** by checking response status codes

4. **Validate input** on the client side before sending requests

### Response Handling

1. **Check the status code** to determine success or failure
2. **Use the message field** for user feedback
3. **Use the data field** for actual response data
4. **Check details array** for validation errors

### Performance Tips

1. **Use activeOnly parameter** to reduce data payload
2. **Cache frequently accessed resources** (products, users)
3. **Implement pagination** for large result sets (future enhancement)
4. **Use specific product IDs** when available instead of fetching all

---

## API Versioning

Current API Version: **1.0.0**

The API is maintained at a single version. Breaking changes will result in a new major version.

---

For more information about the database schema, see [DATABASE.md](DATABASE.md)

For project overview and setup instructions, see [README.md](../../../README.md)