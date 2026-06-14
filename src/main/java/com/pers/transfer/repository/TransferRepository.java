package com.pers.transfer.repository;

import com.pers.transfer.domain.Transfer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transfer t where t.id = :id")
    Optional<Transfer> findByIdForUpdate(@Param("id") UUID id);

    Optional<Transfer> findByIdAndFromClientId(UUID id, UUID clientId);

    @Query("""
            select t from Transfer t
            where t.fromClientId = :clientId or t.toClientId = :clientId
            order by t.timeOfTransfer desc
            """)
    List<Transfer> findHistory(@Param("clientId") UUID clientId);

    @Query("""
            select t from Transfer t
            where t.id = :id
              and (t.fromClientId = :clientId or t.toClientId = :clientId)
            """)
    Optional<Transfer> findByIdAndParticipant(
            @Param("id") UUID id,
            @Param("clientId") UUID clientId
    );
}
