package com.concessio.crm.activity.repository;

import com.concessio.crm.activity.model.Activity;
import com.concessio.crm.activity.model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByTenantId(Long tenantId);

    List<Activity> findByLeadId(Long leadId);

    List<Activity> findByTenantIdAndLeadId(Long tenantId, Long leadId);

    List<Activity> findByUserId(Long userId);

    List<Activity> findByType(ActivityType type);

    List<Activity> findByTenantIdAndType(Long tenantId, ActivityType type);

    List<Activity> findByLeadIdOrderByCreatedAtDesc(Long leadId);

    @Query("SELECT a FROM Activity a WHERE a.tenant.id = :tenantId AND a.createdAt BETWEEN :start AND :end")
    List<Activity> findByTenantIdAndDateRange(@Param("tenantId") Long tenantId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query("SELECT a.type, COUNT(a) FROM Activity a WHERE a.tenant.id = :tenantId AND a.createdAt BETWEEN :start AND :end GROUP BY a.type")
    List<Object[]> countByTypeForPeriod(@Param("tenantId") Long tenantId,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);
}
