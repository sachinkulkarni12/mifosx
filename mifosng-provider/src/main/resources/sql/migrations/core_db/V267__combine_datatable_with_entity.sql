ALTER TABLE `x_registered_table`
	ADD COLUMN `combine_with_main_entity` TINYINT NULL DEFAULT '0' AFTER `category`;
	
ALTER TABLE `x_registered_table`
	ADD COLUMN `minimum_rows` TINYINT NULL DEFAULT '1' AFTER `combine_with_main_entity`;

ALTER TABLE `x_registered_table`
	ADD COLUMN `is_multirow` TINYINT NULL  AFTER `minimum_rows`;	