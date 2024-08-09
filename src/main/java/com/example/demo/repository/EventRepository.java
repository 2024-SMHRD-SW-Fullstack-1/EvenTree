package com.example.demo.repository;

<<<<<<< HEAD
import java.time.LocalDate;
=======
>>>>>>> e143bb5789134809edd8d1007eff39ca138cc3c0
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
=======
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
>>>>>>> e143bb5789134809edd8d1007eff39ca138cc3c0

import com.example.demo.model.EventModel;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Integer> {
<<<<<<< HEAD
	List<EventModel> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDateTime endOfDay, LocalDateTime startOfDay);
=======
	
	// 기존 이벤트가 존재하는 지 확인 후 존재하면 수정, 존재하지 않으면 추가
	boolean existsById(int eIdx);
	
	// 선택된 날짜의 이벤트 조회
	List<EventModel> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDateTime endOfDay, LocalDateTime startOfDay);
	
	@Modifying
    @Query("DELETE FROM EventModel e WHERE e.eIdx = :eIdx")
    void deleteByEIdx(@Param("eIdx") int eIdx);  
	
	@Modifying
    @Transactional
    @Query("UPDATE EventModel e SET e.title = :title, e.startDate = :startDate, e.endDate = :endDate, e.content = :content WHERE e.eIdx = :eIdx")
    void updateEvent(@Param("eIdx") int eIdx,
                     @Param("title") String title,    
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("content") String content);
>>>>>>> e143bb5789134809edd8d1007eff39ca138cc3c0
}