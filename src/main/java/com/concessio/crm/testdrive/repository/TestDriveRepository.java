package com.concessio.crm.testdrive.repository;

import com.concessio.crm.testdrive.model.TestDrive;
import com.concessio.crm.testdrive.model.TestDriveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestDriveRepository extends JpaRepository<TestDrive, Long> {

    List<TestDrive> findByTenantId(Long tenantId);

    List<TestDrive> findByLeadId(Long leadId);

    List<TestDrive> findByTenantIdAndStatus(Long tenantId, TestDriveStatus status);

    List<TestDrive> findByScheduledById(Long userId);

    List<TestDrive> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT td FROM TestDrive td WHERE td.tenant.id = :tenantId AND td.scheduledAt BETWEEN :start AND :end")
    List<TestDrive> findByTenantIdAndDateRange(@Param("tenantId") Long tenantId, 
                                                @Param("start") LocalDateTime start, 
                                                @Param("end") LocalDateTime end);

    @Query("SELECT td FROM TestDrive td WHERE td.reminderSent = false AND td.scheduledAt BETWEEN :now AND :reminderTime")
    List<TestDrive> findPendingReminders(@Param("now") LocalDateTime now, 
                                          @Param("reminderTime") LocalDateTime reminderTime);

    Optional<TestDrive> findByIdAndTenantId(Long id, Long tenantId);

    long countByTenantIdAndStatus(Long tenantId, TestDriveStatus status);
}
