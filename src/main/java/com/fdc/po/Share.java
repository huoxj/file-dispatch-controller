package com.fdc.po;

import com.fdc.vo.share.ShareVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Share")
public class Share {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private String id;

    @JoinColumn(name = "file_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private File file;

    @Basic
    @Column(name = "user_id")
    private String userId;

    @Basic
    @Column(name = "access_token")
    private String accessToken;

    public ShareVO toVO() {
        ShareVO shareVO = new ShareVO();
        shareVO.setId(this.id);
        shareVO.setFileId(this.file != null ? this.file.getId() : null);
        shareVO.setUserId(this.userId);
        shareVO.setAccessToken(this.accessToken);
        return shareVO;
    }
}
