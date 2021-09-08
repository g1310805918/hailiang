package com.yunduan.schedule;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunduan.entity.WorkOrder;
import com.yunduan.service.WorkOrderService;
import com.yunduan.utils.DistributionUtil;
import com.yunduan.utils.StatusCodeUtil;
import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduleService {

    private static final transient Logger log = LoggerFactory.getLogger(ScheduleService.class);


    @Autowired
    private DistributionUtil distributionUtil;
    @Autowired
    private WorkOrderService workOrderService;


    /**
     * 系统每间隔10分钟分配一次工单。
     * 每 10分钟 执行一次
     */
    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void sysAutoDistributionWorkOrder() {
        //所有等待处理中的工单列表
        List<WorkOrder> workOrderList = workOrderService.list(new QueryWrapper<WorkOrder>().eq("status", StatusCodeUtil.WORK_ORDER_PROCESS_STATUS));
        if (workOrderList.size() > 0 && workOrderList != null) {
            for (WorkOrder workOrder : workOrderList) {
                //自动分配工单
                try {
                    distributionUtil.autoDistributionWorkOrderToEngineer(workOrder.getId().toString());
                } catch (Exception e) {
                    log.error("系统间隔10分钟分配工单时错误");
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }



}
