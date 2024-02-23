package com.example.demo.repository;

import com.example.demo.domain.Alert.AlertRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {

    /**
     * 조회 by alert_status & date
     */
    @Query("select ar from AlertRecord ar where ar.alertStatus = :alertStatus and ar.alertDate between :startDatetime and :endDatetime")
    List<AlertRecord> findAllByAlertStatusWithAlertDateBetween(@Param("alertStatus") boolean alertStatus, @Param("startDatetime") String startDatetime, @Param("endDatetime") String endDatetime);

    /**
     * 조회 by user code & alert_status & date
     */
    @Query("select ar from AlertRecord ar left join fetch ar.userCode where  ar.userCode = :userCode and ar.alertStatus = :alertStatus and ar.alertDate between :startDatetime and :endDatetime ")
    List<AlertRecord> findAllByUserCodeAndAlertStatusWithAlertDateBetween(@Param("userCode") Long userCode, @Param("alertStatus") boolean alertStatus, @Param("startDatetime") String startDatetime,
                                                                   @Param("endDatetime") String endDatetime);
    /**
     * 삭제 by user code & alert_status
     */
    @Modifying
    @Query("delete from AlertRecord ar where ar.userCode = :userCode and ar.alertStatus = :alertStatus")
    void deleteByUserCode(@Param("userCode") Long userCode, @Param("alertStatus") Boolean alertStatus);

}