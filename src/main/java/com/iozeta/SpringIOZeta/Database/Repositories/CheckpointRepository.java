package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Checkpoint;
import com.iozeta.SpringIOZeta.Database.Entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckpointRepository extends JpaRepository<Checkpoint, Long> {
    List<Checkpoint> findAllBy();
    List<Checkpoint> findAllByTask(Task task);
}
