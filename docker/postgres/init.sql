-- =====================================================
-- Multi-Warehouse Order Fulfillment System
-- PostgreSQL Database Initialization Script
-- =====================================================

SELECT 'CREATE DATABASE product_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'product_db')\gexec

SELECT 'CREATE DATABASE inventory_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'inventory_db')\gexec

SELECT 'CREATE DATABASE pricing_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'pricing_db')\gexec

SELECT 'CREATE DATABASE user_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'user_db')\gexec

SELECT 'CREATE DATABASE cart_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'cart_db')\gexec

SELECT 'CREATE DATABASE order_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'order_db')\gexec

SELECT 'CREATE DATABASE payment_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'payment_db')\gexec

SELECT 'CREATE DATABASE shipment_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'shipment_db')\gexec

SELECT 'CREATE DATABASE notification_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'notification_db')\gexec