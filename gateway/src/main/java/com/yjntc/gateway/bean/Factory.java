package com.yjntc.gateway.bean;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author WangKangSheng
 * @date 2022-09-25 21:41
 */
@Data
@Entity
@Table(name = "gd_factory")
public class Factory {
    @Id
    private  String configuredFactoryId;
    private String factoryDefinitionId;
    private String factoryType;

    @OneToOne(mappedBy = "factory_definition_id")
    private FactoryDefinition factoryDefinition;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "factory_id")
    private List<Config> configs;

}
