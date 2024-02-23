package com.example.demo.dto.Alert.Request;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiOperation(
        value = "푸시 알림 수신 허용 요청" ,
        notes = "사용자의 푸시 알림 수신 허용을 요청한다.")
public class RequestAlertDto {
    private Long userCode;
    private String userToken;
    private boolean setting;

}
