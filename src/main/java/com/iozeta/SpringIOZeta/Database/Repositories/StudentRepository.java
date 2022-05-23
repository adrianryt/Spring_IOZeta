package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllBy();
    Student findByGitNick(String gitNickname);
    List<Student> findStudentsByGitNick(String gitNickname);
    Optional<Student> findById(Long id);
    Optional<Student> findByBranchName(String branchName);

}
