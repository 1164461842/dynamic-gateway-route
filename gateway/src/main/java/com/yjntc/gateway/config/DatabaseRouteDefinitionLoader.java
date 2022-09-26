package com.yjntc.gateway.config;

import com.yjntc.gateway.bean.Factory;
import com.yjntc.gateway.bean.Route;
import com.yjntc.gateway.dao.ConfigDefinitionDao;
import com.yjntc.gateway.dao.RouteDao;
import com.yjntc.gateway.util.RouteDefinitionConvert;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author WangKangSheng
 * @date 2022-09-25 20:55
 */
@Component
public class DatabaseRouteDefinitionLoader implements RouteDefinitionLocator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigDefinitionDao configDefinitionDao;
    @Autowired
    private RouteDao routeDao;
    @Autowired
    private RouteDefinitionConvert routeDefinitionConvert;

    /**
     * reference pointer ensure atomicity of operation
     */
    private int referencePointer = 0;

    /**
     * read snapshot use pointer ensure atomicity of operation
     */
    private RouteDefinitionList[] definitionReadSnapshot = new RouteDefinitionList[2];

    private long validCacheDuration = TimeUnit.SECONDS.toSeconds(10);

    private final Lock lock = new ReentrantLock();

    private final Condition waitCondition = lock.newCondition();

    /**
     * 重复加载容差值
     */
    private long repeatLoadTolerance = 3000;
    private boolean daemonCacheLoaderRunning = false;


    private synchronized void startDaemon(){
        if (daemonCacheLoaderRunning){
            return;
        }
        daemonCacheLoaderRunning = true;
        Runnable runnable = cacheLoaderRunnable(1);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS,
                new SynchronousQueue<>(), r -> {
                    Thread thread = new Thread(r);
                    thread.setName("route-definition-loader-daemon-thread");
                    return thread;
                },new ThreadPoolExecutor.AbortPolicy());
        executor.submit(runnable);
    }

    private Runnable cacheLoaderRunnable(long interval){
        return ()->{
            while (daemonCacheLoaderRunning) {
                lock.lock();
                try {
                    refreshCache();
                    waitCondition.await(interval,TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }
        };
    }

    private void refreshCache(){
        int pointer = nextPointer();
        // 当前指针和下一个指针相同
        // 有其他线程已经修改了指针
        if ((this.referencePointer ^ pointer) == 0){
            return;
        }

        RouteDefinitionList routeDefinitionList = doLoad();

        // 在查询处理期间指针移动
        // 放弃修改
        if ((this.referencePointer ^ pointer) != 0){
            this.definitionReadSnapshot[pointer] = routeDefinitionList;
            // swap pointer
            swapReferencePointer();
        }
    }

    private void swapReferencePointer(){
        this.referencePointer = nextPointer();
    }

    private int nextPointer(){
        return ~this.referencePointer & 1;
    }
    private synchronized RouteDefinitionList doLoad(){
        RouteDefinitionList routeDefinitionList = this.definitionReadSnapshot[this.referencePointer];

        // 短期内加载过 直接用
        if (null != routeDefinitionList &&  System.currentTimeMillis() -
                (routeDefinitionList.expireTimestamp - validCacheDuration * 1000) < repeatLoadTolerance ){
            return routeDefinitionList;
        }
        routeDefinitionList = new RouteDefinitionList(new ArrayList<>(),System.currentTimeMillis() + validCacheDuration * 1000);


        List<Route> routes = routeDao.findAll();
        routes.parallelStream()
                .map(routeDefinitionConvert::toDefinition)
                .collect(Collectors.toList());

        // 后台刷新线程没有启动
        if (!daemonCacheLoaderRunning){
            this.definitionReadSnapshot[this.referencePointer] = routeDefinitionList;
        }
        return routeDefinitionList;
    }

    private RouteDefinitionList fromCache(){
        RouteDefinitionList routeDefinitionList = definitionReadSnapshot[referencePointer];
        if (null == routeDefinitionList){
            routeDefinitionList = doLoad();
        }else {
            // 通常情况下不会触发这个加载
            // 会有线程会对缓存进行更新
            long cacheTimestamp = routeDefinitionList.getExpireTimestamp();
            if (cacheTimestamp - System.currentTimeMillis() < repeatLoadTolerance){
                routeDefinitionList = doLoad();
                // notify daemon loader refresh cache
                waitCondition.signal();
            }
        }
        return routeDefinitionList;
    }

    @Override public Flux<RouteDefinition> getRouteDefinitions() {
        RouteDefinitionList definitionList = fromCache();
        if (null == definitionList){
            return Flux.empty();
        }
        List<RouteDefinition> routeDefinitions = definitionList
                .getRouteDefinitions();
        return Flux.fromIterable(routeDefinitions)
                .map(configDefinition -> new RouteDefinition());
    }

    @Getter
    private static class RouteDefinitionList{
        private final List<RouteDefinition> routeDefinitions;
        private final long expireTimestamp;

        public RouteDefinitionList(List<RouteDefinition> routeDefinitions, long expireTimestamp) {
            this.routeDefinitions = routeDefinitions;
            this.expireTimestamp = expireTimestamp;
        }
    }

    public static void main(String[] args) {

        // 0000 0001
        // 1111 1110
        // 1111 1101
        // 1000 0010

        // 0000 0001

        // 0000 0000
        // 1111 1111
        // 1111 1110
        // 1000 0001

        // 0000 0001

        DatabaseRouteDefinitionLoader databaseRouteDefinitionLoader = new DatabaseRouteDefinitionLoader();
        databaseRouteDefinitionLoader.startDaemon();

    }
}
