package com.github.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class AccountVo {

    private Long id;

    private String userName;

    private Date birthday;

    private Date createTime;

    private Boolean isAudit;

    private BigDecimal money;

}