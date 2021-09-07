package com.yunduan.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.Engineer;
import com.yunduan.entity.WorkOrder;
import com.yunduan.service.CommunicationRecordService;
import com.yunduan.service.EngineerService;
import com.yunduan.service.WorkOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * 工单分配工具类
 */
@Component
public class DistributionUtil {

    private static final transient Logger log = LoggerFactory.getLogger(DistributionUtil.class);

    @Autowired
    private EngineerService engineerService;
    @Autowired
    private WorkOrderService workOrderService;
    @Autowired
    private CommunicationRecordService communicationRecordService;


    /**
     * 系统自动分配工单给工程师
     * @param workOrderId 工单id
     */
    public void autoDistributionWorkOrderToEngineer(String workOrderId) {
        //条件构造器
        QueryWrapper<Engineer> queryWrapper = new QueryWrapper<Engineer>()
                .ne("identity", 1)
                .ne("product_category_id", null)
                .eq("account_status", StatusCodeUtil.ENGINEER_ACCOUNT_NORMAL_STATUS)
                .eq("online_status", StatusCodeUtil.ENGINEER_ACCOUNT_ONLINE_STATUS)
                .orderByAsc("order_number");
        //需要分配的工单
        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (workOrder != null) {
            //三级分类id
            String categoryId = workOrder.getCategoryId();
            //1、得到所有在线并且可以处理工单的工程师列表
            List<Engineer> engineerList = engineerService.list(queryWrapper);
            if (engineerList.size() > 0 && engineerList != null) {
                for (Engineer engineer : engineerList) {
                    //当前工程师可以处理的工单分类集合
                    List<String> categoryIds = Arrays.asList(engineer.getProductCategoryId().split(","));
                    //如果当前工程师可以处理的类别中包含当前工单的类别，那么将此工单分配给当前工程师
                    if (categoryIds.contains(categoryId)) {
                        //如果工程师不为空、那么表示之前已经有工程师处理过，但是又被工程师放回系统了
                        if (workOrder.getEngineerId() != null) {
                            //更新工单沟通记录为新分配的工程师
                            communicationRecordService.changeCommunicationRecordEngineerID(workOrder.getId().toString(),engineer.getId().toString());
                        }
                        //更新工程师id、更新工单状态为受理中
                        workOrder.setEngineerId(engineer.getId()).setStatus(StatusCodeUtil.WORK_ORDER_ACCEPT_STATUS);
                        boolean flag = workOrderService.updateById(workOrder);
                        //分配成功后结束循环
                        if (flag) {
                            log.info("【系统消息】系统已将工单 " + workOrderId + "成功分配给 工程师id = " + engineer.getId() + "!!!用户名 = " + engineer.getUsername());
                            break;
                        }
                    }
                }
            }else {
                log.info("暂无可分配工单的在线工程师。正在等待中。。。。。。。。");
            }
        }else {
            log.error("系统自动分配工单给工程师【当前工单不存在】，workOrderId === " + workOrderId);
        }
    }





}
