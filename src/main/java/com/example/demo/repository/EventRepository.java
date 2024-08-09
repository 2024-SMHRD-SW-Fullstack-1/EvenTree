package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.EventModel;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Integer> {
	
	// 기존 일정이 존재하는 지 확인 후 존재하면 수정, 존재하지 않으면 추가
	boolean existsById(int eIdx);
	
	// 일정 수정
	@Modifying
    @Transactional
    @Query("UPDATE EventModel e SET e.title = :title, e.startDate = :startDate, e.endDate = :endDate, e.content = :content WHERE e.eIdx = :eIdx")
    void updateEvent(@Param("eIdx") int eIdx,
                     @Param("title") String title,    
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("content") String content);
	
	// 선택된 날짜의 일정 조회 (멤버 조건없이 모두 조회)
	// List<EventModel> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDateTime endOfDay, LocalDateTime startOfDay);
	// 선택된 날짜의 일정 조회 (멤버 조건으로 조회)
	@Query("SELECT e FROM EventModel e WHERE e.startDate <= :endOfDay AND e.endDate >= :startOfDay AND e.mIdx = :mIdx")
    List<EventModel> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndMIdx(
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("mIdx") int mIdx);
	
	// 일정 삭제
	@Modifying
    @Query("DELETE FROM EventModel e WHERE e.eIdx = :eIdx")
    void deleteByEIdx(@Param("eIdx") int eIdx);  
	
	// 아이디에 따른 일정 모든 조회
	@Query("SELECT e FROM EventModel e WHERE e.mIdx = :mIdx")
	List<EventModel> findByMIdx(int mIdx);
}