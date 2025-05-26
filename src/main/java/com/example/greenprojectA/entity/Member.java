package com.example.greenprojectA.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(length = 20, unique = true, nullable = false)
    private String username;  // 로그인용 아이디

    @Column(nullable = false)
    private String password;  // 암호화된 비밀번호

    @Column(nullable = false)
    private String name;      // 사용자 이름 (실명)

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "member_level", nullable = false)
    private int memberLevel;  // 0=관리자, 1=가입대기, 2=일반회원, 99=탈퇴요청

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;  // 소속 기업

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "pw_changed_at")
    private LocalDateTime pwChangedAt;

    @Column(name = "quit_requested_at")
    private LocalDateTime quitRequestedAt;

    private int loginFailCount;

    private boolean accountNonLocked = true;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.pwChangedAt = LocalDateTime.now();
    }
}
