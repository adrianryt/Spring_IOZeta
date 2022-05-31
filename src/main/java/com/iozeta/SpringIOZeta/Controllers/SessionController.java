package com.iozeta.SpringIOZeta.Controllers;

import com.google.gson.JsonArray;
import com.iozeta.SpringIOZeta.Controllers.utilities.EntranceCodeGenerator;
import com.iozeta.SpringIOZeta.Database.Entities.*;
import com.iozeta.SpringIOZeta.Database.Entities.utilities.Content;
import com.iozeta.SpringIOZeta.Database.Repositories.*;
import com.iozeta.SpringIOZeta.Database.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.google.gson.JsonObject;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.iozeta.SpringIOZeta.Controllers.git.BranchesController.requestForCommitToGithub;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final EntranceCodeGenerator entranceCodeGenerator;
    private final LabSessionRepository sessionRepository;
    private final StudentRepository studentRepository;
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
    public ResponseEntity<?> createSession(@Valid @RequestBody Session session) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("session/create").toUriString());

        if(sessionRepository.findSessionByName(session.getName()) != null){
            return ResponseEntity.badRequest().body(session);
        }
        String entranceCode = this.entranceCodeGenerator.generateCode();

        Task task = taskRepository.findTasksById(session.getTask().getId());

        session.setAccessCode(entranceCode);
        session.setActive(true);
        session.setTask(task);
        try {
            String commitSha = requestForCommitToGithub(session.getLecturer(), task.getRepoName(), "main");
            task.setCommitSha(commitSha);
            taskRepository.save(task);
        }catch (RuntimeException ex){
            return ResponseEntity.internalServerError().body("Error communicating with github");
        }

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

    @GetMapping("/active-sessions")
    public ResponseEntity<List<Session>> getActiveSessions(@RequestParam("lecturer_id") Long lecturer_id) {
        Lecturer finalLecturer = lecturerRepository.getLecturerById(lecturer_id);

        List<Session> activeSessions = sessionRepository.findAll().stream().filter(session -> session.isActive()
                && session.getTask().getSubject().getLecturer().getId() == finalLecturer.getId()).toList();

        return ResponseEntity.ok().body(activeSessions);
    }

    @GetMapping("/connected-students")
    public ResponseEntity<List<Student>> getConnectedStudents(@RequestParam("session_id") Long session_id) {
        Session session = sessionRepository.findSessionById(session_id);

        List<Student> connectedStudents = progressRepository.findAllBySession(session).stream()
                .map(Progress::getStudent).distinct().toList();

        return ResponseEntity.ok().body(new ArrayList<>(connectedStudents));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Session>> getSessionsByLecturer(@RequestParam("lecturer_id") Long lecturer_id) {
        Lecturer finalLecturer = lecturerRepository.getLecturerById(lecturer_id);

        List<Session> allLecturersSessions = sessionRepository.findAll().stream().filter(session ->
                session.getTask().getSubject().getLecturer().getId() == finalLecturer.getId()).toList();

        return ResponseEntity.ok().body(allLecturersSessions);
    }

    @GetMapping("/session-details")
    public ResponseEntity<?> getSessionDetails(@RequestParam("session_id") Long session_id) {
        JsonObject response = new JsonObject();

        Session session = sessionRepository.getById(session_id);

        List<Checkpoint> checkpoints = checkpointRepository.findAllByTask(session.getTask());

        JsonArray checkpointNames = new JsonArray();
        for(Checkpoint checkpoint: checkpoints) {
            checkpointNames.add(checkpoint.getContent().getTitle());
        }

        response.add("checkpointNames", checkpointNames);

        JsonArray jsonStudents = new JsonArray();

        List<Student> students = progressRepository.findAllBySession(session).stream()
                .map(Progress::getStudent).distinct().toList();

        Lecturer lecturer = session.getTask().getSubject().getLecturer();
        String repoName = session.getRepoName();

        for(Student student: students) {
            JsonObject jStudent = new JsonObject();
            jStudent.addProperty("name", student.getGitNick());

            JsonArray commits = new JsonArray();
            List<Progress> studentProgresses = progressRepository.findProgressesBySessionAndStudent(session, student);

            for(Progress progress: studentProgresses) {
                JsonObject commit = new JsonObject();

                commit.addProperty("checkpointName", progress.getCheckpoint().getContent().getTitle());
                commit.addProperty("stat", progress.getStatus().toString());

                if (progress.getCommitHash() == null)
                    commit.addProperty("url", "");
                else {
                    String commitLink = "github.com/" + lecturer.getGitNick() + "/" + repoName + "/commit/" + progress.getCommitHash();
                    commit.addProperty("url", commitLink);
                }

                commits.add(commit);
            }

            jStudent.add("commits", commits);
            jsonStudents.add(jStudent);
        }

        response.add("students", jsonStudents);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/student/session-info")
    public ResponseEntity<?> getSessionInfoForStudent(@RequestParam("session_id") Long session_id,
                                                      @RequestParam("student_id") Long student_id) {
        JsonObject response = new JsonObject();
        try {
            Session session = this.sessionRepository.getById(session_id);
            Student student = this.studentRepository.findById(student_id).get();
            Task task = session.getTask();
            response.addProperty("readmeUrl", task.getReadmeLink());
            response.addProperty("topicName", task.getName());
            List<Checkpoint> checkpoints = this.checkpointRepository.findAllByTask(task);
            JsonArray checkpointsArray = new JsonArray();

            List<Progress> progresses = progressRepository.findProgressesBySessionAndStudent(session, student);

            for (Checkpoint checkpoint: checkpoints) {
                JsonObject checkpointContent = new JsonObject();
                Content content = checkpoint.getContent();
                checkpointContent.addProperty("title", content.getTitle());
                checkpointContent.addProperty("description", content.getDescription());
                checkpointContent.addProperty("number", checkpoint.getNumber());

                Progress progress = progresses.stream().filter(
                        progress1 -> progress1.getCheckpoint().getId() == checkpoint.getId()).findFirst().get();

                checkpointContent.addProperty("progressCheckBox", progress.getStatus() == Status.DONE);
                String[] commandsArray = this.createCommandsArray(content.getTitle(), student_id);
                JsonArray commands = new JsonArray();
                Stream.of(commandsArray)
                        .forEach(commands::add);
                checkpointContent.add("commands", commands);
                checkpointsArray.add(checkpointContent);
            }
            response.add("checkpoints", checkpointsArray);
        } catch (Exception e) {
            response = new JsonObject();
            response.addProperty("message", "Error while getting Session " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok().body(response);
    }

    private String[] createCommandsArray(String title, Long student_id) throws ClassNotFoundException {
        ArrayList<String> commands = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        commands.add("git add .");
        stringBuilder.append("git commmit -m \"");
        stringBuilder.append(title);
        stringBuilder.append("\"");
        commands.add(stringBuilder.toString());
        Optional<Student> student = this.studentRepository.findById(student_id);
        if (!student.isPresent()) {
            throw new ClassNotFoundException("Student no found");
        }
        stringBuilder.setLength(0);
        stringBuilder.append("git push origin/");
        stringBuilder.append(student.get().getBranchName());
        commands.add(stringBuilder.toString());
        return commands.toArray(new String[0]);
    }

    @GetMapping
    public ResponseEntity<List<Session>> getSessionsByTaskId(@RequestParam("task_id") Long taskId){

        return new ResponseEntity<>(sessionRepository.findSessionsByTaskId(taskId).stream().sorted((task1,task2)->{
            if(task1.isActive()){
                return -1;
            }else{
                return 1;
            }
        }).collect(Collectors.toList()), HttpStatus.OK);

    }

}
