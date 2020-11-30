package guice_module;

import com.google.inject.AbstractModule;
import service.FxmlLoaderService;

public class MyBudgetModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FxmlLoaderService.class).asEagerSingleton();
    }
}
