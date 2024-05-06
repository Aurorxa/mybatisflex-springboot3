package com.github.vo;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

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
