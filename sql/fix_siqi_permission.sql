-- 为思齐馆管理员角色添加值班表权限

-- 1. 首先查看思齐馆管理员角色ID
SELECT role_id, role_name FROM sys_role WHERE role_name LIKE '%思齐馆%';

-- 2. 查看现有的值班表权限
SELECT perm_id, perm_name, perm_key FROM sys_permission WHERE perm_key LIKE 'manage:dutySchedule:%';

-- 3. 为思齐馆管理员角色添加缺失的权限
-- 假设思齐馆管理员角色ID为100
INSERT IGNORE INTO sys_role_permission (role_id, perm_id) VALUES
-- 基础查询权限
(100, (SELECT perm_id FROM sys_permission WHERE perm_key = 'manage:dutySchedule:list')),
(100, (SELECT perm_id FROM sys_permission WHERE perm_key = 'manage:dutySchedule:query')),
-- 编辑权限
(100, (SELECT perm_id FROM sys_permission WHERE perm_key = 'manage:dutySchedule:add')),
(100, (SELECT perm_id FROM sys_permission WHERE perm_key = 'manage:dutySchedule:edit')),
(100, (SELECT perm_id FROM sys_permission WHERE perm_key = 'manage:dutySchedule:remove'));

-- 4. 验证权限是否添加成功
SELECT r.role_name, p.perm_key 
FROM sys_role r
JOIN sys_role_permission rp ON r.role_id = rp.role_id
JOIN sys_permission p ON rp.perm_id = p.perm_id
WHERE r.role_name LIKE '%思齐馆%' AND p.perm_key LIKE 'manage:dutySchedule:%';
