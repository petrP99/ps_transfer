package com.pers.transfer.repository;

import com.pers.transfer.domain.AccountTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountTransferRepository extends JpaRepository<AccountTransfer, UUID> {

    List<AccountTransfer> findAllByClientIdOrderByTimeOfTransferDesc(UUID clientId);

    Optional<AccountTransfer> findByIdAndClientId(UUID id, UUID clientId);
}
