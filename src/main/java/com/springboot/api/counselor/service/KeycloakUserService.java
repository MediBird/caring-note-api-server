package com.springboot.api.counselor.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final RealmResource realmResource;

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
     * 사용자명으로 Keycloak 사용자를 찾습니다.
     *
     * @param username 찾을 사용자명
     * @return 사용자 표현 객체 목록
     */
    public List<UserRepresentation> getUsersByUsername(String username) {
        return realmResource.users().searchByUsername(username, true);
    }
}