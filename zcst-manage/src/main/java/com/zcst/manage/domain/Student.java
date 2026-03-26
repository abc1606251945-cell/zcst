package com.zcst.manage.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.zcst.common.annotation.Excel;
import com.zcst.common.core.domain.BaseEntity;

/**
 * 学生管理对象 student
 * 
 * @author zcst
 * @date 2026-03-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class Student extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 学号 */
    private String studentId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 性别（男/女） */
    @Excel(name = "性别", readConverterExp = "男=/女")
    private String gender;

    /** 专业ID */
    @Excel(name = "专业ID")
    private Long majorId;

    /** 年级 */
    @Excel(name = "年级")
    private String grade;

    /** 手机号码 */
    @Excel(name = "手机号码")
    private String phone;

    /** 场馆 */
    @Excel(name = "场馆")
    private Long venueId;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;

    /** 角色ID列表 */
    private Long[] roleIds;

    /** 岗位ID列表 */
    private Long[] postIds;

}
