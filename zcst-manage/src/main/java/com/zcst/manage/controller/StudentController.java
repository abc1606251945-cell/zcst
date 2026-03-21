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

/**
 * 学生管理Controller
 * 
 * @author zcst
 * @date 2026-03-18
 */
@RestController
@RequestMapping("/manage/student")
public class StudentController extends BaseController
{
    @Autowired
    private IStudentService studentService;
    
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 查询学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:student:list')")
    @GetMapping("/list")
    public TableDataInfo list(Student student)
    {
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
