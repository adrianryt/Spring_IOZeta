package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Progress;
import com.iozeta.SpringIOZeta.Database.Entities.Session;
import com.iozeta.SpringIOZeta.Database.Entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findAllBy();
    List<Progress> findProgressesBySession(Session session);
    List<Progress> findProgressesBySessionAndStudent(Session session, Student student);
    List<Progress> findAllBySessionId(long sessionId);
}
