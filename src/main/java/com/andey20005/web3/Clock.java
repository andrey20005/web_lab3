package com.andey20005.web3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.time.LocalDateTime;

@Named
@ApplicationScoped
public class Clock {

    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
