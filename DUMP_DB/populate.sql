-- phpMyAdmin SQL Dump
-- version 4.9.4
-- https://www.phpmyadmin.net/
--
-- Хост: adm357.mysql.ukraine.com.ua
-- Час створення: Лют 22 2020 р., 14:44
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

--
-- Дамп даних таблиці `users`
--

INSERT INTO `users` (`id`, `email`, `first_name_en`, `last_name_en`, `password`, `role`, `service_type`, `tel_number`, `first_name_uk`, `last_name_uk`) VALUES
(14, 'admin@mail.com', 'Admin', 'Admin', '$2a$10$it5GyDjgwDCzSQdvZNzfIOwq1TAm4mf0phWLvZXMFtvTM6.4z5rm.', 'ROLE_ADMIN', 'NULL', '+380671234567', 'Адмін', 'Адмін'),
(15, 'jennie@mail.com', 'Jennie', 'Johnson', '$2a$10$dGkGLJal4NnWugv4IYcTkueQEOxAjMbu0U9loOBQuDjTsGueR8zUG', 'ROLE_USER', 'NULL', '+380661596897', 'Євгенія', 'Джонсон'),
(16, 'julia@mail.com', 'Julia', 'Skrypka', '$2a$10$w1aZ.IvvQcRCIuFiVv4ooONdgI69KRDbNYUp56idGZUG7LoCcnW7G', 'ROLE_MASTER', 'HAIRDRESSING', '+380675796428', 'Юлія', 'Скрипка'),
(18, 'tanya@mail.com', 'Tetyana', 'Bila', '$2a$10$w1aZ.IvvQcRCIuFiVv4ooONdgI69KRDbNYUp56idGZUG7LoCcnW7G', 'ROLE_MASTER', 'MAKEUP', '+380693796425', 'Тетяна', 'Біла'),
(23, 'caitlin@mail.com', 'Caitlin', 'Ville', '$2a$10$dGkGLJal4NnWugv4IYcTkueQEOxAjMbu0U9loOBQuDjTsGueR8zUG', 'ROLE_USER', 'NULL', '+380686154512', 'Кейтлін', 'Вілль'),
(26, 'nata@mail.com', 'Natalya', 'Krasko', '$2a$10$w1aZ.IvvQcRCIuFiVv4ooONdgI69KRDbNYUp56idGZUG7LoCcnW7G', 'ROLE_MASTER', 'MAKEUP', '+380676517777', 'Наталя', 'Краско'),
(28, 'josy@mail.com', 'Josephine', 'Jennings', '$2a$10$w1aZ.IvvQcRCIuFiVv4ooONdgI69KRDbNYUp56idGZUG7LoCcnW7G', 'ROLE_MASTER', 'COSMETOLOGY', '+380672574563', 'Жозефіна', 'Дженнінгс'),
(29, 'lindsey@mail.com', 'Lindsey', 'Star', '$2a$10$dGkGLJal4NnWugv4IYcTkueQEOxAjMbu0U9loOBQuDjTsGueR8zUG', 'ROLE_USER', 'NULL', '+380672561819', 'Ліндсі', 'Стар');

--
-- Дамп даних таблиці `appointments`
--

INSERT INTO `appointments` (`id`, `appointment_date`, `appointment_time`, `service_provided`, `service_type`, `customer_id`, `master_id`) VALUES
(241, '2020-02-16', 11, b'1', 'HAIRDRESSING', 29, 16),
(242, '2020-02-17', 9, b'1', 'MAKEUP', 29, 18),
(243, '2020-02-18', 14, b'1', 'COSMETOLOGY', 29, 28),
(244, '2020-02-19', 9, b'1', 'MAKEUP', 29, 18),
(245, '2020-02-20', 10, b'1', 'COSMETOLOGY', 29, 28),
(246, '2020-02-21', 12, b'1', 'HAIRDRESSING', 29, 16),
(247, '2020-02-22', 15, b'1', 'MAKEUP', 29, 18),
(248, '2020-02-16', 14, b'1', 'HAIRDRESSING', 23, 16),
(249, '2020-02-13', 13, b'0', 'MAKEUP', 23, 18),
(250, '2020-02-16', 13, b'1', 'MAKEUP', 23, 18),
(251, '2020-02-11', 11, b'0', 'COSMETOLOGY', 23, 28),
(252, '2020-02-18', 11, b'1', 'COSMETOLOGY', 23, 28),
(253, '2020-02-19', 13, b'1', 'MAKEUP', 23, 18),
(254, '2020-02-20', 15, b'1', 'MAKEUP', 23, 18),
(255, '2020-02-22', 10, b'0', 'HAIRDRESSING', 23, 16),
(256, '2020-02-17', 17, b'1', 'HAIRDRESSING', 15, 16),
(257, '2020-02-17', 9, b'1', 'MAKEUP', 15, 26),
(258, '2020-02-19', 16, b'1', 'HAIRDRESSING', 15, 16),
(259, '2020-02-23', 9, b'0', 'HAIRDRESSING', 15, 16),
(260, '2020-02-10', 10, b'0', 'HAIRDRESSING', 29, 16),
(261, '2020-02-12', 15, b'0', 'MAKEUP', 29, 18);

--
-- Дамп даних таблиці `feedbacks`
--

INSERT INTO `feedbacks` (`id`, `rating`, `text_message`, `appointment_id`) VALUES
(1, 10, 'Дуже дякую за прекрасний сервіс!!!', 243),
(2, 10, 'Дуже дякую! Незабутні враження від вашого підходу до клієнтів!', 245),
(3, 10, 'The best service ever! Great thanks!', 248),
(4, 10, 'The best service ever! Great thanks!', 250),
(5, 10, 'The best service ever! Great thanks!', 252),
(6, 10, 'The best service ever! Great thanks!', 253),
(7, 10, 'The best service ever! You are real professionals!', 254),
(10, 10, 'Найкращий сервіс і найкращий салон! Рекомендуватиму вас всім друзям!', 256),
(11, 10, 'Найкращий сервіс і найкращий салон! Рекомендуватиму вас всім друзям!', 257),
(12, 10, 'Найкращий сервіс і найкращий салон! Рекомендуватиму вас всім друзям!', 258),
(13, 10, 'Дуже дякую! Незабутні враження від вашого підходу до клієнтів!', 241),
(14, 10, 'Дуже дякую! Незабутні враження від вашого підходу до клієнтів!', 242),
(15, 10, 'Дуже дякую! Незабутні враження від вашого підходу до клієнтів!', 244),
(16, 10, 'Ви зробили мені свято!', 246),
(20, 10, 'Thank you!!!', 247);

--
-- Дамп даних таблиці `email_messages`
--

INSERT INTO `email_messages` (`id`, `date_created`, `email`, `quick_access_code`, `subject`, `text_message`, `sent`) VALUES
(1, '2020-02-21', 'caitlin@mail.com', '3a66a9ad-7428-4bcd-aa55-eaab6648d4e2', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/252/3a66a9ad-7428-4bcd-aa55-eaab6648d4e2', b'1'),
(2, '2020-02-21', 'lindsey@mail.com', '', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/243/c5cbe245-0e56-4a4f-aa22-6586c684faff', b'1'),
(3, '2020-02-21', 'lindsey@mail.com', '', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/245/dccba8da-1f04-408a-9135-02e7c14fec78', b'1'),
(4, '2020-02-21', 'jennie@mail.com', 'f822bfdd-0891-43ff-8374-e5b3450885f3', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/257/f822bfdd-0891-43ff-8374-e5b3450885f3', b'1'),
(5, '2020-02-21', 'lindsey@mail.com', 'f2e41cb2-f5c8-42b0-8e2f-0a89c532ee7f', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/242/f2e41cb2-f5c8-42b0-8e2f-0a89c532ee7f', b'1'),
(6, '2020-02-21', 'caitlin@mail.com', '78d11318-279f-4d1c-83a8-0f81effbed20', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/250/78d11318-279f-4d1c-83a8-0f81effbed20', b'1'),
(7, '2020-02-21', 'caitlin@mail.com', '8a3a6fa6-494f-4ec5-9055-ff36dccdea6f', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/253/8a3a6fa6-494f-4ec5-9055-ff36dccdea6f', b'1'),
(8, '2020-02-21', 'lindsey@mail.com', 'b359be9d-3020-4d8c-879a-24b39d3d588f', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/244/b359be9d-3020-4d8c-879a-24b39d3d588f', b'1'),
(9, '2020-02-21', 'caitlin@mail.com', 'f3f39f17-c336-4333-aaf3-f27092ac55f7', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/254/f3f39f17-c336-4333-aaf3-f27092ac55f7', b'1'),
(10, '2020-02-21', 'lindsey@mail.com', 'ada9f34f-2a26-4cf0-b479-d84d29b0bae9', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/241/ada9f34f-2a26-4cf0-b479-d84d29b0bae9', b'1'),
(11, '2020-02-21', 'caitlin@mail.com', '', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/248/420f139b-a6a6-48ac-ac2c-420b79738ddb', b'1'),
(12, '2020-02-21', 'jennie@mail.com', '', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/258/036668e1-0ee4-461c-bb36-92717ce85df4', b'1'),
(13, '2020-02-21', 'jennie@mail.com', '', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/256/ff7fcf24-501f-41f5-bd08-600d6847b5bf', b'1'),
(14, '2020-02-21', 'lindsey@mail.com', '', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/246/f0a2291d-de16-4fc7-990c-6221fcf6142f', b'1'),
(15, '2020-02-22', 'lindsey@mail.com', '', 'Залиште відгук, будь ласка', 'Будь ласка, залиште Ваш відгук тут: http://localhost:8989/feedbacks/247/6fc21529-dd4a-4479-8217-854235367a65', b'1');

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
