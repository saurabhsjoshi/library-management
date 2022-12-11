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

    @PutMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public LibraryUser updateUser(@RequestBody LibraryUser user) {
        return repository.save(user);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String username) {
        repository.deleteById(username);
    }

    @PerformanceTest(path = "/api/users/", httpMethod = HttpMethodEnum.POST, description = "Create a library user")
    public TestSpec<LibraryUser> postUserSpec() throws IOException {
        var testUser = getTestUser();
        return new TestSpec<>(testUser, new TestValidationsBuilder()
                .addHeaderParameter(HttpHeaderFields.STATUS, String.valueOf(HttpStatus.CREATED.value()))
                .buildBodyValidationFromEntity(testUser));
    }

    @PerformanceTest(path = "/api/users/", httpMethod = HttpMethodEnum.GET, description = "Get user by username")
    public TestSpec<GetTestParameter> getUsersSpec() throws IOException {
        GetTestParameter testParameter = new GetTestParameter("username", "testUser");
        var testUser = getTestUser();
        return new TestSpec<>(testParameter, new TestValidationsBuilder()
                .addHeaderParameter(HttpHeaderFields.STATUS, String.valueOf(HttpStatus.OK.value()))
                .buildBodyValidationFromEntity(testUser));
    }

    @PerformanceTest(path = "/api/users/", httpMethod = HttpMethodEnum.PUT, description = "Create a library user")
    public TestSpec<LibraryUser> putUserSpec() throws IOException {
        var testUser = getTestUser();
        return new TestSpec<>(testUser, new TestValidationsBuilder()
                .addHeaderParameter(HttpHeaderFields.STATUS, String.valueOf(HttpStatus.OK.value()))
                .buildBodyValidationFromEntity(testUser));
    }

    @PerformanceTest(path = "/api/users/", httpMethod = HttpMethodEnum.DELETE, description = "Delete user by username")
    public TestSpec<GetTestParameter> deleteUserSpec() throws IOException {
        GetTestParameter testParameter = new GetTestParameter("username", "testUser");
        return new TestSpec<>(testParameter, new TestValidationsBuilder()
                .addHeaderParameter(HttpHeaderFields.STATUS, String.valueOf(HttpStatus.OK.value())));
    }

    private static LibraryUser getTestUser() {
        var testUser = new LibraryUser();
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        testUser.setDisplayName("TestName");

        return testUser;
    }
}
