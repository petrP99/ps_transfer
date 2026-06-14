package com.pers.transfer.repository;

import com.pers.transfer.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value = """
            SELECT *
            FROM outbox_event
            WHERE status = 'PENDING'
              AND next_attempt_at <= CURRENT_TIMESTAMP
            ORDER BY created_at
            FOR UPDATE SKIP LOCKED
            LIMIT :limit
            """, nativeQuery = true)
    List<OutboxEvent> findReadyForPublishing(@Param("limit") int limit);

    @Modifying
    @Query("""
            delete from OutboxEvent e
            where e.status = com.pers.transfer.domain.OutboxStatus.PUBLISHED
              and e.publishedAt < :cutoff
            """)
    int deletePublishedBefore(@Param("cutoff") LocalDateTime cutoff);
}
