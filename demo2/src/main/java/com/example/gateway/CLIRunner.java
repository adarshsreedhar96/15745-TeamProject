package com.example.gateway;

import com.example.gateway.Gateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CLIRunner implements CommandLineRunner {
    @Autowired
    private Gateway gateway;

    @Override
    public void run(String... args) throws Exception {
        String[] arr = { "a", "b", "c", "d", "e" };
        while (true) {
            int rand = ThreadLocalRandom.current().nextInt(0, 5);
            System.out.println(gateway.getInfo(arr[rand]));
            Thread.sleep(2000);
        }
    }

}
