package com.yjntc.gateway.dao;

import com.yjntc.gateway.bean.FactoryDefinition;
import com.yjntc.gateway.bean.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangKangSheng
 * @date 2022-09-25 21:07
 */
@Repository
public interface FactoryDefinitionDao extends JpaRepository<FactoryDefinition,Integer> {

}
