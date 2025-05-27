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

    @Column(name = "mid", length = 20, unique = true, nullable = false)
    private String mid;  // 로그인 아이디

    @Column(nullable = false)
    private String username;  // 실명

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 13)
    private String tel;

    @Column(length = 255)
    private String address;

    @Column(name = "member_level", nullable = false)
    private int memberLevel;  // 0=ADMIN, 1=PENDING, 2=USER, 99=QUIT_PENDING

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "pw_changed_at")
    private LocalDateTime pwChangedAt;

    @Column(name = "quit_requested_at")
    private LocalDateTime quitRequestedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.pwChangedAt = LocalDateTime.now();
    }
}
