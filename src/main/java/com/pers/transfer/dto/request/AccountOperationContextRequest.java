package com.pers.transfer.dto.request;

import java.util.UUID;

public record AccountOperationContextRequest(UUID accountFrom,
                                             UUID accountTo
) {
}
