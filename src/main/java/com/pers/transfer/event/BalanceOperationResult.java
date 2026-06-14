package com.pers.transfer.event;

import java.util.UUID;

public record BalanceOperationResult(UUID operationId,
                                     boolean successful,
                                     String failureCode
) {
}
