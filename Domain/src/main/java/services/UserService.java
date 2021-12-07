package services;

import model.AppUser;
import model.Task;
import model.exceptions.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.IAppUserRepository;
@Service
public class UserService {
    @Autowired
    private IAppUserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(UserService.class);

    public UserService() {
    }

    public AppUser addUser(AppUser user) { //добавить пользавателя
        AppUser savedUser = userRepository.save(user);
        logger.info("user " + savedUser.getId() + " is created");
        return savedUser;
    }

    public void deleteUser(int id) { //удалить пользователя
        userRepository.delete(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user not found with id = " + id)));
        logger.info("user " + id + " is deleted");
    }


    public AppUser getOneUser(int userId) { //получить определенного пользователя
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found with id = " + userId));
    }


    public AppUser updateTask(int userId, AppUser appUser) { //обновить пользователя
        AppUser userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("task not found with id = " + userId));
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
}
