package com.yunduan.vo;

import cn.hutool.core.util.StrUtil;
import com.yunduan.utils.MatchDataUtil;
import com.yunduan.validator.IsMobile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * IsMobile 注解校验规则
 * 具体实现类
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {

    //是否是必填项
    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (required) {
            return MatchDataUtil.matchDataType(value) == 1 ? true : false;
        }else {
            //非必填
            if (StrUtil.hasEmpty(value)) {
                return true;
            }else {
                return MatchDataUtil.matchDataType(value) == 1 ? true : false;
            }
        }
    }

}
