-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: localhost    Database: beauty_scheduler
-- ------------------------------------------------------
-- Server version	8.0.18

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
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `appointment_date` date NOT NULL,
  `appointment_time` tinyint(4) NOT NULL,
  `service_provided` bit(1) NOT NULL DEFAULT b'0',
  `service_type` varchar(255) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `master_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4q5rt20vvnkv7eohwq22l3ayy` (`customer_id`),
  KEY `FKrqdfukjcn0rhqrac8j96cu9w4` (`master_id`),
  CONSTRAINT `FK4q5rt20vvnkv7eohwq22l3ayy` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrqdfukjcn0rhqrac8j96cu9w4` FOREIGN KEY (`master_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=231 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_messages`
--

DROP TABLE IF EXISTS `email_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_messages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_created` date NOT NULL,
  `email` varchar(255) NOT NULL,
  `quick_access_code` varchar(255) DEFAULT NULL,
  `subject` varchar(255) NOT NULL,
  `text_message` text,
  `sent` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `IDX_QUICKACCESSCODE` (`quick_access_code`),
  KEY `IDX_SENT` (`sent`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `feedbacks`
--

DROP TABLE IF EXISTS `feedbacks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedbacks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rating` tinyint(4) NOT NULL DEFAULT '0',
  `text_message` varchar(255) NOT NULL,
  `appointment_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK58uid2ow3xl8erqdy5qy7prkm` (`appointment_id`),
  CONSTRAINT `FK58uid2ow3xl8erqdy5qy7prkm` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name_en` varchar(255) NOT NULL,
  `last_name_en` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `service_type` varchar(255) NOT NULL,
  `tel_number` varchar(255) NOT NULL,
  `first_name_uk` varchar(255) NOT NULL,
  `last_name_uk` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `IDX_ROLE` (`role`),
  KEY `IDX_SERVICETYPE` (`service_type`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-02-20  2:51:01
