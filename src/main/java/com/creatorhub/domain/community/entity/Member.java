package com.creatorhub.domain.community.entity;

import com.creatorhub.domain.common.entity.BaseEntity;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    private static final int MAX_NICKNAME_LENGTH = 20;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$";

    private String email;       // 이메일
    private String password;    // 비밀번호 (암호화됨)
    private String nickname;    // 닉네임
    private Role role;         // 권한
    private boolean isEnabled; // 활성화 여부
    private List<Article> articles;  // 작성한 게시글 목록
    private List<Comment> comments;  // 작성한 댓글 목록

    public enum Role {
        USER("일반 사용자", Set.of("ROLE_USER")),
        CREATOR("크리에이터", Set.of("ROLE_USER", "ROLE_CREATOR")),
        ADMIN("관리자", Set.of("ROLE_USER", "ROLE_CREATOR", "ROLE_ADMIN"));

        private final String description;
        private final Set<String> authorities;

        Role(String description, Set<String> authorities) {
            this.description = description;
            this.authorities = authorities;
        }

        public String getDescription() {
            return description;
        }

        public Set<String> getAuthorities() {
            return authorities;
        }

        public boolean hasAuthority(String authority) {
            return authorities.contains(authority);
        }
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        this.role = Role.USER;
        this.isEnabled = true;
        this.articles = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public void updateEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public void updatePassword(String password) {
        validatePassword(password);
        this.password = password;
    }
    public void setPassword(String encodedPassword) {
        this.password = encodedPassword;
    }
    public void updateNickname(String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
    }

    public void upgradeToCreator() {
        if (this.role == Role.ADMIN) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "관리자는 크리에이터로 변경할 수 없습니다."
            );
        }
        this.role = Role.CREATOR;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean hasAuthority(String authority) {
        return this.role.hasAuthority(authority);
    }

    public void addArticle(Article article) {
        if (article == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "게시글이 null입니다.");
        }
        this.articles.add(article);
    }

    public void removeArticle(Article article) {
        this.articles.remove(article);
    }

    public void addComment(Comment comment) {
        if (comment == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "댓글이 null입니다.");
        }
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }

    public boolean canModifyArticle(Article article) {
        return this.role == Role.ADMIN || article.getAuthor().getId().equals(this.getId());
    }

    public boolean canDeleteArticle(Article article) {
        return canModifyArticle(article);
    }

    public boolean canModifyComment(Comment comment) {
        return this.role == Role.ADMIN || comment.getMember().getId().equals(this.getId());
    }

    public boolean canDeleteComment(Comment comment) {
        return canModifyComment(comment);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "이메일은 필수입니다.");
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "올바른 이메일 형식이 아닙니다.");
        }
    }

    //Member 엔티티의 password 필드에 대한 일반적인 유효성 검증을 담당
    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "비밀번호는 필수입니다.");
        }
        if (!Pattern.matches(PASSWORD_REGEX, password)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "비밀번호는 8-20자의 영문자, 숫자, 특수문자를 포함해야 합니다."
            );
        }
    }

    //밀번호 변경 시의 유효성 검증을 담당
    public void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "비밀번호는 필수입니다.");
        }
        if (!Pattern.matches(PASSWORD_REGEX, newPassword)) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "비밀번호는 8-20자의 영문자, 숫자, 특수문자를 포함해야 합니다."
            );
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "닉네임은 필수입니다.");
        }
        if (nickname.length() > MAX_NICKNAME_LENGTH) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    String.format("닉네임은 %d자를 초과할 수 없습니다.", MAX_NICKNAME_LENGTH)
            );
        }
    }
}

