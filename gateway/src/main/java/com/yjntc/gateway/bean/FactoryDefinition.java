package com.yjntc.gateway.bean;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author WangKangSheng
 * @date 2022-09-25 21:44
 */
@Entity
@Table(name = "gd_factory_definition")
@Data
public class FactoryDefinition {

    @Id
    private String factoryDefinitionId;

    private String factoryName;

    private String factoryClassName;

    private String factoryType;

    private Date createTime;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "factory_definition_id")
    private List<ConfigDefinition> configDefinitions;

    private Map<String,Long> map;
}
