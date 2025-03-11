package com.springboot.api.counselor.service;

import java.util.List;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final RealmResource realmResource;

    public List<UserRepresentation> getUsers() {
        return realmResource.users().list();
    }

    public void createUser(String username, String email, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        // 비밀번호 설정
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        realmResource.users().create(user);
    }

    public void deleteUser(String userId) {
        realmResource.users().delete(userId);
    }

    public void updateUser(String userId, String email) {
        UserRepresentation user = realmResource.users().get(userId).toRepresentation();
        user.setEmail(email);
        realmResource.users().get(userId).update(user);
    }
}