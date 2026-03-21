package com.zcst.manage.controller;

import java.util.List;

import com.zcst.manage.service.IGuofangStudentService;
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
import com.zcst.manage.domain.Student;
import com.zcst.manage.domain.Vo.StudentVo;
import com.zcst.common.utils.poi.ExcelUtil;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.core.page.PageDomain;

/**
 * 国防教育体验馆学生管理 Controller
 *
 * @author zcst
 * @date 2026-03-20
 */
@RestController
@RequestMapping("/manage/guofangStudent")
public class GuofangStudentController extends BaseController
{
    @Autowired
    private IGuofangStudentService guofangStudentService;

    /**
     * 查询国防教育体验馆学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:guofangStudent:list')")
    @GetMapping("/list")
    public TableDataInfo list(Student student)
    {
        startPage();
        List<StudentVo> list = guofangStudentService.selectStudentList(student);
        return getDataTable(list);
    }

    /**
     * 导出国防教育体验馆学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:guofangStudent:export')")
    @Log(title = "国防教育体验馆学生管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Student student)
    {
        List<StudentVo> list = guofangStudentService.selectStudentList(student);
        ExcelUtil<StudentVo> util = new ExcelUtil<StudentVo>(StudentVo.class);
        util.exportExcel(response, list, "国防教育体验馆学生管理数据");
    }

    /**
     * 获取国防教育体验馆学生管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:guofangStudent:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") String studentId)
    {
        return success(guofangStudentService.selectStudentByStudentId(studentId));
    }

    /**
     * 新增国防教育体验馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:guofangStudent:add')")
    @Log(title = "国防教育体验馆学生管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Student student)
    {
        return toAjax(guofangStudentService.insertStudent(student));
    }

    /**
     * 修改国防教育体验馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:guofangStudent:edit')")
    @Log(title = "国防教育体验馆学生管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Student student)
    {
        return toAjax(guofangStudentService.updateStudent(student));
    }

    /**
     * 删除国防教育体验馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:guofangStudent:remove')")
    @Log(title = "国防教育体验馆学生管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable String[] studentIds)
    {
        return toAjax(guofangStudentService.deleteStudentByStudentIds(studentIds));
    }
}