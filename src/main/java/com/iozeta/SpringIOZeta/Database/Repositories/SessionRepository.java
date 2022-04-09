package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findAllBy();
}
