package dasturlash.uz.entity;

import dasturlash.uz.enums.ProfileStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.rmi.server.UID;
import java.time.LocalDateTime;

public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UID id;
    private String title;
    private String description;
    @Column(name = "category")
    private Category category;
    private String content;
    private Integer shared_count;
    private Integer like_count;
    private Integer view_count;
    private Integer image_id;
    @ManyToOne
    @JoinColumn(name = "region")
    private Region region;
    private Profile moderator_id;
    private ProfileStatus status;
    private Profile publisher_id;
    private LocalDateTime created_at;
    private LocalDateTime published_date;
    private Boolean visible;
}
