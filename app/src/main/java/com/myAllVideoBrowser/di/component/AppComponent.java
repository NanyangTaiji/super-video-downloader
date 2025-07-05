package com.myAllVideoBrowser.di.component;

import com.myAllVideoBrowser.DLApplication;
import com.myAllVideoBrowser.di.module.ActivityBindingModule;
import com.myAllVideoBrowser.di.module.AppModule;
import com.myAllVideoBrowser.di.module.DatabaseModule;
import com.myAllVideoBrowser.di.module.MyWorkerModule;
import com.myAllVideoBrowser.di.module.NetworkModule;
import com.myAllVideoBrowser.di.module.RepositoryModule;
import com.myAllVideoBrowser.di.module.UtilModule;
import com.myAllVideoBrowser.di.module.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                AppModule.class,
                ActivityBindingModule.class,
                UtilModule.class,
                DatabaseModule.class,
                NetworkModule.class,
                RepositoryModule.class,
                ViewModelModule.class,
                MyWorkerModule.class
        }
)
public interface AppComponent extends AndroidInjector<DLApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(DLApplication application);

        AppComponent build();
    }
}
