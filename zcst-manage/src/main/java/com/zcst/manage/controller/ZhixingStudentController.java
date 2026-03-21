package com.zcst.manage.controller;

import java.util.List;

import com.zcst.manage.service.IZhixingStudentService;
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
 * 知行馆学生管理 Controller
 *
 * @author zcst
 * @date 2026-03-20
 */
@RestController
@RequestMapping("/manage/zhixingStudent")
public class ZhixingStudentController extends BaseController
{
    @Autowired
    private IZhixingStudentService zhixingStudentService;

    /**
     * 查询知行馆学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:zhixingStudent:list')")
    @GetMapping("/list")
    public TableDataInfo list(Student student)
    {
        PageDomain pageDomain = getPageDomain();
        return getDataTable(zhixingStudentService.selectStudentListWithPage(student, pageDomain.getPageNum(), pageDomain.getPageSize()));
    }

    /**
     * 导出知行馆学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:zhixingStudent:export')")
    @Log(title = "知行馆学生管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Student student)
    {
        List<StudentVo> list = zhixingStudentService.selectStudentList(student);
        ExcelUtil<StudentVo> util = new ExcelUtil<StudentVo>(StudentVo.class);
        util.exportExcel(response, list, "知行馆学生管理数据");
    }

    /**
     * 获取知行馆学生管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:zhixingStudent:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") String studentId)
    {
        return success(zhixingStudentService.selectStudentByStudentId(studentId));
    }

    /**
     * 新增知行馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:zhixingStudent:add')")
    @Log(title = "知行馆学生管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Student student)
    {
        return toAjax(zhixingStudentService.insertStudent(student));
    }

    /**
     * 修改知行馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:zhixingStudent:edit')")
    @Log(title = "知行馆学生管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Student student)
    {
        return toAjax(zhixingStudentService.updateStudent(student));
    }

    /**
     * 删除知行馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:zhixingStudent:remove')")
    @Log(title = "知行馆学生管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable String[] studentIds)
    {
        return toAjax(zhixingStudentService.deleteStudentByStudentIds(studentIds));
    }
}