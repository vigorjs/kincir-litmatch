-- Users table
CREATE TABLE IF NOT EXISTS public.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- Stores table
CREATE TABLE IF NOT EXISTS public.stores (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    store_name VARCHAR(100) NOT NULL,
    store_description TEXT,
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE
);

-- Products table
CREATE TABLE IF NOT EXISTS public.products (
    id SERIAL PRIMARY KEY,
    store_id INT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_description TEXT,
    price BIGINT NOT NULL,
    FOREIGN KEY (store_id) REFERENCES public.stores(id) ON DELETE CASCADE
);

-- Stocks table
CREATE TABLE IF NOT EXISTS public.stocks (
     id SERIAL PRIMARY KEY,
     products_id INT NOT NULL,
     quantity INT,
     FOREIGN KEY (products_id) REFERENCES public.products(id) ON DELETE CASCADE
);

-- Transactions table
CREATE TABLE IF NOT EXISTS public.transactions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    order_id VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    gross_amount BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE SET NULL
);

-- Transaction_details table
CREATE TABLE IF NOT EXISTS public.transaction_details (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    transaction_id INT NOT NULL,
    amount BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES public.products(id) ON DELETE SET NULL,
    FOREIGN KEY (transaction_id) REFERENCES public.transactions(id) ON DELETE SET NULL
);