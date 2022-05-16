package com.iozeta.SpringIOZeta.Controllers;

import com.google.gson.JsonObject;
import com.iozeta.SpringIOZeta.Controllers.utilities.ContentHandler;
import com.iozeta.SpringIOZeta.Controllers.utilities.NewTaskForm;
import com.iozeta.SpringIOZeta.Database.Entities.Checkpoint;
import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Entities.Subject;
import com.iozeta.SpringIOZeta.Database.Entities.Task;
import com.iozeta.SpringIOZeta.Database.Entities.utilities.Content;
import com.iozeta.SpringIOZeta.Database.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/task")
@RequiredArgsConstructor
public class TasksController {
    private final SubjectRepository subjectRepository;
    private final TaskRepository taskRepository;
    private final CheckpointRepository checkpointRepository;
    private final ContentRepository contentRepository;
    private final LecturerRepository lecturerRepository;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addNewTask(@Valid @RequestBody NewTaskForm newTaskForm) { // check if RequestBody is good
        JsonObject response = new JsonObject();
        try {
            Task task = this.createTask(
                    newTaskForm.getTaskName(),
                    newTaskForm.getRepositoryName(),
                    newTaskForm.getRepositoryLink(),
                    newTaskForm.getSubject(),
                    newTaskForm.getLecturerGitNick()
            );
            this.createCheckpoints(newTaskForm.getCheckpointsContent(), task);
            response.addProperty("message", "New Task has been added successfully");
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.addProperty("message", "Error while adding new task. " + e.getMessage());
            return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void createCheckpoints(ContentHandler[] checkpoints, Task task) {
        for(int i = 0; i < checkpoints.length; i++) {
            Checkpoint checkpoint = new Checkpoint();
            Content content = new Content();
            content.setTitle(checkpoints[i].getTitle());
            content.setDescription(checkpoints[i].getDescription());
            checkpoint.setContent(contentRepository.save(content));
            checkpoint.setTask(task);
            checkpoint.setNumber(i+1);
            this.checkpointRepository.save(checkpoint);
        }
    }

    private Task createTask(String name, String repoName, String repoLink, String subjectName, String lecturerGitNick) throws ClassNotFoundException {
        Lecturer lecturer = this.lecturerRepository.findLecturerByGitNick(lecturerGitNick);
        Task task = new Task();
        task.setName(name);
        task.setRepoName(repoName);
        task.setRepoLink(repoLink);
        Subject subject = this.subjectRepository.getSubjectByNameAndLecturer(subjectName, lecturer);
        if (subject == null || lecturer == null) {
            System.out.println(subject);
            System.out.println(lecturer);
            throw new ClassNotFoundException("Wrong subject or lecturer");
        }
        task.setSubject(subject);
        return this.taskRepository.save(task);
    }
}
