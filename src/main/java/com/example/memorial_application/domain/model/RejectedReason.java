package com.example.memorial_application.domain.model;

import com.example.memorial_application.domain.dto.request.RejectedReasonRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectedReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rejectedReasonId;

    @OneToOne
    @JoinColumn(name="memorial_application")
    private MemorialApplication memorialApplication;

    private String reason;

    public static RejectedReason of(MemorialApplication memorialApplication, RejectedReasonRequest reason) {
        return RejectedReason.builder()
                .memorialApplication(memorialApplication)
                .reason(reason.rejectReason())
                .build();
    }
}
