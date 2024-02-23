package com.example.demo.domain.Alert;

import com.example.demo.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Table(schema = "Alert_setting")
public class AlertSetting extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="alert_setting_id")
    private Long id;

    @Column(name="user_code")
    private Long userCode;

    @Column(name="user_token")
    private String userToken;

    private boolean setting;

    private int breakfastHour;

    private int breakfastMinute;

    private int lunchHour;

    private int lunchMinute;

    private int dinnerHour;

    private int dinnerMinute;

    //==생성자==//
    public AlertSetting(Long userCode, String userToken, boolean setting) {
        this.userCode = userCode;
        this.userToken = userToken;
        this.setting = setting;
        this.breakfastHour = 7;
        this.breakfastMinute = 0;
        this.lunchHour = 13;
        this.lunchMinute = 0;
        this.dinnerHour = 18;
        this.dinnerMinute = 30;
    }


    //==비즈니스 로직==//

    // 회원가입 후 최초 푸시 알람 on 시에 사용
    // 푸시 알람 시간대 변경
    public void changeSetting(String userToken, boolean setting, int breakfastHour,
                              int breakfastMinute, int lunchHour, int lunchMinute, int dinnerHour, int dinnerMinute) {
        this.userToken = userToken;
        this.setting = setting;
        this.breakfastHour = breakfastHour;
        this.breakfastMinute = breakfastMinute;
        this.lunchHour = lunchHour;
        this.lunchMinute = lunchMinute;
        this.dinnerHour = dinnerHour;
        this.dinnerMinute = dinnerMinute;
    }
}



