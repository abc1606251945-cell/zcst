UPDATE sys_menu
SET route_name = 'AttendanceStatsIndex'
WHERE menu_id = 2139
  AND (route_name IS NULL OR route_name = '');
