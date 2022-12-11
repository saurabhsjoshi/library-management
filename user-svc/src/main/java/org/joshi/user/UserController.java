package org.joshi.user;

import com.fpts.api.annotation.PerformanceTest;
import com.fpts.api.enums.HttpHeaderFields;
import com.fpts.api.enums.HttpMethodEnum;
import com.fpts.api.model.GetTestParameter;
import com.fpts.api.model.TestSpec;
import com.fpts.api.model.validation.core.TestValidationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Get by username.
     *
     * @param username the username
     * @return user object
     */
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LibraryUser> getUser(@PathVariable String username) {
        return repository.findById(username)
                .map(r -> new ResponseEntity<>(r, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates new library user.
     *
     * @param user user to be created
     * @return created user
     */
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryUser createUser(@RequestBody LibraryUser user) {
        return repository.save(user);
    }

    @PerformanceTest(path = "/api/users/", httpMethod = HttpMethodEnum.POST, description = "Create a library user")
    public TestSpec<LibraryUser> postUserSpec() throws IOException {
        var testUser = getTestUser();
        return new TestSpec<>(testUser, new TestValidationsBuilder()
                .addHeaderParameter(HttpHeaderFields.USERNAME, "admin")
                .addHeaderParameter(HttpHeaderFields.PASSWORD, "admin")
                .addHeaderParameter(HttpHeaderFields.STATUS, String.valueOf(HttpStatus.CREATED.value()))
                .buildBodyValidationFromEntity(testUser));
    }

    @PerformanceTest(path = "/api/users/", httpMethod = HttpMethodEnum.GET, description = "Get all users")
    public TestSpec<GetTestParameter> getUsersSpec() throws IOException {
        GetTestParameter testParameter = new GetTestParameter("username", "testUser");
        var testUser = getTestUser();
        return new TestSpec<>(testParameter, new TestValidationsBuilder().buildHeaderStatus200AndEntityBody(testUser));
    }

    private static LibraryUser getTestUser() {
        var testUser = new LibraryUser();
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        testUser.setDisplayName("Test Name");

        return testUser;
    }
}
