-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: proyectofinal
-- ------------------------------------------------------
-- Server version	8.0.35

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accesorio`
--

DROP TABLE IF EXISTS `accesorio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `accesorio` (
  `codigo` int NOT NULL,
  `nombre` varchar(60) DEFAULT NULL,
  `tarifa` decimal(10,2) DEFAULT NULL,
  `precio_reposicion` decimal(10,2) DEFAULT NULL,
  `existencia` int DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accesorio`
--

LOCK TABLES `accesorio` WRITE;
/*!40000 ALTER TABLE `accesorio` DISABLE KEYS */;
INSERT INTO `accesorio` VALUES (1,'Casco',5.00,150.00,18),(2,'Candado',3.00,80.00,4),(3,'Canasta',4.00,100.00,10),(4,'Luces LED',2.00,60.00,5);
/*!40000 ALTER TABLE `accesorio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alquiler`
--

DROP TABLE IF EXISTS `alquiler`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alquiler` (
  `id_alquiler` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  `hora_estimada` datetime DEFAULT NULL,
  `hora_entrega` datetime DEFAULT NULL,
  `hora_devolucion` datetime DEFAULT NULL,
  `deposito` decimal(10,2) DEFAULT NULL,
  `recargo` decimal(10,2) DEFAULT NULL,
  `reposicion` decimal(10,2) DEFAULT NULL,
  `total` decimal(10,2) DEFAULT NULL,
  `deposito_devuelto` decimal(10,2) DEFAULT NULL,
  `saldo_generado` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id_alquiler`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `alquiler_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alquiler`
--

LOCK TABLES `alquiler` WRITE;
/*!40000 ALTER TABLE `alquiler` DISABLE KEYS */;
INSERT INTO `alquiler` VALUES (1,1001,'FINALIZADO','2026-11-05 11:00:00','2026-11-05 09:00:00','2026-11-05 11:00:00',100.00,0.00,0.00,35.00,100.00,0.00),(2,1002,'ACTIVO','2026-11-09 13:00:00','2026-11-09 09:00:00',NULL,200.00,0.00,0.00,0.00,0.00,0.00),(3,1004,'RESERVADO','2026-11-09 16:00:00',NULL,NULL,0.00,0.00,0.00,0.00,0.00,0.00),(4,1003,'FINALIZADO','2026-11-03 12:00:00','2026-11-03 09:00:00','2026-11-03 13:00:00',100.00,37.50,3000.00,3137.50,0.00,2900.00);
/*!40000 ALTER TABLE `alquiler` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alquiler_accesorio`
--

DROP TABLE IF EXISTS `alquiler_accesorio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alquiler_accesorio` (
  `id_alquiler_accesorio` int NOT NULL AUTO_INCREMENT,
  `id_alquiler` int DEFAULT NULL,
  `codigo_accesorio` int DEFAULT NULL,
  `cantidad` int DEFAULT NULL,
  `extraviados` int DEFAULT NULL,
  PRIMARY KEY (`id_alquiler_accesorio`),
  KEY `id_alquiler` (`id_alquiler`),
  KEY `codigo_accesorio` (`codigo_accesorio`),
  CONSTRAINT `alquiler_accesorio_ibfk_1` FOREIGN KEY (`id_alquiler`) REFERENCES `alquiler` (`id_alquiler`),
  CONSTRAINT `alquiler_accesorio_ibfk_2` FOREIGN KEY (`codigo_accesorio`) REFERENCES `accesorio` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alquiler_accesorio`
--

LOCK TABLES `alquiler_accesorio` WRITE;
/*!40000 ALTER TABLE `alquiler_accesorio` DISABLE KEYS */;
INSERT INTO `alquiler_accesorio` VALUES (1,1,1,1,0),(2,2,1,1,0),(3,3,3,1,0);
/*!40000 ALTER TABLE `alquiler_accesorio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alquiler_equipo`
--

DROP TABLE IF EXISTS `alquiler_equipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alquiler_equipo` (
  `id_alquiler_equipo` int NOT NULL AUTO_INCREMENT,
  `id_alquiler` int DEFAULT NULL,
  `codigo_equipo` int DEFAULT NULL,
  `estado_devolucion` varchar(20) DEFAULT NULL,
  `horas` int DEFAULT NULL,
  PRIMARY KEY (`id_alquiler_equipo`),
  KEY `id_alquiler` (`id_alquiler`),
  KEY `codigo_equipo` (`codigo_equipo`),
  CONSTRAINT `alquiler_equipo_ibfk_1` FOREIGN KEY (`id_alquiler`) REFERENCES `alquiler` (`id_alquiler`),
  CONSTRAINT `alquiler_equipo_ibfk_2` FOREIGN KEY (`codigo_equipo`) REFERENCES `equipo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alquiler_equipo`
--

LOCK TABLES `alquiler_equipo` WRITE;
/*!40000 ALTER TABLE `alquiler_equipo` DISABLE KEYS */;
INSERT INTO `alquiler_equipo` VALUES (1,1,101,'BUENO',2),(2,2,301,NULL,0),(3,3,201,NULL,0),(4,4,203,'DANADO',4);
/*!40000 ALTER TABLE `alquiler_equipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` int NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  `saldo_pendiente` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1001,'Ana Lopez','55501234','ACTIVO',0.00),(1002,'Carlos Mendez','55505678','ACTIVO',0.00),(1003,'Maria Perez','55509012','ACTIVO',2900.00),(1004,'Jose Ramirez','55503456','INACTIVO',0.00);
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipo`
--

DROP TABLE IF EXISTS `equipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipo` (
  `codigo` int NOT NULL,
  `tipo` varchar(30) DEFAULT NULL,
  `modelo` varchar(80) DEFAULT NULL,
  `tarifa_hora` decimal(10,2) DEFAULT NULL,
  `precio_reposicion` decimal(10,2) DEFAULT NULL,
  `estado` varchar(30) DEFAULT NULL,
  `bateria` int DEFAULT NULL,
  `alquileres_desde_mantenimiento` int DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipo`
--

LOCK TABLES `equipo` WRITE;
/*!40000 ALTER TABLE `equipo` DISABLE KEYS */;
INSERT INTO `equipo` VALUES (101,'BICICLETA_URBANA','Trek Verve',15.00,1500.00,'DISPONIBLE',NULL,1),(102,'BICICLETA_URBANA','Giant Escape',15.00,1400.00,'DISPONIBLE',NULL,0),(201,'BICICLETA_MONTANA','Scott Aspect',25.00,3200.00,'RESERVADO',NULL,2),(202,'BICICLETA_MONTANA','Cannondale Trail',28.00,3500.00,'DISPONIBLE',NULL,4),(203,'BICICLETA_MONTANA','Merida Big Nine',25.00,3000.00,'EN_MANTENIMIENTO',NULL,0),(301,'SCOOTER','Xiaomi Pro 2',35.00,4500.00,'ALQUILADO',80,3),(302,'SCOOTER','Segway Ninebot',40.00,6000.00,'DISPONIBLE',95,1),(303,'SCOOTER','Xiaomi Essential',30.00,3500.00,'DISPONIBLE',20,5);
/*!40000 ALTER TABLE `equipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantenimiento`
--

DROP TABLE IF EXISTS `mantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mantenimiento` (
  `id_mantenimiento` int NOT NULL AUTO_INCREMENT,
  `codigo_equipo` int DEFAULT NULL,
  `fecha` date DEFAULT NULL,
  `motivo` varchar(200) DEFAULT NULL,
  `costo` decimal(10,2) DEFAULT NULL,
  `estado` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_mantenimiento`),
  KEY `codigo_equipo` (`codigo_equipo`),
  CONSTRAINT `mantenimiento_ibfk_1` FOREIGN KEY (`codigo_equipo`) REFERENCES `equipo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantenimiento`
--

LOCK TABLES `mantenimiento` WRITE;
/*!40000 ALTER TABLE `mantenimiento` DISABLE KEYS */;
INSERT INTO `mantenimiento` VALUES (1,102,'2026-10-05','Cambio de frenos',120.00,'CERRADO'),(2,203,'2026-11-04','Dano por alquiler',180.00,'ABIERTO');
/*!40000 ALTER TABLE `mantenimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimiento_accesorio`
--

DROP TABLE IF EXISTS `movimiento_accesorio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_accesorio` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `codigo_accesorio` int DEFAULT NULL,
  `fecha` datetime DEFAULT NULL,
  `cantidad` int DEFAULT NULL,
  `tipo` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id_movimiento`),
  KEY `codigo_accesorio` (`codigo_accesorio`),
  CONSTRAINT `movimiento_accesorio_ibfk_1` FOREIGN KEY (`codigo_accesorio`) REFERENCES `accesorio` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento_accesorio`
--

LOCK TABLES `movimiento_accesorio` WRITE;
/*!40000 ALTER TABLE `movimiento_accesorio` DISABLE KEYS */;
INSERT INTO `movimiento_accesorio` VALUES (1,1,'2026-11-01 08:00:00',20,'ENTRADA'),(2,2,'2026-11-01 08:00:00',10,'ENTRADA'),(3,3,'2026-11-01 08:00:00',10,'ENTRADA'),(4,4,'2026-11-01 08:00:00',10,'ENTRADA'),(5,2,'2026-11-03 10:00:00',6,'SALIDA'),(6,4,'2026-11-03 10:00:00',5,'SALIDA'),(7,1,'2026-11-09 09:00:00',2,'SALIDA');
/*!40000 ALTER TABLE `movimiento_accesorio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id_pago` int NOT NULL AUTO_INCREMENT,
  `id_alquiler` int DEFAULT NULL,
  `metodo` varchar(20) DEFAULT NULL,
  `monto` decimal(10,2) DEFAULT NULL,
  `cambio` decimal(10,2) DEFAULT NULL,
  `referencia` varchar(30) DEFAULT NULL,
  `resultado` varchar(20) DEFAULT NULL,
  `fecha` datetime DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  KEY `id_alquiler` (`id_alquiler`),
  CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`id_alquiler`) REFERENCES `alquiler` (`id_alquiler`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
INSERT INTO `pago` VALUES (1,1,'EFECTIVO',35.00,15.00,NULL,'APROBADO','2026-11-05 11:05:00'),(2,4,'TARJETA',137.50,0.00,'REF-0001','RECHAZADO','2026-11-03 13:05:00'),(3,4,'TARJETA',137.50,0.00,'REF-0002','APROBADO','2026-11-03 13:10:00');
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-12 19:53:28
