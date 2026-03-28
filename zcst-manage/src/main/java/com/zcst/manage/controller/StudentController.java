package com.zcst.manage.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zcst.common.annotation.Log;
import com.zcst.common.core.controller.BaseController;
import com.zcst.common.core.domain.AjaxResult;
import com.zcst.common.enums.BusinessType;
import com.github.pagehelper.PageInfo;
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;
import com.zcst.manage.mapper.StudentMapper;
import com.zcst.manage.service.IStudentService;
import com.zcst.common.utils.poi.ExcelUtil;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.core.page.PageDomain;
import com.zcst.common.utils.SecurityUtils;
import com.zcst.common.core.domain.model.LoginUser;
import com.zcst.common.core.domain.entity.SysRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 学生管理 Controller
 * 
 * @author zcst
 * @date 2026-03-18
 */
@RestController
@RequestMapping("/manage/student")
public class StudentController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(StudentController.class);
    
    @Autowired
    private IStudentService studentService;
    
    @Autowired
    private StudentMapper studentMapper;
    
    /**
     * 从用户角色中获取场馆 ID
     * 使用 sys_role 表的 venue_id 字段
     */
    private Integer getVenueIdFromUserRoles() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || loginUser.getUser().getRoles() == null) {
            return null;
        }
        
        return loginUser.getUser().getRoles().stream()
            .filter(role -> role.getVenueId() != null)
            .findFirst()
            .map(SysRole::getVenueId)
            .orElse(null);
    }
    
    /**
     * 判断当前用户是否为超级管理员
     */
    private boolean isSuperAdmin() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return false;
        }
        return "admin".equals(loginUser.getUser().getUserName());
    }

    /**
     * 查询学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:student:list')")
    @GetMapping("/list")
    public TableDataInfo list(Student student)
    {
        // 非超级管理员只能查看自己场馆的学生
        if (!isSuperAdmin()) {
            Integer venueId = getVenueIdFromUserRoles();
            if (venueId != null) {
                student.setVenueId(venueId.longValue());
                log.info("场馆管理员查询学生列表，venueId: {}", venueId);
            }
        }
        PageDomain pageDomain = getPageDomain();
        PageInfo<StudentVo> pageInfo = studentService.selectStudentListWithPage(student, pageDomain.getPageNum(), pageDomain.getPageSize());
        return getDataTable(pageInfo.getList(), pageInfo.getTotal());
    }

    /**
     * 导出学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:student:export')")
    @Log(title = "学生管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Student student)
    {
        // 非超级管理员只能导出自己场馆的学生
        if (!isSuperAdmin()) {
            Integer venueId = getVenueIdFromUserRoles();
            if (venueId != null) {
                student.setVenueId(venueId.longValue());
                log.info("场馆管理员导出学生列表，venueId: {}", venueId);
            }
        }
        List<StudentVo> list = studentService.selectStudentList(student);
        ExcelUtil<StudentVo> util = new ExcelUtil<StudentVo>(StudentVo.class);
        util.exportExcel(response, list, "学生管理数据");
    }

    /**
     * 获取学生管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:student:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") String studentId)
    {
        return success(studentService.selectStudentByStudentId(studentId));
    }

    /**
     * 新增学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:student:add')")
    @Log(title = "学生管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Student student)
    {
        // 非超级管理员添加学生时自动设置场馆 ID
        if (!isSuperAdmin()) {
            Integer venueId = getVenueIdFromUserRoles();
            if (venueId != null) {
                student.setVenueId(venueId.longValue());
                log.info("场馆管理员添加学生，venueId: {}", venueId);
            }
        }
        // 检查学号是否已存在
        if (studentMapper.selectStudentByStudentId(student.getStudentId()) != null) {
            return AjaxResult.error("学号已存在，请重新输入");
        }
        return toAjax(studentService.insertStudent(student));
    }

    /**
     * 修改学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:student:edit')")
    @Log(title = "学生管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Student student)
    {
        // 非超级管理员只能修改自己场馆的学生
        if (!isSuperAdmin()) {
            Integer venueId = getVenueIdFromUserRoles();
            if (venueId != null) {
                student.setVenueId(venueId.longValue());
                log.info("场馆管理员修改学生，venueId: {}", venueId);
            }
        }
        return toAjax(studentService.updateStudent(student));
    }

    /**
     * 删除学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:student:remove')")
    @Log(title = "学生管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable String[] studentIds)
    {
        // 非超级管理员只能删除自己场馆的学生
        if (!isSuperAdmin()) {
            Integer venueId = getVenueIdFromUserRoles();
            if (venueId != null) {
                // 验证要删除的学生是否属于本场馆
                for (String studentId : studentIds) {
                    Student student = studentMapper.selectStudentByStudentId(studentId);
                    if (student != null && !venueId.equals(student.getVenueId().intValue())) {
                        return AjaxResult.error("无权限删除其他场馆的学生");
                    }
                }
                log.info("场馆管理员删除学生，venueId: {}", venueId);
            }
        }
        return toAjax(studentService.deleteStudentByStudentIds(studentIds));
    }

    /**
     * 获取当前学生信息
     */
    @GetMapping("/info")
    public AjaxResult getStudentInfo()
    {
        // 获取当前登录用户的学号
        String studentId = getUsername();
        return success(studentService.selectStudentByStudentId(studentId));
    }

    /**
     * 更新当前学生信息
     */
    @PutMapping("/info")
    public AjaxResult updateStudentInfo(@RequestBody Student student)
    {
        // 获取当前登录用户的学号
        String studentId = getUsername();
        student.setStudentId(studentId);
        return toAjax(studentService.updateStudent(student));
    }
}
