package com.iozeta.SpringIOZeta.Controllers;

import com.google.gson.JsonArray;
import com.iozeta.SpringIOZeta.Controllers.utilities.EntranceCodeGenerator;
import com.iozeta.SpringIOZeta.Database.Entities.*;
import com.iozeta.SpringIOZeta.Database.Repositories.*;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.google.gson.JsonObject;

import javax.validation.Valid;
import javax.ws.rs.Path;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final EntranceCodeGenerator entranceCodeGenerator;
    private final LabSessionRepository sessionRepository;
    private final TaskRepository taskRepository;
    private final LecturerRepository lecturerRepository;
    private final ProgressRepository progressRepository;
    private final CheckpointRepository checkpointRepository;

    //this needs json with session name and task_id
    /*
    {
      "name" : ...,
      "task": {
                "id": ...
              }
    }
     */
    @PostMapping("/create")
    public ResponseEntity<Session> createSession(@Valid @RequestBody Session session) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("session/create").toUriString());

        String entranceCode = this.entranceCodeGenerator.generateCode();

        Task task = taskRepository.findTasksById(session.getTask().getId());

        session.setAccessCode(entranceCode);
        session.setActive(true);
        session.setTask(task);

        session = sessionRepository.save(session);

        return ResponseEntity.created(uri).body(session);
    }

    //this needs json with session_id
    /*
    {
      "id": ...
    }
     */
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateSession(@Valid @RequestBody Session session) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("session/deactivate").toUriString());

        session = sessionRepository.getById(session.getId());

        session.setActive(false);

        sessionRepository.save(session);

        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/active-sessions&{lecturer_id}")
    public ResponseEntity<List<Session>> getActiveSessions(@PathVariable Long lecturer_id) {
        Lecturer finalLecturer = lecturerRepository.getLecturerById(lecturer_id);

        List<Session> activeSessions = sessionRepository.findAll().stream().filter(session -> session.isActive()
                && session.getTask().getSubject().getLecturer().getId() == finalLecturer.getId()).toList();

        return ResponseEntity.ok().body(activeSessions);
    }

    @GetMapping("/connected-students&{session_id}")
    public ResponseEntity<List<Student>> getConnectedStudents(@PathVariable Long session_id) {
        Session session = sessionRepository.getById(session_id);

        List<Student> connectedStudents = progressRepository.findProgressesBySession(session).stream()
                .map(Progress::getStudent).distinct().toList();

        return ResponseEntity.ok().body(new ArrayList<>(connectedStudents));
    }

    @GetMapping("/all&{lecturer_id}")
    public ResponseEntity<List<Session>> getSessionsByLecturer(@PathVariable Long lecturer_id) {
        Lecturer finalLecturer = lecturerRepository.getLecturerById(lecturer_id);

        List<Session> allLecturersSessions = sessionRepository.findAll().stream().filter(session ->
                session.getTask().getSubject().getLecturer().getId() == finalLecturer.getId()).toList();

        return ResponseEntity.ok().body(allLecturersSessions);
    }

    @GetMapping("/session-details&{session_id}")
    public ResponseEntity<?> getSessionDetails(@PathVariable Long session_id) {
        JsonObject response = new JsonObject();

        Session session = sessionRepository.getById(session_id);

        List<Checkpoint> checkpoints = checkpointRepository.findAllByTask(session.getTask());

        JsonArray checkpointNames = new JsonArray();
        for(Checkpoint checkpoint: checkpoints) {
            checkpointNames.add(checkpoint.getContent().getTitle());
        }

        response.add("checkpointNames", checkpointNames);

        JsonArray jsonStudents = new JsonArray();

        List<Student> students = progressRepository.findProgressesBySession(session).stream()
                .map(Progress::getStudent).distinct().toList();

        for(Student student: students) {
            JsonObject jStudent = new JsonObject();
            jStudent.addProperty("name", student.getGitNick());

            JsonArray commits = new JsonArray();
            List<Progress> studentProgresses = progressRepository.findProgressesBySessionAndStudent(session, student);

            for(Progress progress: studentProgresses) {
                JsonObject commit = new JsonObject();

                commit.addProperty("checkpointName", progress.getCheckpoint().getContent().getTitle());
                commit.addProperty("stat", progress.getStatus().toString());
                commit.addProperty("url", progress.getCommitHash());

                commits.add(commit);
            }

            jStudent.add("commits", commits);
            jsonStudents.add(jStudent);
        }

        response.add("students", jsonStudents);

        return ResponseEntity.ok().body(response);
    }

}
