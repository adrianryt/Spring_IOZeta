package com.iozeta.SpringIOZeta.Database.Repositories;

import com.iozeta.SpringIOZeta.Database.Entities.Subject;
import com.iozeta.SpringIOZeta.Database.Entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllBy();
    Task findTasksById(Long id);
    List<Task> findTasksBySubject(Subject subject);
    Task findByRepoName(String repoName);
}
