package com.yunduan.vo.back;

import cn.hutool.core.bean.BeanUtil;
import com.yunduan.entity.Permission;

public class VoUtil {

    public static MenuVo permissionToMenuVo(Permission p) {
        MenuVo menuVo = new MenuVo();
        BeanUtil.copyProperties(p, menuVo);
        return menuVo;
    }

}
