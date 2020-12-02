package guice_module;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import service.FxmlLoaderService;

import javax.persistence.EntityManager;


public class MyBudgetModule extends AbstractModule {

    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_CACHE
            = new ThreadLocal<EntityManager>();

    @Override
    protected void configure() {
        install(new JpaPersistModule("db-manager"));
        bind(FxmlLoaderService.class).asEagerSingleton();
    }
}
