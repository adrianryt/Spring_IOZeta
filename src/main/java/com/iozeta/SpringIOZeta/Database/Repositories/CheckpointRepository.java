package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckpointRepository extends JpaRepository<Checkpoint, Integer> {
    List<Checkpoint> findAllBy();
}
