package com.zcst.manage.controller;

import java.util.List;

import com.zcst.manage.service.IXinyuanStudentService;
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

/**
 * 心缘馆学生管理 Controller
 *
 * @author zcst
 * @date 2026-03-20
 */
@RestController
@RequestMapping("/manage/xinyuanStudent")
public class XinyuanStudentController extends BaseController
{
    @Autowired
    private IXinyuanStudentService xinyuanStudentService;

    /**
     * 查询心缘馆学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:xinyuanStudent:list')")
    @GetMapping("/list")
    public TableDataInfo list(Student student)
    {
        return getDataTable(xinyuanStudentService.selectStudentListWithPage(student));
    }

    /**
     * 导出心缘馆学生管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:xinyuanStudent:export')")
    @Log(title = "心缘馆学生管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Student student)
    {
        List<StudentVo> list = xinyuanStudentService.selectStudentList(student);
        ExcelUtil<StudentVo> util = new ExcelUtil<StudentVo>(StudentVo.class);
        util.exportExcel(response, list, "心缘馆学生管理数据");
    }

    /**
     * 获取心缘馆学生管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:xinyuanStudent:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") String studentId)
    {
        return success(xinyuanStudentService.selectStudentByStudentId(studentId));
    }

    /**
     * 新增心缘馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:xinyuanStudent:add')")
    @Log(title = "心缘馆学生管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Student student)
    {
        return toAjax(xinyuanStudentService.insertStudent(student));
    }

    /**
     * 修改心缘馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:xinyuanStudent:edit')")
    @Log(title = "心缘馆学生管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Student student)
    {
        return toAjax(xinyuanStudentService.updateStudent(student));
    }

    /**
     * 删除心缘馆学生管理
     */
    @PreAuthorize("@ss.hasPermi('manage:xinyuanStudent:remove')")
    @Log(title = "心缘馆学生管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable String[] studentIds)
    {
        return toAjax(xinyuanStudentService.deleteStudentByStudentIds(studentIds));
    }
}