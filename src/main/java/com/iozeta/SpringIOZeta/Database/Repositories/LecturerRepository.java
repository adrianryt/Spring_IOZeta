package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
        List<Lecturer> findAllBy();
        Lecturer findByNameAndSurname(String name, String surname);
        List<Lecturer> findAllByName(String name);
        Lecturer findLecturerByGitNick(String gitNick);
        Lecturer getLecturerById(Long id);
        Lecturer findByGitNick(String gitNick);
}