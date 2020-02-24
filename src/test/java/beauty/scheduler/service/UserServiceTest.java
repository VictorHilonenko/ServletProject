package beauty.scheduler.service;

import beauty.scheduler.dao.UserDao;
import beauty.scheduler.dto.UserDTO;
import beauty.scheduler.entity.User;
import beauty.scheduler.entity.enums.Role;
import beauty.scheduler.entity.enums.ServiceType;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.LocaleUtils;
import beauty.scheduler.web.myspring.UserPrincipal;
import beauty.scheduler.web.myspring.form.RegistrationForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static beauty.scheduler.util.AppConstants.MAIL_USERNAME;
import static beauty.scheduler.util.AppConstants.REST_SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService instance;

    @Mock
    private UserDao userDao;

    @Test
    public void shouldCallExists() throws SQLException, ExtendedException {
        when(userDao.exists(anyString(), anyString())).thenReturn(true);

        instance.exists("", "");

        verify(userDao, times(1)).exists(anyString(), anyString());
    }

    @Test
    public void shouldGetVerifiedUser() throws SQLException, ExtendedException {
        String pass = "pass";
        String hashedPass = BCrypt.hashpw(pass, BCrypt.gensalt());

        User user = new User();
        user.setEmail(MAIL_USERNAME);
        user.setPassword(hashedPass);

        when(userDao.findByEmail(MAIL_USERNAME)).thenReturn(Optional.of(user));

        Optional<User> optionalUser = instance.getVerifiedUser(MAIL_USERNAME, pass);

        assertEquals(user, optionalUser.get());
    }

    @Test
    public void shouldCreateUser() throws Exception {
        RegistrationForm form = new RegistrationForm();

        instance.createUser(form);

        verify(userDao, times(1)).create(any(User.class));
    }

    @Test
    public void shouldGetAll() throws SQLException, ExtendedException {
        when(userDao.getAll()).thenReturn(new ArrayList<User>());

        instance.getAll();

        verify(userDao, times(1)).getAll();
    }

    @Test
    public void shouldGetAllUserDTO() throws Exception {
        List<User> list = new ArrayList<>();
        list.add(new User());

        when(userDao.getAll()).thenReturn(list);

        List<UserDTO> result = instance.getAllUserDTO();

        assertEquals(result.size(), 1);
    }

    @Test
    public void shouldUpdateUserByJSON() throws SQLException, ExtendedException {
        String jsonData = "{\"email\":\"" + MAIL_USERNAME + "\",\"role\":\"ROLE_MASTER\",\"serviceType\":\"HAIRDRESSING\"}";

        User user = new User();
        user.setEmail(MAIL_USERNAME);
        user.setRole(Role.ROLE_MASTER);
        user.setServiceType(ServiceType.HAIRDRESSING);

        when(userDao.findByEmail(MAIL_USERNAME)).thenReturn(Optional.of(user));
        when(userDao.update(user)).thenReturn(true);

        String message = instance.updateUserByJSON(jsonData, new UserPrincipal());

        assertEquals(message, REST_SUCCESS);
    }

    @Test
    public void shouldFindByEmail() throws SQLException, ExtendedException {
        User user = new User();
        Optional<User> optionalUser = Optional.of(user);

        when(userDao.findByEmail(anyString())).thenReturn(optionalUser);

        Optional<User> result = instance.findByEmail(MAIL_USERNAME);

        assertEquals(result, optionalUser);
    }

    @Test
    public void shouldReturnCorrectLocalizedNames() {
        String nameEn = "Name";
        String nameUk = "Ім'я";

        User userEn = new User();
        userEn.setFirstNameEn(nameEn);

        User userEnUk = new User();
        userEnUk.setFirstNameEn(nameEn);
        userEnUk.setFirstNameUk(nameUk);

        assertEquals(nameEn, UserService.getLocalizedName(userEn, LocaleUtils.LOCALE_UKRAINIAN.getLanguage()));
        assertEquals(nameEn, UserService.getLocalizedName(userEnUk, LocaleUtils.LOCALE_ENGLISH.getLanguage()));
        assertEquals(nameUk, UserService.getLocalizedName(userEnUk, LocaleUtils.LOCALE_UKRAINIAN.getLanguage()));
    }

    @Test
    public void shouldReturnCorrectUsersLang() {
        String nameEn = "Name";
        String nameUk = "Ім'я";

        User userEn = new User();
        userEn.setFirstNameEn(nameEn);

        User userEnUk = new User();
        userEnUk.setFirstNameEn(nameEn);
        userEnUk.setFirstNameUk(nameUk);

        assertEquals(UserService.getUsersLang(userEn), LocaleUtils.LOCALE_ENGLISH.getLanguage());
        assertEquals(UserService.getUsersLang(userEnUk), LocaleUtils.LOCALE_UKRAINIAN.getLanguage());
    }
}