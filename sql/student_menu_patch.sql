INSERT INTO sys_role (role_id, role_name, role_key, venue_id, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
VALUES (200, '学生', 'student', NULL, 9, '1', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '学生角色');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
  (3000, '值班管理', 0, 1, 'student/duty', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'job', 'admin', NOW(), '', NULL, ''),
  (3001, '申请管理', 3000, 1, 'application', 'student/duty/index', NULL, '', 1, 0, 'C', '0', '0', NULL, 'edit', 'admin', NOW(), '', NULL, ''),
  (3002, '当前时段无课人员', 3000, 2, 'free', 'student/duty/free', NULL, '', 1, 0, 'C', '0', '0', NULL, 'people', 'admin', NOW(), '', NULL, ''),
  (3010, '消息通知', 0, 2, 'student/notify', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'message', 'admin', NOW(), '', NULL, ''),
  (3011, '通知列表', 3010, 1, 'index', 'student/notify/index', NULL, '', 1, 0, 'C', '0', '0', NULL, 'message', 'admin', NOW(), '', NULL, ''),
  (3020, '考勤管理', 0, 3, 'student/attendance', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'time', 'admin', NOW(), '', NULL, ''),
  (3021, '考勤记录', 3020, 1, 'index', 'student/attendance/index', NULL, '', 1, 0, 'C', '0', '0', NULL, 'time', 'admin', NOW(), '', NULL, ''),
  (3030, '课表管理', 0, 4, 'student/schedule', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, 'date', 'admin', NOW(), '', NULL, ''),
  (3031, '课表上传', 3030, 1, 'index', 'student/schedule/index', NULL, '', 1, 0, 'C', '0', '0', NULL, 'upload', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_role_menu (role_id, menu_id)
VALUES
  (200, 3000),
  (200, 3001),
  (200, 3002),
  (200, 3010),
  (200, 3011),
  (200, 3020),
  (200, 3021),
  (200, 3030),
  (200, 3031);
