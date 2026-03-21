-- 值班表管理菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
('值班表管理', '2030', '1', 'dutySchedule', 'manage/dutySchedule/index', 1, 0, 'C', '0', '0', 'manage:dutySchedule:list', 'table', 'admin', NOW(), 'admin', NOW(), '值班表管理');

-- 值班时间配置菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
('值班时间配置', '2030', '2', 'dutyTimeConfig', 'manage/dutyTimeConfig/index', 1, 0, 'C', '0', '0', 'manage:dutyTimeConfig:list', 'time', 'admin', NOW(), 'admin', NOW(), '值班时间配置');

-- 值班表管理权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
('查询值班表', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班表管理' AND parent_id = '2030' LIMIT 1) AS temp), '1', '', '', 1, 0, 'F', '0', '0', 'manage:dutySchedule:list', '', 'admin', NOW(), 'admin', NOW(), '查询值班表权限'),
('新增值班表', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班表管理' AND parent_id = '2030' LIMIT 1) AS temp), '2', '', '', 1, 0, 'F', '0', '0', 'manage:dutySchedule:add', '', 'admin', NOW(), 'admin', NOW(), '新增值班表权限'),
('修改值班表', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班表管理' AND parent_id = '2030' LIMIT 1) AS temp), '3', '', '', 1, 0, 'F', '0', '0', 'manage:dutySchedule:edit', '', 'admin', NOW(), 'admin', NOW(), '修改值班表权限'),
('删除值班表', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班表管理' AND parent_id = '2030' LIMIT 1) AS temp), '4', '', '', 1, 0, 'F', '0', '0', 'manage:dutySchedule:remove', '', 'admin', NOW(), 'admin', NOW(), '删除值班表权限'),
('导出值班表', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班表管理' AND parent_id = '2030' LIMIT 1) AS temp), '5', '', '', 1, 0, 'F', '0', '0', 'manage:dutySchedule:export', '', 'admin', NOW(), 'admin', NOW(), '导出值班表权限');

-- 值班时间配置权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES
('查询值班时间配置', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班时间配置' AND parent_id = '2030' LIMIT 1) AS temp), '1', '', '', 1, 0, 'F', '0', '0', 'manage:dutyTimeConfig:list', '', 'admin', NOW(), 'admin', NOW(), '查询值班时间配置权限'),
('新增值班时间配置', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班时间配置' AND parent_id = '2030' LIMIT 1) AS temp), '2', '', '', 1, 0, 'F', '0', '0', 'manage:dutyTimeConfig:add', '', 'admin', NOW(), 'admin', NOW(), '新增值班时间配置权限'),
('修改值班时间配置', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班时间配置' AND parent_id = '2030' LIMIT 1) AS temp), '3', '', '', 1, 0, 'F', '0', '0', 'manage:dutyTimeConfig:edit', '', 'admin', NOW(), 'admin', NOW(), '修改值班时间配置权限'),
('删除值班时间配置', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班时间配置' AND parent_id = '2030' LIMIT 1) AS temp), '4', '', '', 1, 0, 'F', '0', '0', 'manage:dutyTimeConfig:remove', '', 'admin', NOW(), 'admin', NOW(), '删除值班时间配置权限'),
('导出值班时间配置', (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '值班时间配置' AND parent_id = '2030' LIMIT 1) AS temp), '5', '', '', 1, 0, 'F', '0', '0', 'manage:dutyTimeConfig:export', '', 'admin', NOW(), 'admin', NOW(), '导出值班时间配置权限');