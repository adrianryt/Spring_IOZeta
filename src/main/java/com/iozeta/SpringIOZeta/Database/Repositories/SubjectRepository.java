package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllBy();
    Subject getSubjectById(Long id);
    Subject getSubjectByNameAndLecturer(String name, Lecturer lecturer);
    List<Subject> findSubjectsByLecturer(Lecturer lecturer);
}
