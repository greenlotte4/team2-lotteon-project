package com.lotteon.entity.config;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "config")
public class Config {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "version_id")
    private String configVersion;

    @Column(name = "config_title")
    private String configTitle;

    @Column(name = "config_sub")
    private String configSub;

    @Column(name = "config_header_logo")
    private String configHeaderLogo;

    @Column(name = "config_footer_logo")
    private String configFooterLogo;

    @Column(name = "config_fabicon")
    private String configFabicon;

    @Column(name = "config_update_version")
    private int configUpdateVersion;

    @Column(name = "config_update_location")
    private int configUpdateLocation;

    @Column(name = "config_updated_admin")
    private String configUpdatedAdmin;

    @Column(name = "config_update_time")
    @CreationTimestamp
    private Timestamp configCreatedAt;

    @Column(name = "config_is_used")
    private boolean configIsUsed;

    public void patchSiteVersion(String version){
        this.configVersion = version;
        this.configUpdateLocation = 1;
    }

    public void patchSiteInfo(String title, String sub) {
        if (title != null) {this.configTitle = title;}
        if (sub != null) {this.configSub = sub;}
        this.configUpdateLocation = 2;
    }

    public void patchSiteLogo(String headerLogo, String footerLogo, String fabicon) {
        if (headerLogo != null) {this.configHeaderLogo = headerLogo;}
        if (footerLogo != null) {this.configFooterLogo = footerLogo;}
        if (fabicon != null) {this.configFabicon = fabicon;}
        configUpdateLocation = 3;
    }

    public void update(String user) {
        this.configUpdatedAdmin = user;
        this.configUpdateVersion += 1;
    }
    public void toggleStatus(){
        this.configIsUsed = !this.configIsUsed;
    }
    public Config copyConfig() {
        this.configIsUsed = false;
        return Config.builder()
                .configVersion(this.configVersion)
                .configTitle(this.configTitle)
                .configSub(this.configSub)
                .configHeaderLogo(this.configHeaderLogo)
                .configFooterLogo(this.configFooterLogo)
                .configFabicon(this.configFabicon)
                .configUpdateVersion(this.configUpdateVersion)
                .configIsUsed(true)
                .build();
    }
}
