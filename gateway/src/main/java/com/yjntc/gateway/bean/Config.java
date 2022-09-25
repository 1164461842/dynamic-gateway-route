package com.yjntc.gateway.bean;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author WangKangSheng
 * @date 2022-09-25 21:44
 */
@Entity
@Data
@Table(name = "gd_config")
public class Config {

    @Id
    private String configId;

    private String configDefinitionId;

    private String configValue;

    private String factoryId;
}
