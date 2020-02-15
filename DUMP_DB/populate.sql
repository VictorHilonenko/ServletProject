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
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,'2020-01-22',19,_binary '','HAIRDRESSING',15,18),(59,'2020-01-30',9,_binary '\0','HAIRDRESSING',15,18),(60,'2020-01-30',11,_binary '\0','COSMETOLOGY',15,16),(61,'2020-01-30',13,_binary '\0','MAKEUP',15,26),(62,'2020-01-21',12,_binary '','HAIRDRESSING',23,18),(63,'2020-01-21',14,_binary '','COSMETOLOGY',23,16),(64,'2020-01-21',16,_binary '','MAKEUP',23,26),(65,'2020-01-21',18,_binary '','HAIRDRESSING',15,18),(66,'2020-01-21',16,_binary '','COSMETOLOGY',15,16),(67,'2020-01-21',10,_binary '','MAKEUP',15,26),(68,'2020-01-30',11,_binary '\0','HAIRDRESSING',23,18),(69,'2020-01-22',18,_binary '\0','COSMETOLOGY',23,16),(70,'2020-01-31',20,_binary '\0','MAKEUP',23,26),(71,'2020-01-20',19,_binary '','HAIRDRESSING',15,18),(72,'2020-01-20',10,_binary '','COSMETOLOGY',15,16),(73,'2020-01-20',17,_binary '','MAKEUP',15,26),(74,'2020-01-20',9,_binary '','HAIRDRESSING',23,18),(75,'2020-01-20',8,_binary '','COSMETOLOGY',23,16),(76,'2020-01-20',20,_binary '','MAKEUP',23,26),(77,'2020-01-19',10,_binary '','HAIRDRESSING',15,18),(78,'2020-01-29',15,_binary '','COSMETOLOGY',15,16),(79,'2020-02-05',18,_binary '','MAKEUP',15,26),(80,'2020-02-07',16,_binary '','HAIRDRESSING',23,18),(81,'2020-02-08',17,_binary '','COSMETOLOGY',23,16),(82,'2020-02-09',11,_binary '','MAKEUP',23,26),(83,'2020-01-18',12,_binary '','HAIRDRESSING',23,18),(84,'2020-01-28',14,_binary '','COSMETOLOGY',23,16),(85,'2020-01-28',16,_binary '','MAKEUP',23,26),(86,'2020-01-18',18,_binary '','HAIRDRESSING',15,18),(87,'2020-01-18',16,_binary '','COSMETOLOGY',15,16),(88,'2020-01-28',10,_binary '','MAKEUP',15,26),(89,'2020-01-27',11,_binary '','HAIRDRESSING',23,18),(90,'2020-01-17',18,_binary '','COSMETOLOGY',23,16),(91,'2020-01-27',20,_binary '','MAKEUP',23,26),(92,'2020-01-17',19,_binary '','HAIRDRESSING',15,18),(93,'2020-01-17',10,_binary '','COSMETOLOGY',15,16),(94,'2020-01-27',17,_binary '','MAKEUP',15,26),(95,'2020-01-16',9,_binary '','HAIRDRESSING',23,18),(96,'2020-01-16',8,_binary '','COSMETOLOGY',23,16),(97,'2020-01-16',20,_binary '','MAKEUP',23,26),(98,'2020-01-15',10,_binary '','HAIRDRESSING',15,18),(99,'2020-01-15',15,_binary '','COSMETOLOGY',15,16),(100,'2020-01-15',18,_binary '','MAKEUP',15,26),(101,'2020-01-15',16,_binary '','HAIRDRESSING',23,18),(102,'2020-01-15',17,_binary '','COSMETOLOGY',23,16),(103,'2020-02-10',11,_binary '','MAKEUP',23,26),(104,'2020-01-23',12,_binary '\0','HAIRDRESSING',23,18),(105,'2020-01-23',14,_binary '\0','COSMETOLOGY',23,16),(106,'2020-01-23',16,_binary '\0','MAKEUP',23,26),(107,'2020-01-23',18,_binary '\0','HAIRDRESSING',15,18),(108,'2020-01-23',16,_binary '\0','COSMETOLOGY',15,16),(109,'2020-01-23',10,_binary '\0','MAKEUP',15,26),(110,'2020-01-24',11,_binary '\0','HAIRDRESSING',23,18),(111,'2020-01-24',18,_binary '\0','COSMETOLOGY',23,16),(112,'2020-02-10',20,_binary '\0','MAKEUP',23,26),(113,'2020-02-12',19,_binary '','HAIRDRESSING',15,18),(114,'2020-02-13',10,_binary '','COSMETOLOGY',15,16),(115,'2020-02-15',17,_binary '\0','MAKEUP',15,26),(116,'2020-01-26',9,_binary '\0','HAIRDRESSING',23,18),(117,'2020-01-26',8,_binary '\0','COSMETOLOGY',23,16),(118,'2020-01-26',20,_binary '\0','MAKEUP',23,26),(119,'2020-01-31',17,_binary '\0','HAIRDRESSING',28,18),(120,'2020-01-22',16,_binary '\0','COSMETOLOGY',28,16),(158,'2020-02-22',19,_binary '\0','HAIRDRESSING',15,18),(162,'2020-02-21',12,_binary '\0','HAIRDRESSING',23,18),(163,'2020-02-21',14,_binary '\0','COSMETOLOGY',23,16),(164,'2020-02-21',16,_binary '\0','MAKEUP',23,26),(165,'2020-02-21',18,_binary '\0','HAIRDRESSING',15,18),(166,'2020-02-21',16,_binary '\0','COSMETOLOGY',15,16),(167,'2020-02-21',10,_binary '\0','MAKEUP',15,26),(169,'2020-02-22',18,_binary '\0','COSMETOLOGY',23,16),(171,'2020-02-20',19,_binary '\0','HAIRDRESSING',15,18),(172,'2020-02-20',10,_binary '\0','COSMETOLOGY',15,16),(173,'2020-02-20',17,_binary '\0','MAKEUP',15,26),(174,'2020-02-20',9,_binary '\0','HAIRDRESSING',23,18),(175,'2020-02-20',8,_binary '\0','COSMETOLOGY',23,16),(176,'2020-02-20',20,_binary '\0','MAKEUP',23,26),(177,'2020-02-19',10,_binary '\0','HAIRDRESSING',15,18),(178,'2020-02-29',15,_binary '\0','COSMETOLOGY',15,16),(183,'2020-02-18',12,_binary '\0','HAIRDRESSING',23,18),(184,'2020-02-28',14,_binary '\0','COSMETOLOGY',23,16),(185,'2020-02-28',16,_binary '\0','MAKEUP',23,26),(186,'2020-02-18',18,_binary '\0','HAIRDRESSING',15,18),(187,'2020-02-18',16,_binary '\0','COSMETOLOGY',15,16),(188,'2020-02-28',10,_binary '\0','MAKEUP',15,26),(189,'2020-02-27',11,_binary '\0','HAIRDRESSING',23,18),(190,'2020-02-17',18,_binary '\0','COSMETOLOGY',23,16),(192,'2020-02-17',19,_binary '\0','HAIRDRESSING',15,18),(193,'2020-02-17',10,_binary '\0','COSMETOLOGY',15,16),(194,'2020-02-27',17,_binary '\0','MAKEUP',15,26),(195,'2020-02-16',9,_binary '\0','HAIRDRESSING',23,18),(196,'2020-02-16',8,_binary '\0','COSMETOLOGY',23,16),(197,'2020-02-16',20,_binary '\0','MAKEUP',23,26),(198,'2020-02-15',10,_binary '\0','HAIRDRESSING',15,18),(199,'2020-02-15',15,_binary '\0','COSMETOLOGY',15,16),(200,'2020-02-15',18,_binary '\0','MAKEUP',15,26),(201,'2020-02-15',16,_binary '\0','HAIRDRESSING',23,18),(202,'2020-02-15',17,_binary '\0','COSMETOLOGY',23,16),(204,'2020-02-23',12,_binary '\0','HAIRDRESSING',23,18),(205,'2020-02-23',14,_binary '\0','COSMETOLOGY',23,16),(206,'2020-02-23',16,_binary '\0','MAKEUP',23,26),(207,'2020-02-23',18,_binary '\0','HAIRDRESSING',15,18),(208,'2020-02-23',16,_binary '\0','COSMETOLOGY',15,16),(209,'2020-02-23',10,_binary '\0','MAKEUP',15,26),(210,'2020-02-24',11,_binary '\0','HAIRDRESSING',23,18),(211,'2020-02-24',18,_binary '\0','COSMETOLOGY',23,16),(216,'2020-02-26',9,_binary '\0','HAIRDRESSING',23,18),(217,'2020-02-26',8,_binary '\0','COSMETOLOGY',23,16),(218,'2020-02-26',20,_binary '\0','MAKEUP',23,26),(220,'2020-02-22',16,_binary '\0','COSMETOLOGY',28,16);
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `email_messages`
--

LOCK TABLES `email_messages` WRITE;
/*!40000 ALTER TABLE `email_messages` DISABLE KEYS */;
INSERT INTO `email_messages` VALUES (1,'2020-01-22',NULL,'2020-01-22','user@ukr.net','117968af-371c-4a56-b044-5045c2dd121f','leave feedback, please','please, leave your feedback here: http://localhost:8989/feedbacks/1/117968af-371c-4a56-b044-5045c2dd121f'),(2,'2020-01-22',NULL,'2020-01-22','user@ukr.net','98924ae1-0034-4d20-b8eb-386c903eab4b','leave feedback, please','please, leave your feedback here: http://localhost:8989/feedbacks/1/98924ae1-0034-4d20-b8eb-386c903eab4b'),(3,'2020-01-22',NULL,'2020-01-22','user@ukr.net','a12c5ebf-4d71-447c-9485-5db8eea33a13','leave feedback, please','please, leave your feedback here: http://localhost:8989/feedbacks/1/a12c5ebf-4d71-447c-9485-5db8eea33a13'),(4,'2020-01-22',NULL,'2020-01-22','user@ukr.net','ada2c7e7-4fbf-4564-bc81-b7a9a1b501f2','leave feedback, please','please, leave your feedback here: http://localhost:8989/feedbacks/98/ada2c7e7-4fbf-4564-bc81-b7a9a1b501f2');
/*!40000 ALTER TABLE `email_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `feedbacks`
--

LOCK TABLES `feedbacks` WRITE;
/*!40000 ALTER TABLE `feedbacks` DISABLE KEYS */;
INSERT INTO `feedbacks` VALUES (1,10,'your first feedback!',1),(2,10,'дуже дякую за гарний сервіс!',65),(3,10,'дуже дякую за гарний сервіс!',66),(5,10,'thanks for your excellent work!',71),(6,10,'thanks for your excellent work!',72),(7,10,'excellent!',73),(8,10,'дуже дякую за гарний сервіс!',77),(9,10,'дуже дякую за гарний сервіс!',78),(10,10,'дуже дякую за гарний сервіс!',79),(20,10,'thank you guys,\r\nwill recommend you to my friends!',67),(21,10,'thanks for your excellent work!',86),(22,10,'дуже дякую за гарний сервіс!',87),(23,10,'дуже дякую за гарний сервіс!',88),(24,10,'thanks for your excellent work!',92),(25,10,'good job!!!',93),(26,10,'thanks for your excellent work!',94),(27,10,'дуже дякую за гарний сервіс!',98),(28,10,'good job!!!',99),(29,10,'thanks for your excellent work!',100),(35,10,'дуже дякую за гарний сервіс!',62),(36,10,'thanks for your excellent work!',63),(37,10,'дуже дякую за гарний сервіс!',64),(38,10,'дуже дякую за гарний сервіс!',74),(39,10,'thanks for your excellent work!',75),(40,10,'дуже дякую за гарний сервіс!',76),(41,10,'дуже дякую за гарний сервіс!',80),(42,10,'thanks for your excellent work!',81),(43,10,'дуже дякую за гарний сервіс!',82),(44,10,'thanks for your excellent work!',83),(45,10,'дуже дякую за гарний сервіс!',84),(46,10,'thanks for your excellent work!',85),(47,10,'good job!!!',89),(48,10,'thanks for your excellent work!',90),(49,10,'good job!!!',91),(50,10,'дуже дякую за гарний сервіс!',95),(51,10,'thanks for your excellent work!',96),(52,10,'дуже дякую за гарний сервіс!',97),(53,10,'thanks for your excellent work!',101),(54,10,'good job!!!',102),(55,10,'thanks for your excellent work!',103),(69,10,'excellent!',113),(70,10,'чудово, дуже дякую за вашу роботу!',114);
/*!40000 ALTER TABLE `feedbacks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (14,'admin@ukr.net','Admin','Admin','$2a$10$it5GyDjgwDCzSQdvZNzfIOwq1TAm4mf0phWLvZXMFtvTM6.4z5rm.','ROLE_ADMIN','','+380671234567','Адмін','Адмін'),(15,'user@ukr.net','Jennie','Johnson','$2a$10$dGkGLJal4NnWugv4IYcTkueQEOxAjMbu0U9loOBQuDjTsGueR8zUG','ROLE_USER','','+380661596897','Євгенія','Джонсон'),(16,'master@ukr.net','Julia','Skrypka','$2a$10$w1aZ.IvvQcRCIuFiVv4ooONdgI69KRDbNYUp56idGZUG7LoCcnW7G','ROLE_MASTER','MAKEUP','+380675796428','Юлія','Скрипка'),(18,'master2@ukr.net','Tetyana','Bila','$2a$10$w1aZ.IvvQcRCIuFiVv4ooONdgI69KRDbNYUp56idGZUG7LoCcnW7G','ROLE_MASTER','COSMETOLOGY','+380693796425','Тетяна','Біла'),(23,'user2@ukr.net','Caitlin','Ville','$2a$10$4betNKttzhCPxiAudLBNQ.IKpyFGTLp9P6cEwREWuVRa08Ukb300q','ROLE_USER','','+380686154512','Кейтлін','Вілль'),(26,'master3@ukr.net','Natalya','Krasko','$2a$10$QH7Njp5xedE4z/xwl9iXB.MusCuCEkMu6XhFLuJ8bucMsPw6YgwZm','ROLE_MASTER','HAIRDRESSING','+380676517777','Наталя','Краско'),(28,'123@ukr.net','Josephine','Jennings','$2a$10$V9SujUodCmu1L2/RFxVuHeMA7hoA8wNPC5Yu0H2YrtWQ.xIkUCZB6','ROLE_MASTER','COSMETOLOGY','+380672574563','Жозефіна','Дженнінгс'),(29,'12@15.com','Lindsey','Star','$2a$10$IG3aQQnM3C0oDR77zQQVMetSGdnq1Jn7.vZE.Vogst8bHNnWB6M1a','ROLE_MASTER','MAKEUP','+380672561819','Ліндсі','Стар');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-02-15 20:32:19
