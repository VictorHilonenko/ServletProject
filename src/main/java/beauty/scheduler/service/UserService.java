package beauty.scheduler.service;

import beauty.scheduler.dao.UserDao;
import beauty.scheduler.dao.core.InstanceMapper;
import beauty.scheduler.dto.UserDTO;
import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.util.ReflectUtils;
import beauty.scheduler.util.StringUtils;
import beauty.scheduler.web.form.RegistrationForm;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.annotations.InjectDependency;
import beauty.scheduler.web.myspring.annotations.ServiceComponent;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

import static beauty.scheduler.util.AppConstants.REST_ERROR;
import static beauty.scheduler.util.AppConstants.REST_SUCCESS;

//NOTE: ready for review
@ServiceComponent
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @InjectDependency
    private UserDao userDao;
    @InjectDependency
    private EmailMessageService emailMessageService;

    public UserService() {
    }

    public boolean exists(String fieldName, String fieldValue) throws SQLException, ExtendedException {
        return userDao.exists(fieldName, fieldValue);
    }

    public Optional<User> getVerifiedUser(String email, String password) throws SQLException, ExtendedException {
        Optional<User> user = userDao.findByEmail(email);

        if (user.isPresent()) {
            if (!BCrypt.checkpw(password, user.get().getPassword())) {
                user = Optional.empty();
            }
        }

        return user;
    }

    public void createUser(RegistrationForm form) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, SQLException, InstantiationException, ExtendedException {
        InstanceMapper<User> userMapper = ReflectUtils.getMapper(User.class);
        User user = userMapper.map(form);

        user.setRole(Role.DEFAULT_ROLE);
        user.setServiceType(ServiceType.NULL);

        //Hash password for the first time
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        userDao.create(user);
    }

    public List<User> getAll() throws SQLException, ExtendedException {
        return userDao.getAll();
    }

    public List<UserDTO> getAllUserDTO() throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ExtendedException {
        InstanceMapper<UserDTO> userDTOMapper = ReflectUtils.getMapper(UserDTO.class);

        List<UserDTO> list = new ArrayList<>();
        for (User user : getAll()) {
            list.add(userDTOMapper.map(user));
        }

        return list;
    }

    //it's possible to make this method general/universal, but now it's a first approach:
    public String updateUserByJSON(String jsonData, UserPrincipal userPrincipal) {
        Map<String, String> map = new Gson().fromJson(jsonData, HashMap.class);
        String email = map.getOrDefault("email", "");
        String strRole = map.getOrDefault("role", "");
        String strServiceType = map.getOrDefault("serviceType", "");

        Optional<User> optionalUser;
        try {
            optionalUser = findByEmail(email);
        } catch (SQLException | ExtendedException e) {
            LOGGER.error("SQLException findByEmail");
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.someRepositoryIssueTryAgainLater", userPrincipal.getCurrentLang());
        }

        Optional<Role> optionalRole = Role.lookupOptional(strRole);
        Optional<ServiceType> optionalServiceType = ServiceType.lookupOptional(strServiceType);

        if (!optionalUser.isPresent() || !optionalRole.isPresent() || !optionalServiceType.isPresent()) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang());
        }

        if (!optionalRole.get().hasTag("front")) {
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.wrongDataPassed", userPrincipal.getCurrentLang());
        }

        User user = optionalUser.get();
        user.setRole(optionalRole.get());
        user.setServiceType(optionalServiceType.get());

        try {
            userDao.update(user);
        } catch (SQLException | ExtendedException e) {
            LOGGER.error("SQLException updateUserByJSON");
            return REST_ERROR + ":" + LocaleUtils.getLocalizedMessage("error.someRepositoryIssueTryAgainLater", userPrincipal.getCurrentLang());
        }

        return REST_SUCCESS;
    }

    public Optional<User> findByEmail(String email) throws SQLException, ExtendedException {
        return userDao.findByEmail(email);
    }

    public String getLocalizedName(User user, String lang) {
        String nameEn = user.getFirstNameEn();
        String nameUk = user.getFirstNameUk();
        if (lang.equals(LocaleUtils.getDefaultLocale().getLanguage()) || StringUtils.isEmpty(nameUk)) {
            return nameEn;
        } else {
            return nameUk;
        }
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public EmailMessageService getEmailMessageService() {
        return emailMessageService;
    }

    public void setEmailMessageService(EmailMessageService emailMessageService) {
        this.emailMessageService = emailMessageService;
    }
}