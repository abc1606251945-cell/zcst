package com.zcst.manage.domain.Vo;

import com.zcst.manage.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.zcst.system.domain.SysPost;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentVo extends Student {

    /** 岗位列表 */
    private List<SysPost> postList;

    /** 岗位名称拼接 */
    private String postNames;
}
