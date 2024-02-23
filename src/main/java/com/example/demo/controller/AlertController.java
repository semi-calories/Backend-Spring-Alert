package com.example.demo.controller;

import com.example.demo.domain.Alert.AlertRecord;
import com.example.demo.domain.Alert.AlertSetting;
import com.example.demo.dto.Alert.Request.RequestAlertDto;
import com.example.demo.dto.Alert.Request.RequestAlertSettingDto;
import com.example.demo.dto.Alert.Request.RequestUpdateAlertSettingDto;
import com.example.demo.dto.Alert.Response.ResponseGetAlertRecordListDto;
import com.example.demo.dto.Alert.Response.ResponseGetAlertSettingDto;
import com.example.demo.service.AlertService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/alert")
public class AlertController {
    private final AlertService alertService;

    /**
     * 푸시 알람 수신 허용
     */
    @PostMapping("/saveSetting")
    public ReturnDto saveAlertSetting(@RequestBody RequestAlertDto requestAlertDto) throws Exception {


        // entity 생성
        AlertSetting alertSetting = new AlertSetting(requestAlertDto.getUserCode(), requestAlertDto.getUserToken(), requestAlertDto.isSetting());

        // alert setting 에 저장
        alertService.saveAlertSetting(alertSetting);

        return new ReturnDto<>(true);
    }

    /**
     * 푸시 알람 설정 조회
     */
    @PostMapping("/getSetting")
    public ResponseGetAlertSettingDto getAlertSetting(@RequestBody RequestAlertSettingDto requestAlertSettingDto) throws Exception {
        // 기본 정보 조회
        AlertSetting alertSetting = alertService.findOne(requestAlertSettingDto.getUserCode());

        // 응답 DTO 생성
        ResponseGetAlertSettingDto responseGetAlertSettingDto = new ResponseGetAlertSettingDto(alertSetting);

        return responseGetAlertSettingDto;
    }


    /**
     * 푸시 알람 수신 시간 or 수신 여부 설정 변경
     */
    @PostMapping("/updateSetting")
    public ReturnDto updateAlertSetting(@RequestBody RequestUpdateAlertSettingDto requestUpdateAlertSettingDto) throws Exception {
        // 푸시 알림 시간 설정 변경
        alertService.updateAlertSetting(requestUpdateAlertSettingDto);

        return new ReturnDto<>(true);
    }


    /**
     * 푸시 알람 발송 기록 조회
     */
    @GetMapping("/getAlertRecord")
    public ResponseGetAlertRecordListDto getAlertRecord(Long userCode, int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) throws Exception {

        // 기본 정보 조회
        List<AlertRecord> alertRecordList = alertService.getRangeRecord(userCode, startYear, startMonth, startDay, endYear, endMonth, endDay);

        // 응답 DTO 생성
        return new ResponseGetAlertRecordListDto(alertRecordList);
    }

    @AllArgsConstructor
    @Getter
    static class ReturnDto<T>{
        private T response;
    }
}
