package com.iozeta.SpringIOZeta.Database.Services;

import com.iozeta.SpringIOZeta.Database.Entities.Lecturer;
import com.iozeta.SpringIOZeta.Database.Repositories.LecturerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LecturerServiceImpl implements LecturerService, UserDetailsService {
    private final LecturerRepository lecturerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String gitNick) throws UsernameNotFoundException {
        Lecturer lecturer = lecturerRepository.findLecturerByGitNick(gitNick);
        if (lecturer == null) {
            throw new UsernameNotFoundException(gitNick);
        } else {
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            lecturer.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role));
            });
            return new User(lecturer.getGitNick(), lecturer.getPassword(), authorities);
        }
    }

    @Override
    public Lecturer saveLecturer(Lecturer lecturer) {
        lecturer.setPassword(passwordEncoder.encode(lecturer.getPassword()));
        return lecturerRepository.save(lecturer);
    }

    @Override
    public Lecturer getUser(String gitNick) {
        return lecturerRepository.findLecturerByGitNick(gitNick);
    }

    @Override
    public List<Lecturer> getLecturers() {
        return lecturerRepository.findAll();
    }

}
