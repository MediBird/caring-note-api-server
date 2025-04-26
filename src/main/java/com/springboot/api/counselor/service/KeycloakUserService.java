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

    /**
     * 사용자의 비밀번호를 초기화합니다.
     *
     * @param userId      비밀번호를 초기화할 사용자 ID
     * @param newPassword 새 비밀번호
     * @param isTemporary 임시 비밀번호 여부 (true인 경우 사용자는 다음 로그인 시 비밀번호 변경 필요)
     */
    public void resetPassword(String userId, String newPassword, boolean isTemporary) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(isTemporary);

        realmResource.users().get(userId).resetPassword(credential);
    }

    /**
     * 사용자 ID로 Keycloak 사용자를 찾습니다.
     *
     * @param userId 찾을 사용자 ID
     * @return 사용자 표현 객체
     */
    public UserRepresentation getUserById(String userId) {
        return realmResource.users().get(userId).toRepresentation();
    }

    /**
     * 이메일로 Keycloak 사용자를 찾습니다.
     *
     * @param email 찾을 사용자 이메일
     * @return 사용자 표현 객체 목록
     */
    public List<UserRepresentation> getUsersByEmail(String email) {
        return realmResource.users().searchByEmail(email, true);
    }

    /**
     * 사용자명으로 Keycloak 사용자를 찾습니다.
     *
     * @param username 찾을 사용자명
     * @return 사용자 표현 객체 목록
     */
    public List<UserRepresentation> getUsersByUsername(String username) {
        return realmResource.users().searchByUsername(username, true);
    }
}