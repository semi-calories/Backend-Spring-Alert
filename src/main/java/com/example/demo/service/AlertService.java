package com.example.demo.service;

import com.example.demo.domain.Alert.AlertRecord;
import com.example.demo.domain.Alert.AlertSetting;
import com.example.demo.dto.Alert.Request.RequestUpdateAlertSettingDto;
import com.example.demo.dto.Recommend.RecommendDto;
import com.example.demo.repository.AlertRecordRepository;
import com.example.demo.repository.AlertSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {
    private final AlertSettingRepository alertSettingRepository;
    private final AlertRecordRepository alertRecordRepository;
    private final AlertRecordService alertRecordService;
    private final DBService dbService;

    /**
     * 푸시 알람 수신 허용
     */
    @Transactional
    public Long saveAlertSetting(AlertSetting alertSetting){
        // alert setting 테이블에 없으면 저장
        if(alertSettingRepository.findByUserCode(alertSetting.getUserCode()).isEmpty()){
            alertSettingRepository.save(alertSetting);
        }
        return alertSetting.getUserCode();
    }

    /**
     * 유저 설정 단건 검색
     */
    public AlertSetting findOne(Long userCode){
        return alertSettingRepository.findByUserCode(userCode)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 설정입니다."));
    }

    /**
     * 푸시 알람 설정 시간 수정
     */
    @Transactional
    public AlertSetting updateAlertSetting(RequestUpdateAlertSettingDto requestUpdateAlertSettingDto){
        AlertSetting alertSetting = findOne(requestUpdateAlertSettingDto.getUserCode());

        // Alert Record 수정 비즈니스 로직

        // 시간대만 수정일 때
        if(requestUpdateAlertSettingDto.isSetting() == alertSetting.isSetting()){
            // 현재 시간부터 다음날 23:59까지 발송되지 않은 모든 Alert Record
            List<AlertRecord> alertRecordList = alertRecordRepository.findAllByUserCodeAndAlertStatusWithAlertDateBetween(requestUpdateAlertSettingDto.getUserCode(), false,
                    getNowLocalDate(), getTomorrowDateTime(23, 59));
            // Alert Record 발송 시간 변경
            changeAllTime(alertRecordList, requestUpdateAlertSettingDto);
        } else {
            
            if(requestUpdateAlertSettingDto.isSetting()){
                // off -> on 일 때
                // 한국 시간대 설정
                ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
                // 현재 한국 시간
                LocalTime currentKoreanTime = LocalTime.now(koreaZoneId);

                // 비교할 시간 (20시 00분)
                // TODO  hard coding 말고 전역변수로 바꾸기
                LocalTime targetTime = LocalTime.of(20, 0);

                // 비교할 시간 (발송 시간들)
                LocalTime targetBreakfast = LocalTime.of(requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute());
                LocalTime targetlunch = LocalTime.of(requestUpdateAlertSettingDto.getLunchHour(), requestUpdateAlertSettingDto.getLunchMinute());
                LocalTime targetDinner = LocalTime.of(requestUpdateAlertSettingDto.getDinnerHour(), requestUpdateAlertSettingDto.getDinnerMinute());

                // db에서 유저 검색
                Long user = requestUpdateAlertSettingDto.getUserCode();

                if (currentKoreanTime.isBefore(targetBreakfast)){
                    // 현재 시간이 아침 발송 시간 전일 경우
                    RecommendDto recommend = requestPushRecommend(requestUpdateAlertSettingDto.getUserCode(), 1).get(0);
                    alertRecordRepository.save(new AlertRecord(user, requestUpdateAlertSettingDto.getUserToken(),
                            getTodayDateTime(requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute()), requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute(),
                            1, dbService.findOne(Long.valueOf(recommend.getFoodCode())),
                            recommend.getFoodName(), recommend.getFoodKcal(),
                            recommend.getFoodCarbon(), recommend.getFoodProtein(), recommend.getFoodFat()));
                }
                if (currentKoreanTime.isBefore(targetlunch)){
                    // 현재 시간이 점심 발송 시간 전일 경우
                    RecommendDto recommend = requestPushRecommend(requestUpdateAlertSettingDto.getUserCode(), 2).get(0);
                    alertRecordRepository.save(new AlertRecord(user, requestUpdateAlertSettingDto.getUserToken(),
                            getTodayDateTime(requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute()), requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute(),
                            2, dbService.findOne(Long.valueOf(recommend.getFoodCode())),
                            recommend.getFoodName(), recommend.getFoodKcal(),
                            recommend.getFoodCarbon(), recommend.getFoodProtein(), recommend.getFoodFat()));
                }
                if (currentKoreanTime.isBefore(targetDinner)){
                    // 현재 시간이 저녁 발송 시간 전일 경우
                    RecommendDto recommend = requestPushRecommend(requestUpdateAlertSettingDto.getUserCode(), 3).get(0);
                    alertRecordRepository.save(new AlertRecord(user, requestUpdateAlertSettingDto.getUserToken(),
                            getTodayDateTime(requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute()), requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute(),
                            3, dbService.findOne(Long.valueOf(recommend.getFoodCode())),
                            recommend.getFoodName(), recommend.getFoodKcal(),
                            recommend.getFoodCarbon(), recommend.getFoodProtein(), recommend.getFoodFat()));
                }

                // 현재 시간이 20시 00분 후일 경우
                if(currentKoreanTime.isAfter(targetTime)){
                    alertSetting.changeSetting(requestUpdateAlertSettingDto.getUserToken(), requestUpdateAlertSettingDto.isSetting(),
                            requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute(),
                            requestUpdateAlertSettingDto.getLunchHour(), requestUpdateAlertSettingDto.getLunchMinute(),
                            requestUpdateAlertSettingDto.getDinnerHour(), requestUpdateAlertSettingDto.getDinnerMinute());

                    alertRecordService.findALlAlertSetting();
                }

                return alertSetting;
            } else{
                // on -> off 일 때
                System.out.println(requestUpdateAlertSettingDto.getUserCode());
                alertRecordRepository.deleteByUserCode(requestUpdateAlertSettingDto.getUserCode(), false);
            }
        }

        alertSetting.changeSetting(requestUpdateAlertSettingDto.getUserToken(), requestUpdateAlertSettingDto.isSetting(),
                requestUpdateAlertSettingDto.getBreakfastHour(), requestUpdateAlertSettingDto.getBreakfastMinute(),
                requestUpdateAlertSettingDto.getLunchHour(), requestUpdateAlertSettingDto.getLunchMinute(),
                requestUpdateAlertSettingDto.getDinnerHour(), requestUpdateAlertSettingDto.getDinnerMinute());
        return alertSetting;
    }
    public void changeAllTime(List<AlertRecord> alertRecordList, RequestUpdateAlertSettingDto requestUpdateAlertSettingDto){
        for(int i = 0; i < alertRecordList.size(); i++){
            AlertRecord alertRecord = alertRecordList.get(i);
            if(alertRecord.getFoodTimes() == 1){
                int newHour = requestUpdateAlertSettingDto.getBreakfastHour();
                int newMinute = requestUpdateAlertSettingDto.getBreakfastMinute();
                alertRecord.changeTime(getModified(alertRecord.getAlertDate(), newHour, newMinute), newHour, newMinute);
            }
            if(alertRecord.getFoodTimes() == 2){
                int newHour = requestUpdateAlertSettingDto.getLunchHour();
                int newMinute = requestUpdateAlertSettingDto.getLunchMinute();
                alertRecord.changeTime(getModified(alertRecord.getAlertDate(), newHour, newMinute), newHour, newMinute);
            }
            if(alertRecord.getFoodTimes() == 3){
                int newHour = requestUpdateAlertSettingDto.getDinnerHour();
                int newMinute = requestUpdateAlertSettingDto.getDinnerMinute();
                alertRecord.changeTime(getModified(alertRecord.getAlertDate(), newHour, newMinute), newHour, newMinute);
            }
        }
    }

    public String getNowLocalDate(){
        // 한국 시간대 설정
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");

        // 지금 날짜 구하기 (한국 시간 기준)
        LocalDateTime now = LocalDateTime.now(koreaZoneId);

        // DateTimeFormatter를 사용하여 LocalDateTime을 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return now.format(formatter);
    }

    public String getTodayDateTime(int hour, int minute){
        // 한국 시간대 설정
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");

        // 내일 날짜 구하기 (한국 시간 기준)
        LocalDate today = LocalDate.now(koreaZoneId);

        // LocalDateTime 만들기
        LocalDateTime tomorrowDateTime = LocalDateTime.of(today, LocalTime.of(hour, minute));

        // DateTimeFormatter를 사용하여 LocalDateTime을 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = tomorrowDateTime.format(formatter);

        return formattedDateTime;
    }

    public String getTomorrowDateTime(int hour, int minute){
        // 한국 시간대 설정
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");

        // 내일 날짜 구하기 (한국 시간 기준)
        LocalDate tomorrow = LocalDate.now(koreaZoneId).plusDays(1);

        // LocalDateTime 만들기
        LocalDateTime tomorrowDateTime = LocalDateTime.of(tomorrow, LocalTime.of(hour, minute));

        // DateTimeFormatter를 사용하여 LocalDateTime을 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = tomorrowDateTime.format(formatter);

        return formattedDateTime;
    }

    public String getModified(String Original, int hour, int Minute) {
        // 문자열을 LocalDateTime으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime originalDTM = LocalDateTime.parse(Original, formatter);
        
        // 새로운 시간과 분으로 수정
        LocalDateTime modifiedDTM = originalDTM.with(ChronoField.HOUR_OF_DAY, hour)
                                                .with(ChronoField.MINUTE_OF_HOUR, Minute);
        return modifiedDTM.format(formatter);
    }


    /**
     * 푸시 알람 발송 기록 조회
     */
    public List<AlertRecord> getRangeRecord(Long userCode, int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        
        LocalDateTime startDatetime = LocalDateTime.of(startYear,startMonth,startDay,0,0);
        LocalDateTime endDatetime = LocalDateTime.of(endYear,endMonth,endDay,23,59);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<AlertRecord> alertRecordList = alertRecordRepository.findAllByUserCodeAndAlertStatusWithAlertDateBetween(userCode, true, startDatetime.format(formatter), endDatetime.format(formatter));
        return alertRecordList;
    }

    public List<RecommendDto> requestPushRecommend(Long UserCode, int eatTimes) {

        // DB에서 해당 정보 가져옴
        // 유저 목표 및 유저 조회
        UserGoal user = userService.findUserWithUserGoal(UserCode);

        // 유저 선호, 비선호, 기록 조회
        List<UserDietPrefer> preferDiet = dietService.findPreferByUserCode(UserCode);
        List<UserDietDislike> dislikeDiet = dietService.findDislikeByUserCode(UserCode);
        List<DietRecord> dietRecords = dietService.findDietRecordByUserCodeAndDate(UserCode, LocalDate.now());


        // FASTAPI 서버에 api 요청
        RequestRecommendAPIDto requestRecommendAPIDto =
                new RequestRecommendAPIDto(user, eatTimes, preferDiet, dislikeDiet, dietRecords);

        ResponseRecommendAPIDto responseAPIDto = fastApiFeign.requestRecommend(requestRecommendAPIDto);

        // FASTAPI 응답 DTO로 list별로 음식 접근가능케 함 (fast api: 인덱스별로 접근)
        List<RecommendDto> recommendDtoList = IntStream.range(0, responseAPIDto.getFoodCodeList().size()) // 음식 추천 수만큼 반복
                .mapToObj(i -> new RecommendDto(
                        responseAPIDto.getFoodCodeList().get(i),
                        responseAPIDto.getFoodNameList().get(i),
                        responseAPIDto.getFoodMainCategoryList().get(i),
                        responseAPIDto.getFoodDetailedClassificationList().get(i),
                        responseAPIDto.getFoodWeightList().get(i),
                        responseAPIDto.getFoodKcalList().get(i),
                        responseAPIDto.getFoodCarbonList().get(i),
                        responseAPIDto.getFoodProteinList().get(i),
                        responseAPIDto.getFoodFatList().get(i)
                ))
                .collect(Collectors.toList());

        // 응답 DTO 생성 및 return
        return recommendDtoList;
    }
}
