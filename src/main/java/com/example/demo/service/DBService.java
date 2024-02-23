package com.example.demo.service;

import com.example.demo.domain.DB.DietList;
import com.example.demo.repository.DietListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DBService {

    private final DietListRepository dietListRepository;

    /**
     * 음식 검색
     */
    public DietList findOne(Long foodCode){

        return dietListRepository.findById(foodCode)
                .orElseThrow(()->new IllegalStateException("존재하지 않는 정보입니다.") );

    }

    /**
     * 음식 여러번 검색
     */
    // food code list 통해 DB 읽어와 diet 객체 리스트(Diet List)로 반환
    public List<DietList> findByList(List<Long> foodCodeList) {
        List<DietList> dietList = foodCodeList.stream()
                .map(foodCode -> dietListRepository.findById(foodCode)) //각 코드를통해 dietList 찾기
                .filter(Optional::isPresent) // 찾은 dietList가 존재하는 것만 filter
                .map(Optional::get) // 존재하면 get
                .collect(Collectors.toList());// 리스트로 리턴
        if (dietList.isEmpty()){
            throw new IllegalStateException("인식된 음식의 정보가 없습니다.");
        }
        return dietList;
    }

    /**
     * 음식 이름으로 검색
     */
    public List<DietList> findDietListByName(String param){
        Optional<List<DietList>> foodList = dietListRepository.findByFoodNameContaining(param);
        if (foodList.isPresent()){
            return foodList.get();
        }
        else {
            throw new IllegalArgumentException("내부오류");
        }
    }
}
