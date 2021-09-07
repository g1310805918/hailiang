package com.yunduan.schedule;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.WorkOrder;
import com.yunduan.service.WorkOrderService;
import com.yunduan.utils.DistributionUtil;
import com.yunduan.utils.StatusCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleService {

    @Autowired
    private DistributionUtil distributionUtil;
    @Autowired
    private WorkOrderService workOrderService;


    

    /**
     * 当前时间到达任务结束时间时
     * 使用户正在进行中的订单失败
     * 每 10分钟 执行一次
     */
    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void sysAutoDistributionWorkOrder() {
        //所有等待处理中的工单列表
        List<WorkOrder> workOrderList = workOrderService.list(new QueryWrapper<WorkOrder>().eq("status", StatusCodeUtil.WORK_ORDER_COLLECTION_STATUS));
        if (workOrderList.size() > 0 && workOrderList != null) {
            for (WorkOrder workOrder : workOrderList) {
                //自动分配工单
                distributionUtil.autoDistributionWorkOrderToEngineer(workOrder.getId().toString());
            }
        }
    }



}
