package com.smith.helmify.model.meta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smith.helmify.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "machines")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "location")
    private String location;

    @Column(name = "status", length = 100, nullable = false)
    private String status;

    @Column(name = "ipAddress", length = 100, nullable = false)
    private String ipAddress;

    @Embedded
    private BaseEntity baseEntity = new BaseEntity();

    @PrePersist
    protected void onCreate() {
        if (baseEntity == null) {
            baseEntity = new BaseEntity();
        }
        baseEntity.setCreatedAt(LocalDateTime.now());
        baseEntity.setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        if (baseEntity == null) {
            baseEntity = new BaseEntity();
        }
        baseEntity.setUpdatedAt(LocalDateTime.now());
    }
}
