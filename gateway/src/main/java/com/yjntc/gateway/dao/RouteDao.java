package com.yjntc.gateway.dao;

import com.yjntc.gateway.bean.Factory;
import com.yjntc.gateway.bean.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangKangSheng
 * @date 2022-09-25 21:07
 */
@Repository
public interface RouteDao extends JpaRepository<Route,Integer> {

}
