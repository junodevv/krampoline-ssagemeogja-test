CREATE DATABASE  IF NOT EXISTS `sgmjdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `sgmjdb`;
-- MySQL dump 10.13  Distrib 8.0.36, for macos14 (arm64)
--
-- Host: 127.0.0.1    Database: sgmjdb
-- ------------------------------------------------------
-- Server version	8.4.0

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
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
                        `cart_id` bigint NOT NULL AUTO_INCREMENT,
                        `member_id` bigint DEFAULT NULL,
                        `product_id` bigint DEFAULT NULL,
                        `quantity` bigint DEFAULT NULL,
                        PRIMARY KEY (`cart_id`),
                        KEY `FKix170nytunweovf2v9137mx2o` (`member_id`),
                        KEY `FK3d704slv66tw6x5hmbm6p2x3u` (`product_id`),
                        CONSTRAINT `FK3d704slv66tw6x5hmbm6p2x3u` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
                        CONSTRAINT `FKix170nytunweovf2v9137mx2o` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `discount`
--

DROP TABLE IF EXISTS `discount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discount` (
                            `discount_rate` decimal(3,1) NOT NULL,
                            `discount_id` bigint NOT NULL AUTO_INCREMENT,
                            PRIMARY KEY (`discount_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `join_mart`
--

DROP TABLE IF EXISTS `join_mart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `join_mart` (
                             `join_id` bigint NOT NULL AUTO_INCREMENT,
                             `store` varchar(255) NOT NULL,
                             PRIMARY KEY (`join_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mart`
--

DROP TABLE IF EXISTS `mart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mart` (
                        `join_id` bigint DEFAULT NULL,
                        `mart_id` bigint NOT NULL AUTO_INCREMENT,
                        `mart_address` varchar(255) NOT NULL,
                        `mart_name` varchar(255) NOT NULL,
                        PRIMARY KEY (`mart_id`),
                        KEY `FKmoa0xwg5n9vo23sqtmkc6p0dn` (`join_id`),
                        CONSTRAINT `FKmoa0xwg5n9vo23sqtmkc6p0dn` FOREIGN KEY (`join_id`) REFERENCES `join_mart` (`join_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mart_info`
--

DROP TABLE IF EXISTS `mart_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mart_info` (
                             `data_id` bigint NOT NULL AUTO_INCREMENT,
                             `manufacturer` varchar(255) DEFAULT NULL,
                             `product_name` varchar(255) NOT NULL,
                             `sale_price` varchar(255) NOT NULL,
                             `store` varchar(255) NOT NULL,
                             PRIMARY KEY (`data_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1224 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mart_product`
--

DROP TABLE IF EXISTS `mart_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mart_product` (
                                `discount_id` bigint DEFAULT NULL,
                                `join_id` bigint DEFAULT NULL,
                                `mart_product_id` bigint NOT NULL AUTO_INCREMENT,
                                `price` bigint NOT NULL,
                                `product_id` bigint DEFAULT NULL,
                                `stock` bigint DEFAULT NULL,
                                `manufacturer` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`mart_product_id`),
                                KEY `FK36l0tbm76xlc6g6ejg008i67e` (`discount_id`),
                                KEY `FK5jiyonjiruwfl1l38higeqpdu` (`join_id`),
                                KEY `FKb34ppnh734q9dtjet5ex0wiy5` (`product_id`),
                                CONSTRAINT `FK36l0tbm76xlc6g6ejg008i67e` FOREIGN KEY (`discount_id`) REFERENCES `discount` (`discount_id`),
                                CONSTRAINT `FK5jiyonjiruwfl1l38higeqpdu` FOREIGN KEY (`join_id`) REFERENCES `join_mart` (`join_id`),
                                CONSTRAINT `FKb34ppnh734q9dtjet5ex0wiy5` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1915 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
                          `create_time` datetime(6) DEFAULT NULL,
                          `member_id` bigint NOT NULL AUTO_INCREMENT,
                          `update_time` datetime(6) DEFAULT NULL,
                          `address` varchar(255) DEFAULT NULL,
                          `email` varchar(255) DEFAULT NULL,
                          `name` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member_seq`
--

DROP TABLE IF EXISTS `member_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_seq` (
                              `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
                           `product_id` bigint NOT NULL AUTO_INCREMENT,
                           `product_img_url` varchar(255) DEFAULT NULL,
                           `product_name` varchar(255) NOT NULL,
                           PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=512 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
                          `score` decimal(2,1) NOT NULL,
                          `mart_id` bigint DEFAULT NULL,
                          `member_id` bigint DEFAULT NULL,
                          `review_id` bigint NOT NULL AUTO_INCREMENT,
                          `review_content` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`review_id`),
                          KEY `FKkcal4uuj4v7ue8ouaxwxfe8b8` (`mart_id`),
                          KEY `FKk0ccx5i4ci2wd70vegug074w1` (`member_id`),
                          CONSTRAINT `FKk0ccx5i4ci2wd70vegug074w1` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
                          CONSTRAINT `FKkcal4uuj4v7ue8ouaxwxfe8b8` FOREIGN KEY (`mart_id`) REFERENCES `mart` (`mart_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-24 14:49:18
