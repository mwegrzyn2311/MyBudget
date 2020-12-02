package guice_module;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import service.FxmlLoaderService;


public class MyBudgetModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new JpaPersistModule("db-manager"));
        bind(FxmlLoaderService.class).asEagerSingleton();
    }
}
