package com.zcst.common.constant;

/**
 * 代码生成通用常量
 * 
 * @author ruoyi
 */
public class GenConstants
{
    /** 单表（增删改查） */
    public static final String TPL_CRUD = "crud";

    /** 树表（增删改查） */
    public static final String TPL_TREE = "tree";

    /** 主子表（增删改查） */
    public static final String TPL_SUB = "sub";

    /** 树编码字段 */
    public static final String TREE_CODE = "treeCode";

    /** 树父编码字段 */
    public static final String TREE_PARENT_CODE = "treeParentCode";

    /** 树名称字段 */
    public static final String TREE_NAME = "treeName";

    /** 上级字段 */
    public static final String TREE_PARENT = "treeParent";

    /** 树类型字段 */
    public static final String TREE_TYPE = "treeType";

    /** 树实体字段 */
    public static final String[] TREE_ENTITY = { "children", "parent" };

    /** 基础实体字段 */
    public static final String[] BASE_ENTITY = { "createBy", "createTime", "updateBy", "updateTime", "remark" };

    /** 树类型（普通） */
    public static final String SYS_TREE_TYPE_NORMAL = "0";

    /** 树类型（目录） */
    public static final String SYS_TREE_TYPE_DIR = "1";

    /** 树类型（外部） */
    public static final String SYS_TREE_TYPE_OUTER = "2";

    /** 任务调度 正常 */
    public static final Integer JOB_STATUS_NORMAL = 0;

    /** 任务调度 暂停 */
    public static final Integer JOB_STATUS_SUSPEND = 1;

    /** 任务调度 错误 */
    public static final Integer JOB_STATUS_ERROR = 2;

    /** 任务调度 执行中 */
    public static final Integer JOB_STATUS_RUNNING = 3;

    /** 任务调度 已完成 */
    public static final Integer JOB_STATUS_COMPLETE = 4;

    /** 字典表列名 */
    public static final String SYS_DICT_TYPE = "dictType";

    /** 字典表列名 */
    public static final String SYS_DICT_VALUE = "dictValue";

    /** 字典表列名 */
    public static final String SYS_DICT_LABEL = "dictLabel";

    /** 字典表列名 */
    public static final String SYS_DICT_SORT = "dictSort";

    /** 字典表列名 */
    public static final String SYS_DICT_STATUS = "status";

    /** 字典表列名 */
    public static final String SYS_DICT_REMARK = "remark";

    /** 字典表列名 */
    public static final String SYS_DICT_CREATE_BY = "createBy";

    /** 字典表列名 */
    public static final String SYS_DICT_CREATE_TIME = "createTime";

    /** 字典表列名 */
    public static final String SYS_DICT_UPDATE_BY = "updateBy";

    /** 字典表列名 */
    public static final String SYS_DICT_UPDATE_TIME = "updateTime";

    /** 字符串类型 */
    public static final String TYPE_STRING = "String";

    /** 整型 */
    public static final String TYPE_INTEGER = "Integer";

    /** 长整型 */
    public static final String TYPE_LONG = "Long";

    /** 浮点型 */
    public static final String TYPE_DOUBLE = "Double";

    /** 高精度计算类型 */
    public static final String TYPE_BIGDECIMAL = "BigDecimal";

    /** 日期类型 */
    public static final String TYPE_DATE = "Date";

    /** 文本框 */
    public static final String HTML_INPUT = "input";

    /** 文本域 */
    public static final String HTML_TEXTAREA = "textarea";

    /** 下拉框 */
    public static final String HTML_SELECT = "select";

    /** 单选框 */
    public static final String HTML_RADIO = "radio";

    /** 复选框 */
    public static final String HTML_CHECKBOX = "checkbox";

    /** 日期控件 */
    public static final String HTML_DATETIME = "datetime";

    /** 图片上传控件 */
    public static final String HTML_IMAGE_UPLOAD = "imageUpload";

    /** 文件上传控件 */
    public static final String HTML_FILE_UPLOAD = "fileUpload";

    /** 富文本控件 */
    public static final String HTML_EDITOR = "editor";

    /** 字符串类型列 */
    public static final String[] COLUMNTYPE_STR = { "char", "varchar", "nvarchar", "varchar2" };

    /** 文本类型列 */
    public static final String[] COLUMNTYPE_TEXT = { "tinytext", "text", "mediumtext", "longtext" };

    /** 时间类型列 */
    public static final String[] COLUMNTYPE_TIME = { "datetime", "time", "date", "timestamp" };

    /** 数字类型列 */
    public static final String[] COLUMNTYPE_NUMBER = { "int", "tinyint", "smallint", "mediumint", "integer", "bigint", "decimal", "double", "float" };

    /** 不需要编辑的列名 */
    public static final String[] COLUMNNAME_NOT_EDIT = { "id", "create_by", "create_time", "del_flag", "update_by", "update_time" };

    /** 不需要列表的列名 */
    public static final String[] COLUMNNAME_NOT_LIST = { "id", "create_by", "create_time", "del_flag", "update_by", "update_time" };

    /** 不需要查询的列名 */
    public static final String[] COLUMNNAME_NOT_QUERY = { "id", "create_by", "create_time", "del_flag", "update_by", "update_time", "remark" };

    /** 是否为系统默认（是） */
    public static final String YES = "Y";

    /** 是否（是） */
    public static final String REQUIRE = "1";

    /** 否（否） */
    public static final String NO = "0";

    /** 等于 */
    public static final String QUERY_EQ = "EQ";

    /** 不等于 */
    public static final String QUERY_NE = "NE";

    /** 大于 */
    public static final String QUERY_GT = "GT";

    /** 小于 */
    public static final String QUERY_LT = "LT";

    /** 范围查询 */
    public static final String QUERY_RANGE = "BETWEEN";

    /** 模糊查询 */
    public static final String QUERY_LIKE = "LIKE";

    /** 开始日期 */
    public static final String BEGIN_TIME = "beginTime";

    /** 结束日期 */
    public static final String END_TIME = "endTime";

    /** 代码生成自定义路径 */
    public static final String PATH = "path";

    /** 代码生成压缩包 */
    public static final String ZIP = "zip";

    /** 代码生成模板路径 */
    public static final String TEMPLATE_PATH = "vm";

    /** 代码生成模板后缀 */
    public static final String TEMPLATE_SUFFIX = ".vm";

    /** 代码生成模板 java 后缀 */
    public static final String JAVA_SUFFIX = ".java";

    /** 代码生成模板 html 后缀 */
    public static final String HTML_SUFFIX = ".html";

    /** 代码生成模板 xml 后缀 */
    public static final String XML_SUFFIX = ".xml";

    /** 代码生成模板 js 后缀 */
    public static final String JS_SUFFIX = ".js";

    /** 代码生成模板 vue 后缀 */
    public static final String VUE_SUFFIX = ".vue";

    /** 代码生成模板 sql 后缀 */
    public static final String SQL_SUFFIX = ".sql";

    /** 代码生成模板 txt 后缀 */
    public static final String TXT_SUFFIX = ".txt";

    /** 代码生成模板 json 后缀 */
    public static final String JSON_SUFFIX = ".json";

    /** 代码生成模板 md 后缀 */
    public static final String MD_SUFFIX = ".md";

    /** 代码生成模板 properties 后缀 */
    public static final String PROPERTIES_SUFFIX = ".properties";

    /** 代码生成模板 yml 后缀 */
    public static final String YML_SUFFIX = ".yml";

    /** 代码生成模板 yaml 后缀 */
    public static final String YAML_SUFFIX = ".yaml";

    /** 代码生成模板 sh 后缀 */
    public static final String SH_SUFFIX = ".sh";

    /** 代码生成模板 bat 后缀 */
    public static final String BAT_SUFFIX = ".bat";

    /** 代码生成模板 cmd 后缀 */
    public static final String CMD_SUFFIX = ".cmd";

    /** 代码生成模板 ps1 后缀 */
    public static final String PS1_SUFFIX = ".ps1";

    /** 代码生成模板 psd1 后缀 */
    public static final String PSD1_SUFFIX = ".psd1";

    /** 代码生成模板 psm1 后缀 */
    public static final String PSM1_SUFFIX = ".psm1";

    /** 代码生成模板 pssc 后缀 */
    public static final String PSSC_SUFFIX = ".pssc";

    /** 代码生成模板 psxml 后缀 */
    public static final String PSXML_SUFFIX = ".psxml";

    /** 代码生成模板 psc1 后缀 */
    public static final String PSC1_SUFFIX = ".psc1";

    /** 代码生成模板 cdxml 后缀 */
    public static final String CDXML_SUFFIX = ".cdxml";

    /** 代码生成模板 mof 后缀 */
    public static final String MOF_SUFFIX = ".mof";

    /** 代码生成模板 mofc 后缀 */
    public static final String MOFC_SUFFIX = ".mofc";

    /** 代码生成模板 wmimof 后缀 */
    public static final String WMIMOF_SUFFIX = ".wmimof";

    /** 代码生成模板 wmi 后缀 */
    public static final String WMI_SUFFIX = ".wmi";

    /** 代码生成模板 wbem 后缀 */
    public static final String WBEM_SUFFIX = ".wbem";

    /** 代码生成模板 cim 后缀 */
    public static final String CIM_SUFFIX = ".cim";

    /** 代码生成模板 dmof 后缀 */
    public static final String DMOF_SUFFIX = ".dmof";

    /** 代码生成模板 dmofc 后缀 */
    public static final String DMOFC_SUFFIX = ".dmofc";

    /** 代码生成模板 wdm 后缀 */
    public static final String WDM_SUFFIX = ".wdm";

    /** 代码生成模板 wdf 后缀 */
    public static final String WDF_SUFFIX = ".wdf";

    /** 代码生成模板 umdf 后缀 */
    public static final String UMDF_SUFFIX = ".umdf";

    /** 代码生成模板 kmf 后缀 */
    public static final String KMF_SUFFIX = ".kmf";

    /** 代码生成模板 kmdf 后缀 */
    public static final String KMDF_SUFFIX = ".kmdf";

    /** 代码生成模板 umf 后缀 */
    public static final String UMF_SUFFIX = ".umf";

    /** 代码生成模板 umdf 后缀 */
    public static final String UMDF2_SUFFIX = ".umdf2";

    /** 代码生成模板 kmdf 后缀 */
    public static final String KMDF2_SUFFIX = ".kmdf2";

    /** 代码生成模板 wdf2 后缀 */
    public static final String WDF2_SUFFIX = ".wdf2";

    /** 代码生成模板 wdm2 后缀 */
    public static final String WDM2_SUFFIX = ".wdm2";

    /** 代码生成模板 dmof2 后缀 */
    public static final String DMOF2_SUFFIX = ".dmof2";

    /** 代码生成模板 dmofc2 后缀 */
    public static final String DMOFC2_SUFFIX = ".dmofc2";

    /** 代码生成模板 wmimof2 后缀 */
    public static final String WMIMOF2_SUFFIX = ".wmimof2";

    /** 代码生成模板 wmi2 后缀 */
    public static final String WMI2_SUFFIX = ".wmi2";

    /** 代码生成模板 wbem2 后缀 */
    public static final String WBEM2_SUFFIX = ".wbem2";

    /** 代码生成模板 cim2 后缀 */
    public static final String CIM2_SUFFIX = ".cim2";

    /** 代码生成模板 mof2 后缀 */
    public static final String MOF2_SUFFIX = ".mof2";

    /** 代码生成模板 mofc2 后缀 */
    public static final String MOFC2_SUFFIX = ".mofc2";

    /** 代码生成模板 ps1_2 后缀 */
    public static final String PS1_2_SUFFIX = ".ps1_2";

    /** 代码生成模板 psd1_2 后缀 */
    public static final String PSD1_2_SUFFIX = ".psd1_2";

    /** 代码生成模板 psm1_2 后缀 */
    public static final String PSM1_2_SUFFIX = ".psm1_2";

    /** 代码生成模板 pssc_2 后缀 */
    public static final String PSSC_2_SUFFIX = ".pssc_2";

    /** 代码生成模板 psxml_2 后缀 */
    public static final String PSXML_2_SUFFIX = ".psxml_2";

    /** 代码生成模板 psc1_2 后缀 */
    public static final String PSC1_2_SUFFIX = ".psc1_2";

    /** 代码生成模板 cdxml_2 后缀 */
    public static final String CDXML_2_SUFFIX = ".cdxml_2";

    /** 代码生成模板 mof_2 后缀 */
    public static final String MOF_2_SUFFIX = ".mof_2";

    /** 代码生成模板 mofc_2 后缀 */
    public static final String MOFC_2_SUFFIX = ".mofc_2";

    /** 代码生成模板 wmimof_2 后缀 */
    public static final String WMIMOF_2_SUFFIX = ".wmimof_2";

    /** 代码生成模板 wmi_2 后缀 */
    public static final String WMI_2_SUFFIX = ".wmi_2";

    /** 代码生成模板 wbem_2 后缀 */
    public static final String WBEM_2_SUFFIX = ".wbem_2";

    /** 代码生成模板 cim_2 后缀 */
    public static final String CIM_2_SUFFIX = ".cim_2";

    /** 代码生成模板 dmof_2 后缀 */
    public static final String DMOF_2_SUFFIX = ".dmof_2";

    /** 代码生成模板 dmofc_2 后缀 */
    public static final String DMOFC_2_SUFFIX = ".dmofc_2";

    /** 代码生成模板 wdm_2 后缀 */
    public static final String WDM_2_SUFFIX = ".wdm_2";

    /** 代码生成模板 wdf_2 后缀 */
    public static final String WDF_2_SUFFIX = ".wdf_2";

    /** 代码生成模板 umdf_2 后缀 */
    public static final String UMDF_2_SUFFIX = ".umdf_2";

    /** 代码生成模板 kmf_2 后缀 */
    public static final String KMF_2_SUFFIX = ".kmf_2";

    /** 代码生成模板 kmdf_2 后缀 */
    public static final String KMDF_2_SUFFIX = ".kmdf_2";

    /** 代码生成模板 umf_2 后缀 */
    public static final String UMF_2_SUFFIX = ".umf_2";

    /** 代码生成模板 umdf2_2 后缀 */
    public static final String UMDF2_2_SUFFIX = ".umdf2_2";

    /** 代码生成模板 kmdf2_2 后缀 */
    public static final String KMDF2_2_SUFFIX = ".kmdf2_2";

    /** 代码生成模板 wdf2_2 后缀 */
    public static final String WDF2_2_SUFFIX = ".wdf2_2";

    /** 代码生成模板 wdm2_2 后缀 */
    public static final String WDM2_2_SUFFIX = ".wdm2_2";

    /** 代码生成模板 dmof2_2 后缀 */
    public static final String DMOF2_2_SUFFIX = ".dmof2_2";

    /** 代码生成模板 dmofc2_2 后缀 */
    public static final String DMOFC2_2_SUFFIX = ".dmofc2_2";

    /** 代码生成模板 wmimof2_2 后缀 */
    public static final String WMIMOF2_2_SUFFIX = ".wmimof2_2";

    /** 代码生成模板 wmi2_2 后缀 */
    public static final String WMI2_2_SUFFIX = ".wmi2_2";

    /** 代码生成模板 wbem2_2 后缀 */
    public static final String WBEM2_2_SUFFIX = ".wbem2_2";

    /** 代码生成模板 cim2_2 后缀 */
    public static final String CIM2_2_SUFFIX = ".cim2_2";

    /** 代码生成模板 mof2_2 后缀 */
    public static final String MOF2_2_SUFFIX = ".mof2_2";

    /** 代码生成模板 mofc2_2 后缀 */
    public static final String MOFC2_2_SUFFIX = ".mofc2_2";

    /** 上级菜单 ID */
    public static final String PARENT_MENU_ID = "parentMenuId";

    /** 上级菜单名称 */
    public static final String PARENT_MENU_NAME = "parentMenuName";
}
