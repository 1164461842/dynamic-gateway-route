package com.yjntc.gateway.bean;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author WangKangSheng
 * @date 2022-09-25 21:44
 */
@Table(name = "gd_route")
@Entity
@Data
public class Route {

    @Id
    private String routeId;

    private String routeKeyId;

    private Integer routeSeq;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "route_id")
    private List<Factory> factories;

}
