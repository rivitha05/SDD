package com.sdd.assessment.mobile;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/** Immutable synthetic test data; never use a real user's credentials here. */
public record MobileUser(String username, String name, String email, String password, String language) {
    public static MobileUser load() {
        try (var input = MobileUser.class.getResourceAsStream("/data/mobile-user.json")) {
            if (input == null) throw new IllegalStateException("Missing mobile-user.json");
            return new ObjectMapper().readValue(input, MobileUser.class);
        } catch (IOException e) { throw new IllegalStateException("Cannot read mobile test data", e); }
    }
}
