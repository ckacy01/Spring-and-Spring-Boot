# Database Documentation

## Overview

The MeliECommerce database follows a relational model with four interconnected tables. The design implements **soft deletion logic** to maintain referential integrity and historical data. Additionally, the system captures product information snapshots at the time of order creation to preserve historical pricing and descriptions.

## Database Architecture

### Database Selection

- **Development & Testing**: H2 (embedded, in-memory)
- **Production**: PostgreSQL 12+

### Key Design Principles

1. **Soft Deletion**: Records are marked as inactive rather than deleted
2. **Referential Integrity**: Foreign key constraints maintain data consistency
3. **Product Snapshots**: Product data is captured at order time for historical accuracy
4. **Audit Trail**: Creation timestamps track when records were added to the system
5. **Logical Relationships**: Clear relationships between users, products, and orders

## Entity Relationship Diagram

```
┌─────────────┐           ┌──────────────┐
│    USERS    │           │   PRODUCTS   │
├─────────────┤           ├──────────────┤
│ id (PK)     │           │ id (PK)      │
│ name        │           │ name         │
│ last_name   │           │ description  │
│ email       │           │ price        │
│ created_at  │           │ created_at   │
│ is_active   │           │ is_active    │
└──────┬──────┘           └──────┬───────┘
       │                         │
       │ 1:N                     │ M:N
       │                         │
       └──────────┬──────────────┘
                  │
           ┌──────▼──────┐
           │   ORDERS    │
           ├─────────────┤
           │ id (PK)     │
           │ user_id (FK)│
           │ created_at  │
           │ total       │
           │ is_active   │
           └──────┬──────┘
                  │
                  │ 1:N
                  │
           ┌──────▼────────────────┐
           │  ORDER_DETAILS        │
           ├───────────────────────┤
           │ id (PK)               │
           │ order_id (FK)         │
           │ product_id (FK)       │
           │ product_name (snap)   │
           │ description_snap      │
           │ quantity              │
           │ unit_price (snap)     │
           └───────────────────────┘
```

## Table Specifications

### 1. USERS Table

Stores information about customers in the e-commerce system.

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);
```

#### Columns

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `name` | VARCHAR(255) | NOT NULL | User's first name |
| `last_name` | VARCHAR(255) | NOT NULL | User's last name |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | User's email address (unique) |
| `created_at` | DATE | NOT NULL, DEFAULT CURRENT_DATE | Account creation date |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT TRUE | Soft delete flag (false = inactive/deleted) |

#### Indexes
- PRIMARY KEY: `id`
- UNIQUE: `email`

#### Relationships
- One user can have many orders (1:N relationship with ORDERS)

---

### 2. PRODUCTS Table

Stores product information available for purchase.

```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);
```

#### Columns

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique product identifier |
| `name` | VARCHAR(255) | NOT NULL | Product name |
| `description` | TEXT | NULLABLE | Detailed product description |
| `price` | DECIMAL(10, 2) | NOT NULL | Current product price |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Product creation timestamp |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT TRUE | Soft delete flag (false = inactive/deleted) |

#### Indexes
- PRIMARY KEY: `id`

#### Relationships
- One product can appear in many orders (through ORDER_DETAILS)
- Products can be referenced by multiple orders even after deactivation

---

### 3. ORDERS Table

Tracks customer orders and their associated information.

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(12, 2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

#### Columns

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique order identifier |
| `user_id` | BIGINT | NOT NULL, FOREIGN KEY | Reference to ordering user |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Order creation timestamp |
| `total` | DECIMAL(12, 2) | NOT NULL | Order total amount (calculated) |
| `is_active` | BOOLEAN | NOT NULL, DEFAULT TRUE | Soft delete flag (false = cancelled/deleted) |

#### Indexes
- PRIMARY KEY: `id`
- FOREIGN KEY: `user_id` → USERS(id)

#### Relationships
- Many orders belong to one user (N:1 with USERS)
- One order can have multiple order details (1:N with ORDER_DETAILS)

---

### 4. ORDER_DETAILS Table

Contains line items for each order, with product snapshots at order time.

```sql
CREATE TABLE order_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    description_snap TEXT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

#### Columns

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique order detail identifier |
| `order_id` | BIGINT | NOT NULL, FOREIGN KEY | Reference to parent order |
| `product_id` | BIGINT | NOT NULL, FOREIGN KEY | Reference to ordered product |
| `product_name` | VARCHAR(255) | NOT NULL | **Snapshot** of product name at order time |
| `description_snap` | TEXT | NULLABLE | **Snapshot** of product description at order time |
| `quantity` | INT | NOT NULL | Quantity of items ordered |
| `unit_price` | DECIMAL(10, 2) | NOT NULL | **Snapshot** of product price at order time |

#### Indexes
- PRIMARY KEY: `id`
- FOREIGN KEY: `order_id` → ORDERS(id) (CASCADE DELETE)
- FOREIGN KEY: `product_id` → PRODUCTS(id)

#### Relationships
- Many order details belong to one order (N:1 with ORDERS)
- Many order details reference products (N:1 with PRODUCTS)

---

## Data Integrity & Constraints

### Foreign Key Constraints

```sql
ALTER TABLE orders 
ADD CONSTRAINT fk_orders_user_id 
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE order_details 
ADD CONSTRAINT fk_order_details_order_id 
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE order_details 
ADD CONSTRAINT fk_order_details_product_id 
FOREIGN KEY (product_id) REFERENCES products(id);
```

### Cascade Rules

- **Orders → Order Details**: CASCADE DELETE (deleting an order removes all order details)
- **Users → Orders**: RESTRICT (cannot delete user with existing orders)
- **Products → Order Details**: RESTRICT (cannot delete product with existing order details)

---

## Soft Delete Logic

### Implementation Strategy

Instead of physically removing records with `DELETE` statements, records are marked as inactive using the `is_active` boolean flag.

### Benefits

1. **Data Preservation**: Historical data remains in the database
2. **Audit Trails**: Maintains complete transaction history
3. **Referential Integrity**: Foreign key constraints remain valid
4. **Recovery**: Deleted records can be reactivated if needed
5. **Reporting**: Historical analysis possible with deleted records

### Usage

**Marking a user as inactive:**
```sql
UPDATE users SET is_active = FALSE WHERE id = 1;
```

**Querying only active records:**
```sql
SELECT * FROM users WHERE is_active = TRUE;
```

**Querying including inactive records:**
```sql
SELECT * FROM users; -- Returns all records
```

### Application-Level Soft Delete

The application uses the `@service` layer to handle soft deletes:

```java
public void deleteUser(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    user.setActive(false);
    userRepository.save(user);
}
```

---

## Product Snapshot Mechanism

### Why Snapshots?

Products can be modified or deactivated after an order is placed. Snapshots preserve the exact product information at order time, ensuring:

1. **Accurate Historical Records**: Invoices and receipts show what was ordered
2. **Price History**: Captures exact pricing at purchase time
3. **Audit Compliance**: Meets regulatory requirements
4. **Dispute Resolution**: Definitive record of what was sold

### Snapshot Fields in ORDER_DETAILS

| Field | Captured From | Purpose |
|-------|---------------|---------|
| `product_name` | PRODUCTS.name | Preserve product name |
| `description_snap` | PRODUCTS.description | Preserve product description |
| `unit_price` | PRODUCTS.price | Preserve price at purchase time |

### Snapshot Creation Example

When an order is created:

```java
OrderDetails detail = new OrderDetails();
detail.setProduct(product);
detail.setProductName(product.getName());           // Snapshot
detail.setDescriptionSnap(product.getDescription()); // Snapshot
detail.setUnitPrice(product.getPrice());             // Snapshot
detail.setQuantity(quantity);
```

### Impact of Product Changes

If a product is later updated:
- **New orders**: Use the new product information
- **Existing order details**: Retain original snapshots (unchanged)

```
Product name changes: "Laptop" → "Gaming Laptop"
├─ Order 1 (before): Shows "Laptop"
├─ Order 2 (after): Shows "Gaming Laptop"
└─ Order_Details snapshots preserve original names
```

---

## Database Access Patterns

### Common Queries

**Get all active users:**
```sql
SELECT * FROM users WHERE is_active = TRUE ORDER BY created_at DESC;
```

**Get user's orders with order details:**
```sql
SELECT o.id, o.created_at, o.total, od.product_name, od.quantity, od.unit_price
FROM orders o
JOIN order_details od ON o.id = od.order_id
WHERE o.user_id = ? AND o.is_active = TRUE
ORDER BY o.created_at DESC;
```

**Calculate total revenue (active orders only):**
```sql
SELECT SUM(total) as revenue FROM orders WHERE is_active = TRUE;
```

**Find orders for a specific product:**
```sql
SELECT DISTINCT o.* 
FROM orders o
JOIN order_details od ON o.id = od.order_id
WHERE od.product_id = ? AND o.is_active = TRUE;
```

---

## Performance Considerations

### Indexes for Common Queries

Recommended indexes for optimal performance:

```sql
-- User queries
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);

-- Product queries
CREATE INDEX idx_products_active ON products(is_active);

-- Order queries
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_active ON orders(is_active);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Order details queries
CREATE INDEX idx_order_details_order_id ON order_details(order_id);
CREATE INDEX idx_order_details_product_id ON order_details(product_id);
```

---

## Migration & Setup

### H2 Database (Development/Testing)

H2 automatically creates tables on application startup using Hibernate's `ddl-auto: create-drop`.

### PostgreSQL (Production)

Manual schema setup:

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE melie_commerce;

```

---

## Backup & Recovery

### Backup Strategy

```bash
# PostgreSQL backup
pg_dump -U postgres melie_commerce > backup.sql

# Restore backup
psql -U postgres melie_commerce < backup.sql
```

### Data Recovery from Soft Deletes

Since soft deletes only set `is_active = FALSE`, recovery is simple:

```sql
-- Restore deleted user
UPDATE users SET is_active = TRUE WHERE id = 1;

-- Restore deleted order
UPDATE orders SET is_active = TRUE WHERE id = 1;
```

---

## Entity Relationship with Application

### JPA Entity Mappings

**User Entity:**
```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private List<Order> orders;
```

**Order Entity:**
```java
@ManyToOne
@JoinColumn(name = "user_id", nullable = false)
private User user;

@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
private List<OrderDetails> details;
```

**OrderDetails Entity:**
```java
@ManyToOne
@JoinColumn(name = "order_id", nullable = false)
private Order order;

@ManyToOne
@JoinColumn(name = "product_id", nullable = false)
private Product product;
```

---

## Future Considerations

- **Read Replicas**: PostgreSQL replication for read scaling
- **Column Encryption**: Encrypt sensitive customer data

---

*For API documentation on how to interact with this database through endpoints, see [API.md](API.md)*