-- phpMyAdmin SQL Dump
-- version 4.9.4
-- https://www.phpmyadmin.net/
--
-- Хост: adm357.mysql.ukraine.com.ua
-- Час створення: Лют 22 2020 р., 14:43
-- Версія сервера: 5.7.16-10-log
-- Версія PHP: 7.0.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База даних: `adm357_db`
--

-- --------------------------------------------------------

--
-- Структура таблиці `appointments`
--

CREATE TABLE `appointments` (
  `id` int(11) NOT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` tinyint(4) NOT NULL,
  `service_provided` bit(1) NOT NULL DEFAULT b'0',
  `service_type` varchar(255) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `master_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблиці `email_messages`
--

CREATE TABLE `email_messages` (
  `id` int(11) NOT NULL,
  `date_created` date NOT NULL,
  `email` varchar(255) NOT NULL,
  `quick_access_code` varchar(255) DEFAULT NULL,
  `subject` varchar(255) NOT NULL,
  `text_message` text,
  `sent` bit(1) NOT NULL DEFAULT b'0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблиці `feedbacks`
--

CREATE TABLE `feedbacks` (
  `id` int(11) NOT NULL,
  `rating` tinyint(4) NOT NULL DEFAULT '0',
  `text_message` varchar(255) NOT NULL,
  `appointment_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблиці `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name_en` varchar(255) NOT NULL,
  `last_name_en` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `service_type` varchar(255) NOT NULL,
  `tel_number` varchar(255) NOT NULL,
  `first_name_uk` varchar(255) NOT NULL,
  `last_name_uk` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Індекси збережених таблиць
--

--
-- Індекси таблиці `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4q5rt20vvnkv7eohwq22l3ayy` (`customer_id`),
  ADD KEY `FKrqdfukjcn0rhqrac8j96cu9w4` (`master_id`);

--
-- Індекси таблиці `email_messages`
--
ALTER TABLE `email_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `IDX_QUICKACCESSCODE` (`quick_access_code`),
  ADD KEY `IDX_SENT` (`sent`);

--
-- Індекси таблиці `feedbacks`
--
ALTER TABLE `feedbacks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK58uid2ow3xl8erqdy5qy7prkm` (`appointment_id`);

--
-- Індекси таблиці `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  ADD KEY `IDX_ROLE` (`role`),
  ADD KEY `IDX_SERVICETYPE` (`service_type`);

--
-- AUTO_INCREMENT для збережених таблиць
--

--
-- AUTO_INCREMENT для таблиці `appointments`
--
ALTER TABLE `appointments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблиці `email_messages`
--
ALTER TABLE `email_messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблиці `feedbacks`
--
ALTER TABLE `feedbacks`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблиці `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Обмеження зовнішнього ключа збережених таблиць
--

--
-- Обмеження зовнішнього ключа таблиці `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `FK4q5rt20vvnkv7eohwq22l3ayy` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKrqdfukjcn0rhqrac8j96cu9w4` FOREIGN KEY (`master_id`) REFERENCES `users` (`id`);

--
-- Обмеження зовнішнього ключа таблиці `feedbacks`
--
ALTER TABLE `feedbacks`
  ADD CONSTRAINT `FK58uid2ow3xl8erqdy5qy7prkm` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
