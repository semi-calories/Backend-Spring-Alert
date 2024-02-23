package com.example.demo.dto.Alert.Response;

import com.example.demo.domain.Alert.AlertRecord;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiOperation(
        value = "푸시 알림 기록 조회 응답",
        notes = "푸시 알림 기록 조회 요청에 응답한다.")

@ToString
public class ResponseGetAlertRecordListDto {
    private List<AlertRecord> alertRecordList = new ArrayList<>();
}
