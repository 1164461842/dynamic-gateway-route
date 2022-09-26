package com.yjntc.gateway.initializer;

import com.yjntc.gateway.bean.ConfigDefinition;
import com.yjntc.gateway.bean.FactoryDefinition;
import com.yjntc.gateway.dao.ConfigDefinitionDao;
import com.yjntc.gateway.dao.FactoryDefinitionDao;
import com.yjntc.gateway.util.IdUtil;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.transaction.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author WangKangSheng
 * @date 2022-09-25 21:57
 */
public class DefinitionInitializer {

    private final ConfigDefinitionDao configDefinitionDao;
    private final FactoryDefinitionDao factoryDefinitionDao;

    private final ApplicationContext applicationContext;



    private DefinitionInitializer(ConfigDefinitionDao configDefinitionDao,
                                  FactoryDefinitionDao factoryDefinitionDao,
                                  ApplicationContext applicationContext) {
        Objects.requireNonNull(configDefinitionDao,"config definition dao must be not null");
        Objects.requireNonNull(factoryDefinitionDao,"factory definition dao must be not null");
        Objects.requireNonNull(applicationContext,"application context must be not null");
        this.configDefinitionDao = configDefinitionDao;
        this.factoryDefinitionDao = factoryDefinitionDao;
        this.applicationContext = applicationContext;
    }

    private TransactionManager transactionManager;
    private volatile static DefinitionInitializer initializer;
    private static final Lock LOCK = new ReentrantLock();

    private static final Class<AbstractGatewayFilterFactory> GATEWAY_FILTER_FACTORY_CLASS = AbstractGatewayFilterFactory.class;

    public static DefinitionInitializer getInstance(ConfigDefinitionDao configDefinitionDao,
                                                    FactoryDefinitionDao factoryDefinitionDao,
                                                    ApplicationContext applicationContext) {
        if (null == initializer){
            LOCK.lock();
            try {
                if (null == initializer){
                    initializer = new DefinitionInitializer(configDefinitionDao, factoryDefinitionDao, applicationContext);
                    initializer.transactionManager = applicationContext.getBean(TransactionManager.class);
                }
            }finally {
                LOCK.unlock();
            }
        }
        return initializer;
    }

    public synchronized void init() throws SystemException {
        Transaction transaction = createTransaction();
        try{
            // delete all
            factoryDefinitionDao.deleteAll();
            configDefinitionDao.deleteAll();

            // reload all
            List<FactoryDefinition> factoryDefinitions = loadFactoryDefinitions();

            // save all
            factoryDefinitionDao.saveAllAndFlush(factoryDefinitions);
            commit(transaction);
        }catch (Exception e){
            rollback(transaction);
            throw new RuntimeException(e);
        }
    }


    private Transaction createTransaction() throws SystemException {
        Transaction transaction = null;
        if (null != transactionManager){
            transaction = transactionManager.getTransaction();
        }
        return transaction;
    }

    private void rollback(Transaction transaction) throws SystemException {
        if (null != transaction){
            transaction.rollback();
        }
    }

    private void commit(Transaction transaction) throws SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        if (null != transaction){
            transaction.commit();
        }
    }

    public void refresh(){

    }


    public List<FactoryDefinition> loadFactoryDefinitions(){
        List<FactoryDefinition> list = new ArrayList<>();
        list.addAll(loadFilterFactoryDefinitions());
        list.addAll(loadPredicateFactoryDefinitions());
        return list;
    }


    /**
     * load all filter factories
     * @return List<FactoryDefinition>
     */
    protected final List<FactoryDefinition> loadFilterFactoryDefinitions(){
        Map<String, AbstractGatewayFilterFactory> gatewayFilterFactoryMap = applicationContext.getBeansOfType(AbstractGatewayFilterFactory.class);

        for (AbstractGatewayFilterFactory gatewayFilterFactory : gatewayFilterFactoryMap.values()) {
            Class<? extends AbstractGatewayFilterFactory> filterFactoryClass = gatewayFilterFactory.getClass();
            Class configClass = gatewayFilterFactory.getConfigClass();


        }

        return new ArrayList<>();
    }

    private List<ConfigDefinition> toDefinition(FactoryDefinition factoryDefinition,Configurable configurable){
        String factoryDefinitionId = factoryDefinition.getFactoryDefinitionId();
        Objects.requireNonNull(factoryDefinitionId,"factory definition id must be not null");
        Class configClass = configurable.getConfigClass();
        Field[] declaredFields = configClass.getDeclaredFields();
        List<ConfigDefinition> configDefinitions = new ArrayList<>(declaredFields.length);
        for (Field declaredField : declaredFields) {
            String simpleUuid = IdUtil.simpleUuid();
            ConfigDefinition configDefinition = new ConfigDefinition();
            configDefinition.setFactoryDefinitionId(factoryDefinitionId);
            configDefinition.setConfigDefinitionId(simpleUuid);
            configDefinition.setConfigClassName(configurable.getClass().getName());
            configDefinition.setConfigArgName(declaredField.getName());
            configDefinitions.add(configDefinition);
        }

        return configDefinitions;
    }


    /**
     * load all predicate factories
     * @return List<FactoryDefinition>
     */
    protected final List<FactoryDefinition> loadPredicateFactoryDefinitions(){

        return new ArrayList<>();
    }




}
