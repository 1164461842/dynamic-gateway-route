package com.yjntc.gateway.bean;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author WangKangSheng
 * @date 2022-09-25 21:00
 */
@Entity
@Data
@Table(name = "gd_config_definition")
public class ConfigDefinition {

    @Id
    private String configDefinitionId;

    private String factoryDefinitionId;

    private String configClassName;

    private String configType;

    private String configArgName;

}
