package com.zcst.manage.controller;

import java.util.List;

import com.zcst.manage.service.ISiqiStudentService;
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
import com.zcst.manage.service.IStudentService;
import com.zcst.common.utils.poi.ExcelUtil;
import com.zcst.common.core.page.TableDataInfo;
import com.zcst.common.core.page.PageDomain;

/**
 * 思齐馆学生管理Controller
 *
 * @author zcst
 * @date 2026-03-19
 */
@RestController
@RequestMapping("/manage/siqiStudent")
public class SiqiStudentController extends BaseController
{
    @Autowired
    private ISiqiStudentService siqiStudentService;

    /**
     * 查询思齐馆学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:siqiStudent:list')")
    @GetMapping("/list")
    public TableDataInfo list(Student student)
    {
        PageDomain pageDomain = getPageDomain();
        return getDataTable(siqiStudentService.selectStudentListWithPage(student, pageDomain.getPageNum(), pageDomain.getPageSize()));
    }

    /**
     * 导出思齐馆学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:siqiStudent:export')")
    @Log(title = "思齐馆学生管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Student student)
    {
        List<StudentVo> list = siqiStudentService.selectStudentList(student);
        ExcelUtil<StudentVo> util = new ExcelUtil<StudentVo>(StudentVo.class);
        util.exportExcel(response, list, "思齐馆学生管理数据");
    }

    /**
     * 获取思齐馆学生管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:siqiStudent:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") String studentId)
    {
        return success(siqiStudentService.selectStudentByStudentId(studentId));
    }

    /**
     * 新增思齐馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:siqiStudent:add')")
    @Log(title = "思齐馆学生管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Student student)
    {
        return toAjax(siqiStudentService.insertStudent(student));
    }

    /**
     * 修改思齐馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:siqiStudent:edit')")
    @Log(title = "思齐馆学生管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Student student)
    {
        return toAjax(siqiStudentService.updateStudent(student));
    }

    /**
     * 删除思齐馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:siqiStudent:remove')")
    @Log(title = "思齐馆学生管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable String[] studentIds)
    {
        return toAjax(siqiStudentService.deleteStudentByStudentIds(studentIds));
    }
}
