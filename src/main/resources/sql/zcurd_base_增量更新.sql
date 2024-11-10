/* 逻辑删除	addBy：钟世云 2018.07.16 */
ALTER TABLE `zcurd_head` ADD `delete_flag_field` VARCHAR(50)  NULL  DEFAULT NULL  COMMENT '删除标志字段'  AFTER `handle_class`;

/* 在线表单	addBy：钟世云 2020.03.29 */
ALTER TABLE `zcurd_head` ADD `include_func` VARCHAR(100) NOT  NULL  DEFAULT '*'  COMMENT '包含功能' AFTER `delete_flag_field`;

/* 在线表单，支持更新表配置，用来区分查询表和更新表。	addBy：钟世云 2020.04.07 */ 
ALTER TABLE `zcurd_head` ADD `table_name_update` VARCHAR(100)  NULL  DEFAULT NULL  COMMENT '更新表，为空时默认为查询表'  AFTER `table_name`;
ALTER TABLE `zcurd_head` CHANGE `table_name` `table_name` VARCHAR(100)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT ''  COMMENT '查询表';

/* 代码优化	addBy：钟世云 2020.05.10 */
delete from zcurd_head where id=9;
INSERT INTO `zcurd_head` (`id`, `table_name`, `table_name_update`, `form_name`, `id_field`, `is_auto`, `form_type`, `dialog_size`, `db_source`, `handle_class`, `delete_flag_field`, `include_func`, `create_time`)
VALUES 
	(9, 'sys_oplog', NULL, '系统操作日志', 'id', 1, 1, '600x400', 'zcurd_base', NULL, NULL, 'detail,list,export', '2020-04-13 23:24:20');
delete from zcurd_field where head_id=9;
INSERT INTO `zcurd_field` (`head_id`, `field_name`, `column_name`, `column_length`, `data_type`, `input_type`, `is_show_list`, `is_allow_detail`, `is_allow_add`, `is_allow_update`, `is_search`, `is_allow_null`, `dict_sql`, `order_num`, `search_type`, `is_sum`, `default_value`, `create_time`)
VALUES
	(9, 'create_time', '创建时间', 120, 'datetime', 'easyui-datebox', 1, 1, 1, 1, 1, 1, '', 50, 2, 0, '', '2020-04-13 23:24:20'),
	(9, 'ip', 'ip', 120, 'varchar', 'easyui-textbox', 1, 1, 1, 1, 1, 1, '', 40, 1, 0, '', '2020-04-13 23:24:20'),
	(9, 'op_content', '操作内容', 120, 'varchar', 'easyui-textbox', 1, 1, 1, 1, 1, 1, '', 30, 1, 0, '', '2020-04-13 23:24:20'),
	(9, 'user_id', '用户', 120, 'int', 'easyui-combobox', 1, 1, 1, 1, 1, 1, 'select id, user_name from sys_user', 20, 1, 0, '', '2020-04-13 23:24:20'),
	(9, 'id', 'id', 120, 'int', 'easyui-numberspinner', 1, 1, 1, 1, 0, 0, '', 1, 1, 0, '', '2020-04-13 23:24:20');
