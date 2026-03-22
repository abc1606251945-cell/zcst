-- 1) 把“值班管理(2030)”升级为主类目：改名为“场馆值班管理”
-- 说明：这样菜单层级将从 “值班管理 -> 场馆值班管理 -> 六个场馆” 变为 “场馆值班管理 -> 六个场馆”
UPDATE `sys_menu`
SET `menu_name` = '场馆值班管理',
    `icon` = 'peoples',
    `remark` = '各场馆值班管理目录'
WHERE `menu_id` = 2030;

-- 2) 六个场馆直接挂到 2030 下（作为二级菜单）
SET @parent_id = 2030;

-- 可重复执行：先清理旧的六馆菜单与角色关联
DELETE FROM `sys_role_menu`
WHERE `menu_id` IN (
  SELECT `menu_id` FROM `sys_menu`
  WHERE `perms` IN (
    'manage:dutySchedule:siqi',
    'manage:dutySchedule:hongyi',
    'manage:dutySchedule:xinyuan',
    'manage:dutySchedule:duxue',
    'manage:dutySchedule:zhixing',
    'manage:dutySchedule:guofang'
  )
);

DELETE FROM `sys_menu`
WHERE `perms` IN (
  'manage:dutySchedule:siqi',
  'manage:dutySchedule:hongyi',
  'manage:dutySchedule:xinyuan',
  'manage:dutySchedule:duxue',
  'manage:dutySchedule:zhixing',
  'manage:dutySchedule:guofang'
);

-- 2. 创建六个场馆的菜单页面
-- 思齐馆
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('思齐馆值班', @parent_id, '1', 'siqiDuty', 'manage/dutySchedule/venue/Siqi', 1, 0, 'C', '0', '0', 'manage:dutySchedule:siqi', 'skill', 'admin', NOW(), 'admin', NOW(), '思齐馆值班管理');

-- 弘毅馆
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('弘毅馆值班', @parent_id, '2', 'hongyiDuty', 'manage/dutySchedule/venue/Hongyi', 1, 0, 'C', '0', '0', 'manage:dutySchedule:hongyi', 'skill', 'admin', NOW(), 'admin', NOW(), '弘毅馆值班管理');

-- 心缘馆
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('心缘馆值班', @parent_id, '3', 'xinyuanDuty', 'manage/dutySchedule/venue/Xinyuan', 1, 0, 'C', '0', '0', 'manage:dutySchedule:xinyuan', 'skill', 'admin', NOW(), 'admin', NOW(), '心缘馆值班管理');

-- 笃学馆
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('笃学馆值班', @parent_id, '4', 'duxueDuty', 'manage/dutySchedule/venue/Duxue', 1, 0, 'C', '0', '0', 'manage:dutySchedule:duxue', 'skill', 'admin', NOW(), 'admin', NOW(), '笃学馆值班管理');

-- 知行馆
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('知行馆值班', @parent_id, '5', 'zhixingDuty', 'manage/dutySchedule/venue/Zhixing', 1, 0, 'C', '0', '0', 'manage:dutySchedule:zhixing', 'skill', 'admin', NOW(), 'admin', NOW(), '知行馆值班管理');

-- 国防教育体验馆
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('国防教育体验馆值班', @parent_id, '6', 'guofangDuty', 'manage/dutySchedule/venue/Guofang', 1, 0, 'C', '0', '0', 'manage:dutySchedule:guofang', 'skill', 'admin', NOW(), 'admin', NOW(), '国防教育体验馆值班管理');
