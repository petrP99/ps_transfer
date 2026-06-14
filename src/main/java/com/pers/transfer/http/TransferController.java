package com.pers.transfer.http;

import com.pers.transfer.dto.response.PageResponse;
import com.pers.transfer.dto.request.PhoneTransferPreviewRequest;
import com.pers.transfer.dto.request.PhoneTransferRequest;
import com.pers.transfer.dto.response.TransferHistoryResponse;
import com.pers.transfer.dto.request.TransferPreviewRequest;
import com.pers.transfer.dto.response.TransferPreviewResponse;
import com.pers.transfer.dto.request.TransferRequest;
import com.pers.transfer.dto.response.TransferResponse;
import com.pers.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/preview")
    ResponseEntity<TransferPreviewResponse> preview(@Valid @RequestBody TransferPreviewRequest request) {
        return ResponseEntity.ok(transferService.preview(request));
    }

    @PostMapping("/preview-phone")
    ResponseEntity<TransferPreviewResponse> previewPhone(
            @Valid @RequestBody PhoneTransferPreviewRequest request
    ) {
        return ResponseEntity.ok(transferService.previewPhone(request));
    }

    @PostMapping("/create")
    ResponseEntity<TransferResponse> create(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transferService.create(request));
    }

    @PostMapping("/create-phone")
    ResponseEntity<TransferResponse> createPhone(@Valid @RequestBody PhoneTransferRequest request) {
        return ResponseEntity.ok(transferService.createPhone(request));
    }

    @GetMapping("/{id}")
    ResponseEntity<TransferResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(transferService.findById(id));
    }

    @GetMapping("/my")
    PageResponse<TransferHistoryResponse> history(Pageable pageable) {
        return transferService.history(pageable);
    }

    @GetMapping("/history/{id}")
    ResponseEntity<TransferHistoryResponse> historyDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(transferService.historyDetails(id));
    }
}
