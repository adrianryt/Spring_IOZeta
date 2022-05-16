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

        System.out.println(session.getTask().getId());

        Task task = taskRepository.getById(session.getTask().getId());

        session.setAccessCode(entranceCode);
        session.setActive(true);
        session.setTask(task);

        session = sessionRepository.save(session);
        System.out.println(session.getAccessCode());
        System.out.println(session.isActive());
        System.out.println(session.getTask().getName());

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

    //this needs json with lecturer_id
    /*
    {
        "id": ...
    }
    */
    @GetMapping("/active-sessions")
    public ResponseEntity<List<Session>> getActiveSessions(@Valid @RequestBody Lecturer lecturer) {
        lecturer = lecturerRepository.getLecturerById(lecturer.getId());

        System.out.println(lecturer.getId());

        List<Session> activeSessions = new ArrayList<>();

        List<Session> sessions = sessionRepository.findAll();
        for(Session session: sessions) {
            if(session.isActive() && session.getTask().getSubject().getLecturer().getId() == lecturer.getId())
                activeSessions.add(session);
        }

        return ResponseEntity.ok().body(activeSessions);
    }

    //this needs json with session_id
    /*
    {
      "id": ...
    }
     */
    @GetMapping("/connected-students")
    public ResponseEntity<List<Student>> getConnectedStudents(@Valid @RequestBody Session session) {
        session = sessionRepository.getById(session.getId());

        Set<Student> connectedStudents = new HashSet<>();

        List<Progress> progresses = progressRepository.findProgressesBySession(session);
        for(Progress progress: progresses) {
            connectedStudents.add(progress.getStudent());
        }

        return ResponseEntity.ok().body(new ArrayList<>(connectedStudents));
    }

    //this needs json with lecturer_id
    /*
    {
        "id": ...
    }
    */
    @GetMapping("/all")
    public ResponseEntity<List<Session>> getSessionsByLecturer(@Valid @RequestBody Lecturer lecturer) {
        lecturer = lecturerRepository.getLecturerById(lecturer.getId());

        List<Session> allLecturersSessions = new ArrayList<>();

        List<Session> allSessions = sessionRepository.findAll();
        for(Session session: allSessions) {
            if(session.getTask().getSubject().getLecturer().getId() == lecturer.getId())
                allLecturersSessions.add(session);
        }

        return ResponseEntity.ok().body(allLecturersSessions);
    }

    //this needs json with session_id
    /*
    {
      "id": ...
    }
     */
    @GetMapping("/session-details")
    public ResponseEntity<?> getSessionDetails(@Valid @RequestBody Session session) {
        JsonObject response = new JsonObject();

        session = sessionRepository.getById(session.getId());

        List<Checkpoint> checkpoints = checkpointRepository.findAllByTask(session.getTask());

        System.out.println(checkpoints.size());

        JsonArray checkpointNames = new JsonArray();
        for(Checkpoint checkpoint: checkpoints) {
            checkpointNames.add(checkpoint.getContent());
        }

        response.add("checkpointNames", checkpointNames);

        JsonArray jsonStudents = new JsonArray();
        List<Progress> progresses = progressRepository.findProgressesBySession(session);
        Set<Student> students = new LinkedHashSet<>();

        for(Progress progress: progresses) {
            students.add(progress.getStudent());
        }

        for(Student student: students) {
            JsonObject jStudent = new JsonObject();
            jStudent.addProperty("name", student.getGitNick());

            JsonArray commits = new JsonArray();
            List<Progress> studentProgresses = progressRepository.findProgressesBySessionAndStudent(session, student);

            for(Progress progress: studentProgresses) {
                JsonObject commit = new JsonObject();

                commit.addProperty("checkpointName", progress.getCheckpoint().getContent());
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
