package com.example.demo.dto.Alert.Response;

import com.example.demo.domain.Alert.AlertSetting;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiOperation(
        value = "푸시 알림 설정 조회 응답",
        notes = "푸시 알림 설정 조회 요청에 응답한다.")

@ToString
public class ResponseGetAlertSettingDto {

    private Long userCode;
    private String userToken;
    private boolean setting;
    private int breakfastHour;
    private int breakfastMinute;
    private int lunchHour;
    private int lunchMinute;
    private int dinnerHour;
    private int dinnerMinute;

    public ResponseGetAlertSettingDto(AlertSetting alertSetting) {
        this.userCode = alertSetting.getUserCode();
        this.userToken = alertSetting.getUserToken();
        this.setting = alertSetting.isSetting();
        this.breakfastHour = alertSetting.getBreakfastHour();
        this.breakfastMinute=alertSetting.getBreakfastMinute();
        this.lunchHour=alertSetting.getLunchHour();
        this.lunchMinute=alertSetting.getLunchMinute();
        this.dinnerHour=alertSetting.getDinnerHour();
        this.dinnerMinute=alertSetting.getDinnerMinute();
    }

}
