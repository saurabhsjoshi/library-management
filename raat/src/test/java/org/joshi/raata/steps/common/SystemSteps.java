package org.joshi.raata.steps.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemSteps {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // List of microservices
    private final static List<String> CONTAINERS = List.of(
            "nginx-proxy",
            "card-svc",
            "inventory-svc",
            "reservation-svc",
            "notification-svc"
    );

    private static final String NETWORK_NAME = "library-mgmt_default";

    @After
    public void teardown() {
        //   stopServices();
    }

    @Given("The library management system is running")
    public void systemIsRunning() {
//        var services = getRunningServices();
//        if (!services.isEmpty()) {
//            stopServices();
//        }
//        startServices();
    }

    private static DockerClient getDockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)

                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }

    public static Stream<Container> getRunningContainers(DockerClient dockerClient) {
        return dockerClient.listContainersCmd()
                .exec()
                .stream()
                .filter(c -> {
                    for (var i : CONTAINERS) {
                        if (c.getNames()[0].contains(i)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    /**
     * Method that connects to Docker daemon and retrieves running list of containers and networks corresponding to this
     * application.
     *
     * @return list of active objects such as containers and networks
     */
    private static List<String> getRunningServices() {
        var dockerClient = getDockerClient();

        var containers = getRunningContainers(dockerClient)
                .flatMap(c -> Arrays.stream(c.getNames()));

        var networks = dockerClient.listNetworksCmd()
                .exec()
                .stream()
                .map(Network::getName)
                .filter(n -> n.contains(NETWORK_NAME));

        try {
            dockerClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Stream.concat(containers, networks)
                .toList();
    }

    private static void stopServices() {
        ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "down");
        processBuilder.directory(new File(".."));
        try {
            processBuilder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startServices() {
        ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "up", "--detach");
        processBuilder.directory(new File(".."));
        try {
            processBuilder.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Pattern pattern = Pattern.compile("\\(healthy\\)");

        var dockerClient = getDockerClient();

        // Wait for 30 seconds
        long retryPeriod = Duration.ofSeconds(30).toMillis();
        long currentPeriod = 0;

        long healthy = 0;
        System.out.println("Waiting for all containers to be healthy.");
        while (healthy != CONTAINERS.size()) {

            if (currentPeriod > retryPeriod) {
                Assertions.fail("Containers were not healthy after '" + retryPeriod + "' milliseconds");
                return;
            }

            healthy = getRunningContainers(dockerClient)
                    .filter(c -> pattern.matcher(c.getStatus()).find())
                    .count();
            try {
                var sleepPeriod = 500;
                currentPeriod += sleepPeriod;
                Thread.sleep(sleepPeriod);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Then("I get an {string} error")
    public void iGetAnUnauthorizedErrorWithStatus(String error) {
        if (error.equals("Unauthorized")) {
            assertEquals(401, TestData.getInstance().statusCode);
        } else if (error.equals("Not Found")) {
            assertEquals(404, TestData.getInstance().statusCode);
        } else {
            throw new RuntimeException("Unknown error '" + error + "'.");
        }
    }

    public static <T> HttpPost getAuthPostReq(String uri, T data) throws JsonProcessingException {
        var post = new HttpPost(uri);
        post.setHeader("username", TestData.getInstance().username);
        post.setHeader("password", TestData.getInstance().password);
        post.setEntity(new StringEntity(objectMapper.writeValueAsString(data)));
        return post;
    }
}
