package com.zcst.common.core.page;

import com.zcst.common.core.text.Convert;
import com.zcst.common.utils.ServletUtils;

/**
 * 表格数据处理
 * 
 * @author ruoyi
 */
public class TableSupport
{
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain()
    {
        PageDomain pageDomain = new PageDomain();
        // 优先获取page和limit参数，兼容前端分页组件
        Integer pageNum = Convert.toInt(ServletUtils.getParameter("page"), 0);
        Integer pageSize = Convert.toInt(ServletUtils.getParameter("limit"), 0);
        
        // 如果page和limit参数不存在，使用pageNum和pageSize参数
        if (pageNum == 0) {
            pageNum = Convert.toInt(ServletUtils.getParameter(PAGE_NUM), 1);
        }
        if (pageSize == 0) {
            pageSize = Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10);
        }
        
        pageDomain.setPageNum(pageNum);
        pageDomain.setPageSize(pageSize);
        pageDomain.setOrderByColumn(ServletUtils.getParameter(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(ServletUtils.getParameter(IS_ASC));
        pageDomain.setReasonable(ServletUtils.getParameterToBool(REASONABLE));
        return pageDomain;
    }

    public static PageDomain buildPageRequest()
    {
        return getPageDomain();
    }
}
