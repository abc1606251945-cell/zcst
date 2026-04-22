DROP TRIGGER IF EXISTS tri_student_insert;
DELIMITER ;;
CREATE TRIGGER tri_student_insert
BEFORE INSERT ON student
FOR EACH ROW
BEGIN
  DECLARE v_user_id BIGINT;

  INSERT INTO sys_user (
    dept_id, user_name, nick_name, user_type,
    phonenumber, sex, password, status, create_time
  ) VALUES (
    NULL,
    NEW.student_id,
    NEW.name,
    '02',
    NEW.phone,
    CASE NEW.gender WHEN '男' THEN '0' WHEN '女' THEN '1' ELSE '0' END,
    NEW.password,
    '0',
    NOW()
  );

  SET v_user_id = LAST_INSERT_ID();
  SET NEW.user_id = v_user_id;

  INSERT INTO sys_user_role (user_id, role_id)
  VALUES (v_user_id, 200);

  INSERT INTO permission_assign_log (user_id, username, role_id, role_name, operator, ip_address)
  VALUES (v_user_id, NEW.student_id, 200, '学生', 'system', '127.0.0.1');
END ;;
DELIMITER ;
