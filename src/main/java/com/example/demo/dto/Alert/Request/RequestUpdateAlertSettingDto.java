package com.example.demo.dto.Alert.Request;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiOperation(
        value = "푸시 알림 설정 수정 요청",
        notes = "사용자가 입력한 푸시 알림 설정 수정을 요청한다.")

@ToString
public class RequestUpdateAlertSettingDto {
    private Long userCode;
    private String userToken;
    private boolean setting;
    private int breakfastHour;
    private int breakfastMinute;
    private int lunchHour;
    private int lunchMinute;
    private int dinnerHour;
    private int dinnerMinute;
}
