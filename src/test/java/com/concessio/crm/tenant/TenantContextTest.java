package com.concessio.crm.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TenantContextTest {

    @BeforeEach
    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Test
    void testSetAndGetCurrentTenant() {
        TenantContext.setCurrentTenant(42L);

        Long tenantId = TenantContext.getCurrentTenant();

        assertEquals(42L, tenantId);
    }

    @Test
    void testClear() {
        TenantContext.setCurrentTenant(42L);
        TenantContext.clear();

        Long tenantId = TenantContext.getCurrentTenant();

        assertNull(tenantId);
    }

    @Test
    void testDefaultValueIsNull() {
        Long tenantId = TenantContext.getCurrentTenant();

        assertNull(tenantId);
    }

    @Test
    void testThreadLocalIsolation() throws InterruptedException {
        TenantContext.setCurrentTenant(1L);

        Thread otherThread = new Thread(() -> {
            // Different thread should have null tenant
            assertNull(TenantContext.getCurrentTenant());
            
            // Set different tenant
            TenantContext.setCurrentTenant(99L);
            assertEquals(99L, TenantContext.getCurrentTenant());
        });

        otherThread.start();
        otherThread.join();

        // Main thread should still have original tenant
        assertEquals(1L, TenantContext.getCurrentTenant());
    }

    @Test
    void testMultipleSets() {
        TenantContext.setCurrentTenant(1L);
        assertEquals(1L, TenantContext.getCurrentTenant());

        TenantContext.setCurrentTenant(2L);
        assertEquals(2L, TenantContext.getCurrentTenant());

        TenantContext.setCurrentTenant(3L);
        assertEquals(3L, TenantContext.getCurrentTenant());
    }
}
