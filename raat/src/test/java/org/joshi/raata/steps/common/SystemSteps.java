package org.joshi.raata.steps.common;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SystemSteps {

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
        stopServices();
    }

    @Given("The library management system is running")
    public void systemIsRunning() {
        var services = getRunningServices();
        if (!services.isEmpty()) {
            stopServices();
        }
        startServices();
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

    /**
     * Method that connects to Docker daemon and retrieves running list of containers and networks corresponding to this
     * application.
     *
     * @return list of active objects such as containers and networks
     */
    private static List<String> getRunningServices() {
        var dockerClient = getDockerClient();

        var containers = dockerClient.listContainersCmd()
                .exec()
                .stream()
                .flatMap(c -> Arrays.stream(c.getNames()))
                .filter(c -> {
                    for (var i : CONTAINERS) {
                        if (c.contains(i)) {
                            return true;
                        }
                    }
                    return false;
                });

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
    }

}
