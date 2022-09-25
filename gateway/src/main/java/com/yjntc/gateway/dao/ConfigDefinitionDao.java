package com.yjntc.gateway.dao;

import com.yjntc.gateway.bean.ConfigDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangKangSheng
 * @date 2022-09-25 20:57
 */
@Repository
public interface ConfigDefinitionDao extends JpaRepository<ConfigDefinition,Integer> {

}
