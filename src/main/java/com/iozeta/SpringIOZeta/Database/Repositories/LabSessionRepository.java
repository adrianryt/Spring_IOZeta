package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabSessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllBy();
    Session findSessionById(Long id);
    Session findSessionByAccessCode(String accessCode);
    Session findSessionByName(String name);
    List<Session> findSessionsByTaskId(Long taskId);

}
