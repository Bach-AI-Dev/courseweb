package com.baitapnhom.courseweb.repository;

import com.baitapnhom.courseweb.entity.VideoLessons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoLessonsRepository extends JpaRepository <VideoLessons,String> {

}
