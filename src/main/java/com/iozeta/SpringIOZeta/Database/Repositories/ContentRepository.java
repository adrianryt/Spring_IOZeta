package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Checkpoint;
import com.iozeta.SpringIOZeta.Database.Entities.utilities.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Checkpoint> findAllBy();
}
