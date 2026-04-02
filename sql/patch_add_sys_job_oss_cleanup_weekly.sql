INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
SELECT 'OSS 文件清理（保留7天）', 'DEFAULT', 'ossCleanupTask.cleanExpired7Days()', '0 0 3 ? * MON', '3', '1', '0', 'admin', NOW(), ''
WHERE NOT EXISTS (
  SELECT 1 FROM sys_job WHERE job_name = 'OSS 文件清理（保留7天）' AND job_group = 'DEFAULT'
);

