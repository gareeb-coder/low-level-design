package io.kp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public class Period {

    private Date startDate;
    private Date endDate;

}
