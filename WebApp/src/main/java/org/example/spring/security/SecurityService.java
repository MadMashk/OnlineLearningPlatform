package org.example.spring.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.AppRoles;
import org.example.model.AppUser;
import org.example.model.Student;
import org.example.model.Teacher;
import org.example.model.constants.RolesEnumeration;
import org.example.model.exceptions.AlreadyExistsException;
import org.example.model.exceptions.NotFoundException;
import org.example.repositories.IAppUserRepository;
import org.example.repositories.IRoleRepository;
import org.example.repositories.IStudentRepository;
import org.example.repositories.ITeacherRepository;
import org.example.spring.security.jwt.JwtUtils;
import org.example.spring.security.pojo.JwtResponse;
import org.example.spring.security.pojo.LoginRequest;
import org.example.spring.security.pojo.SingUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SecurityService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private IAppUserRepository appUserRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IStudentRepository studentRepository;
    @Autowired
    private ITeacherRepository teacherRepository;
    @Autowired
    private IAppUserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(SecurityService.class);
    @Transactional(readOnly = true)
    public boolean hasStudentAccess(String currentUserName, Integer studentID) {
        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new NotFoundException("Student not found with id " + currentUserName));
        return student.getUserName().equals(currentUserName);
    }
    @Transactional(readOnly = true)
    public boolean hasTeacherAccess(String currentUserName, Integer teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found with id " + currentUserName));
        return teacher.getUserName().equals(currentUserName);
    }
    @Transactional
    public JwtResponse authentication(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        logger.info("Principal " + authentication.getPrincipal() + "is authenticated");

        return new JwtResponse(jwt, userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);

    }
    @Transactional
    public void registration(SingUpRequest singUpRequest) {
        if (appUserRepository.existsByUserName(singUpRequest.getUserName())) {
            throw new AlreadyExistsException("Error: UserName already exists");
        }
        if (appUserRepository.existsByEmail(singUpRequest.getEmail())) {
            throw new AlreadyExistsException("Error: Email already exists");
        }

        Set<String> reqRoles = singUpRequest.getRoles();
        Set<AppRoles> roles = new HashSet<>();

        reqRoles.forEach(r -> {
            switch (r) {
                case "admin" -> {
                    AppRoles adminRole = roleRepository
                            .findByName(RolesEnumeration.ROLE_ADMIN)
                            .orElseThrow(() -> new NotFoundException("Role ADMIN isn't fond"));
                    roles.add(adminRole);
                }
                case "teacher" -> {
                    AppRoles teacherRole = roleRepository
                            .findByName(RolesEnumeration.ROLE_TEACHER)
                            .orElseThrow(() -> new NotFoundException("Role TEACHER isn't fond"));
                    roles.add(teacherRole);
                    Teacher teacher = new Teacher();
                    teacher.setUserName(singUpRequest.getUserName());
                    teacherRepository.save(teacher);
                    logger.info("teacher with user name " + singUpRequest.getUserName() + "is created");
                }
                default -> {
                    AppRoles studentRole = roleRepository
                            .findByName(RolesEnumeration.ROLE_STUDENT)
                            .orElseThrow(() -> new NotFoundException("Role STUDENT isn't fond"));
                    roles.add(studentRole);
                    Student student = new Student();
                    student.setUserName(singUpRequest.getUserName());
                    logger.info("student  with user name " + singUpRequest.getUserName() + "is created");
                    studentRepository.save(student);
                }
            }
        });

        AppUser user = new AppUser(singUpRequest.getUserName(),
                passwordEncoder.encode(singUpRequest.getPassword()),
                singUpRequest.getEmail());

        user.setRoles(roles);
        userRepository.save(user);
        logger.info("User " + user.getId()+ " is created");

    }
}