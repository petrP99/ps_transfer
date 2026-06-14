package com.pers.transfer.dto.response;

import java.util.Objects;
import java.util.UUID;

public record ClientResponse(UUID id,
                             String firstName,
                             String lastName) {

    public String fullName() {
        String name = (Objects.toString(firstName, "") + " " + Objects.toString(lastName, "")).trim();
        return name.isBlank() ? "Клиент:" : name;
    }
}
