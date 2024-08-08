package com.example.demo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.EventModel;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Integer> {
	List<EventModel> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDateTime dateTime, LocalDateTime dateTime2);
}