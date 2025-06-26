package ru.otus.hw.security.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AuditableAcl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AclServiceWrapperServiceImpl implements AclServiceWrapperService {

    private static final String USER = "ROLE_USER";

    private final MutableAclService mutableAclService;

    @Override
    public void createPermission(Class<?> objectType, Serializable objectId) {
        final ObjectIdentity oid = new ObjectIdentityImpl(objectType, objectId);
        try {
            mutableAclService.readAclById(oid);
            return;
        } catch (NotFoundException nfe) {
            log.info("Creating new ACL for: {} with id: {}", oid.getType(), oid.getIdentifier());
        }
        final List<ImmutablePair<Permission, Sid>> permissions = getPermissions();
        final MutableAcl acl = mutableAclService.createAcl(oid);
        if (acl instanceof AuditableAcl auditableAcl) {
            permissions.forEach(permissionPair -> {
                int aceIndex = auditableAcl.getEntries().size();
                auditableAcl.insertAce(aceIndex, permissionPair.getLeft(), permissionPair.getRight(), true);
                auditableAcl.updateAuditing(aceIndex, true, true);
            });
        } else {
            permissions.forEach(permissionPair -> {
                int aceIndex = acl.getEntries().size();
                acl.insertAce(aceIndex, permissionPair.getLeft(), permissionPair.getRight(), true);
            });
        }
        mutableAclService.updateAcl(acl);
    }

    @Override
    public void deletePermission(Class<?> objectType, Serializable objectId) {
        final ObjectIdentity oid = new ObjectIdentityImpl(objectType, objectId);
        try {
            // The 'false' means do not delete children ACLs.
            mutableAclService.deleteAcl(oid, false);
            log.info("Deleted ACL for: {} with id: {}", oid.getType(), oid.getIdentifier());
        } catch (NotFoundException e) {
            log.warn("ACL for {} with id: {} not found, nothing to delete.", oid.getType(), oid.getIdentifier());
        }
    }

    //Left - Permission x Right - Sid
    private static List<ImmutablePair<Permission, Sid>> getPermissions() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Sid owner = new PrincipalSid(authentication);
        final Sid group = new GrantedAuthoritySid(USER);
        return List.of(
                ImmutablePair.of(BasePermission.READ, owner),
                ImmutablePair.of(BasePermission.WRITE, owner),
                ImmutablePair.of(BasePermission.READ, group)
        );
    }
}