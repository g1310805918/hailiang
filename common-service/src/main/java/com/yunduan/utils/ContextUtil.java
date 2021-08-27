package com.yunduan.utils;


/**
 * @author ：qinchengnian
 * @date ：Created in 2020/7/27 11:46
 * @description：上下文
 * @modified By：
 * @version: $
 */
public class ContextUtil {

    /**
     * 当前登录用户
     */
    private static ThreadLocal<Long> userId = new ThreadLocal<>();
    public static void setUserId(Long id) {userId.set(id);}
    public static Long getUserId(){return userId.get();}
    public static void removeUserId(){userId.remove();}

    /**
     * 当前设备信息
     */
    private static ThreadLocal<EquipmentSourceInformation> equipmentInformation = new ThreadLocal<>();
    public static void setEquipmentInformation(EquipmentSourceInformation info) {equipmentInformation.set(info);}
    public static EquipmentSourceInformation getEquipmentInformation(){return equipmentInformation.get();}
    public static void removeEquipmentInformation(){equipmentInformation.remove();}

}
