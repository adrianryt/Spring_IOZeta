package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {
    List<Progress> findAllBy();
}
