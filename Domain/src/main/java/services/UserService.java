package services;

import model.AppUser;
import model.exceptions.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.IAppUserRepository;

import java.security.SecureRandom;

@Service
public class UserService {
    @Autowired
    private IAppUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger logger = LogManager.getLogger(UserService.class);

    public UserService() {
    }

    @Transactional
    public AppUser addUser(AppUser user) { //добавить пользавателя
        AppUser savedUser = userRepository.save(user);
        logger.info("user " + savedUser.getId() + " is created");
        return savedUser;
    }

    @Transactional
    public void deleteUser(int id) { //удалить пользователя
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found with id = " + id)));
        logger.info("user " + id + " is deleted");
    }

    @Transactional(readOnly = true)
    public AppUser getOneUser(int userId) { //получить определенного пользователя
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found with id = " + userId));
    }

    @Transactional(readOnly = true)
    public AppUser findByUserName (String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(()-> new NotFoundException("user not found with user name " + userName));
    }

    @Transactional
    public AppUser updateUser(int userId, AppUser appUser) { //обновить пользователя
        AppUser userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found with id = " + userId));
        if (appUser.getId()==null){
            appUser.setId(userToUpdate.getId());
        }
        if (appUser.getUserName() == null) {
            appUser.setUserName(userToUpdate.getUserName());
        }
        if (appUser.getPassWord() == null) {
            appUser.setPassWord(userToUpdate.getPassWord());
        }
        if (appUser.getEmail() == null) {
            appUser.setEmail(userToUpdate.getEmail());
        }
        if (appUser.getRoles() ==null){
            appUser.setRoles(userToUpdate.getRoles());
        }
        logger.info("user " + userId + " is updated");
        return userRepository.save(appUser);
    }

    public AppUser getOneUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("user not found with email " + email));
    }
    public void changePasswordOfUser(Integer id, String password){
        AppUser user = userRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("user not found with id " + id));
        user.setPassWord(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public String generateRandomPassword(){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        int randomLen = (int) (Math.random() * (50 -1) + 1);
        StringBuilder sb = new StringBuilder(randomLen);
        for( int i = 0; i < randomLen; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
