package com.pers.transfer.dto.response;

import java.util.UUID;

public record TransferPreparationResponse(UUID fromClientId,
                                          UUID toClientId,
                                          String sender,
                                          TransferPreviewResponse preview
) {
}
