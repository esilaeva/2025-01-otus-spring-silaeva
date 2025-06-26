package ru.otus.hw.security.services;

import java.io.Serializable;

public interface AclServiceWrapperService {

    void createPermission(Class<?> objectType, Serializable objectId);

    void deletePermission(Class<?> objectType, Serializable objectId);
}
