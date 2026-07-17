package com.medo.api.auth.dao;

import com.medo.api.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByModuleAndAction(
        Permission.ModuleApp module, Permission.ActionType action);
}
