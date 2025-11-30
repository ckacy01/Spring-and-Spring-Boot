-- INSERTS PARA USERS
INSERT INTO "users" ("name", "last_name", "email", "create_date", "active") VALUES
                                                                              ('Jorge', 'Avila', 'jorge@example.com', CURRENT_DATE, TRUE),
                                                                              ('Benjamin', 'Lopez', 'benjamin@example.com', CURRENT_DATE, TRUE),
                                                                              ('Jose', 'Perez', 'jose@example.com', CURRENT_DATE, TRUE);

-- INSERTS PARA PRODUCTS
INSERT INTO "products" ("name", "description", "price", "created_at", "is_active") VALUES
                                                                                   ('Laptop', 'Laptop Gamer XYZ', 1500.00, CURRENT_TIMESTAMP, TRUE),
                                                                                   ('Mouse', 'Mouse Inalámbrico', 25.00, CURRENT_TIMESTAMP, TRUE),
                                                                                   ('Teclado', 'Teclado Mecánico', 75.00, CURRENT_TIMESTAMP, TRUE);

-- INSERTS PARA ORDERS
INSERT INTO "orders" ("user_id", "created_at", "total", "active") VALUES
                                                                     (1, CURRENT_TIMESTAMP, 1600.00, TRUE),
                                                                     (2, CURRENT_TIMESTAMP, 100.00, TRUE);

-- INSERTS PARA ORDER_DETAILS
INSERT INTO "order_details" ("order_id", "product_id", "product_name", "description_snap", "quantity", "unit_price") VALUES
                                                                                                                      (1, 1, 'Laptop', 'Laptop Gamer XYZ', 1, 1500.00),
                                                                                                                      (1, 2, 'Mouse', 'Mouse Inalámbrico', 4, 25.00),
                                                                                                                      (2, 3, 'Teclado', 'Teclado Mecánico', 1, 75.00);
