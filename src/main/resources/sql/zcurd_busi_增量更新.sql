/* 逻辑删除	addBy：钟世云 2018.07.16 */
ALTER TABLE `claw_book_url` ADD `sys_delete_flag` TINYINT  NULL  DEFAULT '0'  COMMENT '删除标志'  AFTER `sys_update_time`;