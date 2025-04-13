-- Create both databases for app and Keycloak
CREATE DATABASE skilltrack;
CREATE DATABASE keycloak;

-- Ensure uuid extension is available in skilltrack DB (where Flyway will run)
\connect skilltrack
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
