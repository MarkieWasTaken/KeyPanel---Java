CREATE TABLE `users` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255) UNIQUE NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) UNIQUE NOT NULL,
  `location_id` int NOT NULL,
  `is_admin` boolean DEFAULT false,
  `created_at` timestamp DEFAULT (now()),
  `updated_at` timestamp DEFAULT (now())
);

CREATE TABLE `location` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `postal_code` varchar(255) UNIQUE NOT NULL,
  `name` varchar(255)
);

CREATE TABLE `license` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `license_key` varchar(255) UNIQUE NOT NULL,
  `duration` int NOT NULL,
  `active` boolean DEFAULT true,
  `expiration_date` timestamp NOT NULL,
  `created_at` timestamp DEFAULT (now()),
  `updated_at` timestamp DEFAULT (now())
);

CREATE TABLE `license_user` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `license_id` int NOT NULL,
  `user_id` int NOT NULL,
  `assigned_at` timestamp DEFAULT (now())
);

CREATE TABLE `log` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `table_name` varchar(255) NOT NULL,
  `action` varchar(255) NOT NULL,
  `executed_by` int NOT NULL,
  `executed_at` timestamp DEFAULT (now())
);

ALTER TABLE `users` ADD FOREIGN KEY (`location_id`) REFERENCES `location` (`id`);

ALTER TABLE `license_user` ADD FOREIGN KEY (`license_id`) REFERENCES `license` (`id`);

ALTER TABLE `license_user` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `log` ADD FOREIGN KEY (`executed_by`) REFERENCES `users` (`id`);
