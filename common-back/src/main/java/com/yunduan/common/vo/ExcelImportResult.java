package com.yunduan.common.vo;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.util.List;


@Data
public class ExcelImportResult {

    Integer successCount;
    List<Error> errorList = CollectionUtil.newArrayList();

    public ExcelImportResult() {
    }

    public ExcelImportResult(Integer successCount, List<Error> errorList) {
        this.successCount = successCount;
        this.errorList = errorList;
    }

    @Data
    public static class Error {
        Integer row;
        String message;

        public Error(Integer row, String message) {
            this.row = row;
            this.message = message;
        }

        public Error() {
        }
    }

}
