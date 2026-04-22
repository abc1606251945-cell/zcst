package com.zcst.manage.domain.Vo;

import com.zcst.manage.domain.Student;
import com.zcst.system.domain.SysPost;
import com.zcst.common.core.domain.entity.SysRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学生视图对象
 * 
 * @author zcst
 * @date 2026-03-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class StudentVo extends Student {
    // 可以在这里添加额外的字段，用于前端展示
    private String collegeName; // 学院名称
    private String majorName;   // 专业名称
    private String venueName;   // 场馆名称
    private List<SysPost> postList; // 岗位列表
    private String postNames;   // 岗位名称字符串
    private List<SysRole> roleList; // 角色列表
    private String roleNames;   // 角色名称字符串
}
