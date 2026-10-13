-- =====================================================================
-- Sadu Pedidos | 00 - Base de datos y usuario de aplicacion
-- Ejecutar UNA vez con la cuenta root en MySQL Workbench.
-- =====================================================================
CREATE DATABASE IF NOT EXISTS sadu_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- Usuario con permisos limitados: la aplicacion NUNCA se conecta con root.
-- Cambia la clave en tu maquina y ponla solo en ~/.sadu/db.properties
CREATE USER IF NOT EXISTS 'sadu_app'@'localhost'
  IDENTIFIED BY 'SaduDam2026';

GRANT SELECT, INSERT, UPDATE, DELETE
  ON sadu_db.*
  TO 'sadu_app'@'localhost';

FLUSH PRIVILEGES;
