CREATE TABLE `d_webhook_tbl` (
  `webhook_id` int NOT NULL AUTO_INCREMENT,
  `created_ts` datetime(6) DEFAULT NULL,
  `deleted_ts` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `endpoint_url` varchar(255) NOT NULL,
  `headers` text,
  `is_active` bit(1) DEFAULT NULL,
  `retry_count` int DEFAULT NULL,
  `updated_ts` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`webhook_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `d_processing_request_tbl` (
  `request_id` int NOT NULL AUTO_INCREMENT,
  `completion_time` datetime(6) DEFAULT NULL,
  `created_ts` datetime(6) DEFAULT NULL,
  `deleted_ts` datetime(6) DEFAULT NULL,
  `original_csv_file_name` varchar(255) DEFAULT NULL,
  `original_csv_file_s3_url` varchar(255) DEFAULT NULL,
  `s3_csv_file_name` varchar(255) DEFAULT NULL,
  `status` enum('COMPLETED','FAILED','IN_PROGRESS','PENDING') NOT NULL,
  `updated_ts` datetime(6) DEFAULT NULL,
  `webhook_triggered` bit(1) DEFAULT NULL,
  `webhook_triggered_is_successful` bit(1) DEFAULT NULL,
  PRIMARY KEY (`request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `d_product_tbl` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `created_ts` datetime(6) DEFAULT NULL,
  `deleted_ts` datetime(6) DEFAULT NULL,
  `product_name` varchar(255) NOT NULL,
  `serial_number` varchar(255) NOT NULL,
  `request_id` int NOT NULL,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `unique_product_per_request` (`request_id`,`serial_number`),
  CONSTRAINT `FKkandbu4vyrwdnlhp86hs9pxbn` FOREIGN KEY (`request_id`) REFERENCES `d_processing_request_tbl` (`request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `d_image_tbl` (
  `image_id` int NOT NULL AUTO_INCREMENT,
  `created_ts` datetime(6) DEFAULT NULL,
  `deleted_ts` datetime(6) DEFAULT NULL,
  `error_message` text,
  `input_url` varchar(1024) NOT NULL,
  `output_url` varchar(1024) DEFAULT NULL,
  `processing_status` enum('COMPLETED','DOWNLOADING','FAILED','PENDING','PROCESSING') NOT NULL,
  `updated_ts` datetime(6) DEFAULT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`image_id`),
  KEY `FK1ihb54et1ca561kk9ecwn1cox` (`product_id`),
  CONSTRAINT `FK1ihb54et1ca561kk9ecwn1cox` FOREIGN KEY (`product_id`) REFERENCES `d_product_tbl` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



