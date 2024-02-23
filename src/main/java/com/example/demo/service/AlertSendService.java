package com.example.demo.service;

import com.example.demo.domain.Alert.AlertRecord;
import com.example.demo.repository.AlertRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertSendService {
    private final AlertRecordRepository alertRecordRepository;

    /**
     * 10초 후 실행
     */
    @Transactional
    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void findALlAlertSetting() {
        // 한국 시간대 설정
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");

        // 지금 날짜 구하기 (한국 시간 기준)
        LocalDateTime now = LocalDateTime.now(koreaZoneId);

        // 1분 뒤
        LocalDateTime nextMinute = now.plus(1, ChronoUnit.MINUTES);

        // DateTimeFormatter를 사용하여 LocalDateTime을 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);
        String formattedNext = nextMinute.format(formatter);

        // TODO 페이징 처리
        List<AlertRecord> alertRecordList = alertRecordRepository.findAllByAlertStatusWithAlertDateBetween(false, formattedNow, formattedNext);

        for (int i = 0; i < alertRecordList.size(); i++) {
            AlertRecord alertRecord = alertRecordList.get(i);
            if (sendExpoPushNotification(alertRecord)) {
                //발송이 잘 된 것
                alertRecord.changeDtm(true, now);
            } else {
                // TODO : ERROR MESSAGE DB에 넣기
            }
        }
    }

    private boolean sendExpoPushNotification(AlertRecord alertRecord) {
        // Expo push notification을 보내기 위한 Expo 서버 API 엔드포인트
        String expoApiEndpoint = "https://exp.host/--/api/v2/push/send";

        String alertdate = alertRecord.getAlertDate();
        String[] dateList = getMonthAndDay(alertdate);
        String[] BLDList = {"잘못됨", "아침", "점심", "저녁"};

        int hour = alertRecord.getAlertHour();
        int minute = alertRecord.getAlertMinute();

        String title = String.format("%s월 %s일 %s %d시 %d분", dateList[1], dateList[2], BLDList[alertRecord.getFoodTimes()], hour, minute);
        //→ Message Title : MM월 dd일 (아/점/저) N시 N분
        String body = String.format("%s %.1f kcal", alertRecord.getFoodName(), alertRecord.getFoodKcal());
        //→ Message Body : 음식이름 칼로리 kcal

        // Expo 서버에 전송할 데이터 (푸시 알림 내용 등)
        String expoRequestBody = String.format("{ \"to\": \"%s\", \"title\": \"%s\", \"body\": \"%s\" }",
                alertRecord.getUserToken(), title, body);

        // Expo 서버로 HTTP POST 요청을 보냄
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(expoRequestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate().exchange(expoApiEndpoint, HttpMethod.POST, requestEntity, String.class);


        //TODO 존재하지 않는 토큰 예외처리 org.springframework.web.client.HttpClientErrorException$NotFound: 404 Not Found: "Not Found"
        //2023-11-28T19:12:48.073+09:00 DEBUG 1396 --- [   scheduling-1] o.s.web.client.RestTemplate              : HTTP POST https://exp.host/--/api/v2/push/send
        //2023-11-28T19:12:48.088+09:00 DEBUG 1396 --- [   scheduling-1] o.s.web.client.RestTemplate              : Accept=[text/plain, application/json, application/cbor, application+json, *]
        //2023-11-28T19:12:48.092+09:00 DEBUG 1396 --- [   scheduling-1] o.s.web.client.RestTemplate              : Writing [{ "to": ""ExponentPushToken[Cgd7MKMqDELU7ge5lTBj05]"", "title": "11월 29일 저녁 18시 48분", "body": "불고기 395.30 kcal" }] as "application/json"
        //2023-11-28T19:12:48.840+09:00 DEBUG 1396 --- [   scheduling-1] o.s.web.client.RestTemplate              : Response 404 NOT_FOUND
        //2023-11-28T19:12:49.448+09:00 ERROR 1396 --- [   scheduling-1] o.s.s.s.TaskUtils$LoggingErrorHandler    : Unexpected error occurred in scheduled task

        // Expo 서버 응답 확인
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            System.out.println("Expo push notification sent successfully");
            return true;
        }
        System.err.println("Failed to send Expo push notification. Response: " + responseEntity.getBody());
        return false;
    }

    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String[] getMonthAndDay(String date) {
        String[] eatDateList = date.split(" ");
        String[] dateList = eatDateList[0].split("-"); // "2023-09-11"
        return dateList;
    }
}