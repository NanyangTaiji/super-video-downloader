package com.myAllVideoBrowser.ui.main.proxies;

import androidx.databinding.ObservableField;
import com.myAllVideoBrowser.data.local.model.Proxy;
import com.myAllVideoBrowser.ui.main.base.BaseViewModel;
import com.myAllVideoBrowser.util.SharedPrefHelper;
import com.myAllVideoBrowser.util.proxy_utils.CustomProxyController;
import com.myAllVideoBrowser.util.scheduler.BaseSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class ProxiesViewModel extends BaseViewModel {

    private final CustomProxyController proxyController;
    private final BaseSchedulers baseSchedulers;
    private final SharedPrefHelper sharedPrefHelper;

    public final ObservableField<Proxy> currentProxy = new ObservableField<>(Proxy.noProxy());
    public final ObservableField<Proxy> userProxy = new ObservableField<>(Proxy.noProxy());
    public final ObservableField<List<Proxy>> proxiesList = new ObservableField<>(new ArrayList<>());
    public final ObservableField<Boolean> isProxyOn = new ObservableField<>(false);

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public ProxiesViewModel(
            CustomProxyController proxyController,
            BaseSchedulers baseSchedulers,
            SharedPrefHelper sharedPrefHelper
    ) {
        this.proxyController = proxyController;
        this.baseSchedulers = baseSchedulers;
        this.sharedPrefHelper = sharedPrefHelper;
    }

    @Override
    public void start() {
        if (compositeDisposable.size() >= 1) {
            compositeDisposable.dispose();
            compositeDisposable = new CompositeDisposable();
        }

        fetchProxies();
        loadProxyData();
    }

    private void loadProxyData() {
        Disposable disposable = Observable.fromCallable(() -> {
                    userProxy.set(sharedPrefHelper.getUserProxy());
                    currentProxy.set(proxyController.getCurrentSavedProxy());
                    isProxyOn.set(proxyController.isProxyOn());
                    return true;
                })
                .subscribeOn(baseSchedulers.getIo())
                .observeOn(baseSchedulers.getComputation())
                .subscribe();

        compositeDisposable.add(disposable);
    }

    private void fetchProxies() {
        Disposable disposable = proxyController.fetchUserProxy()
                .subscribeOn(baseSchedulers.getIo())
                .observeOn(baseSchedulers.getComputation())
                .subscribe(proxy -> {
                    List<Proxy> proxyList = new ArrayList<>();
                    proxyList.add((Proxy) proxy);
                    proxiesList.set(proxyList);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void stop() {
        compositeDisposable.clear();
    }

    public void setProxy(Proxy proxy) {
        proxyController.setCurrentProxy(proxy);
        currentProxy.set(proxy);
        isProxyOn.set(true);

        refreshList();
    }

    public void turnOffProxy() {
        proxyController.setIsProxyOn(false);
        isProxyOn.set(false);
    }

    public void turnOnProxy() {
        proxyController.setIsProxyOn(true);
        isProxyOn.set(true);
    }

    private void refreshList() {
        List<Proxy> refreshed = new ArrayList<>();
        List<Proxy> currentList = proxiesList.get();
        if (currentList != null) {
            refreshed.addAll(currentList);
        }
        proxiesList.set(refreshed);
    }

    public void setUserProxy(Proxy userProxy) {
        Disposable disposable = Observable.fromCallable(() -> {
                    this.userProxy.set(userProxy);
                    sharedPrefHelper.saveUserProxy(userProxy);
                    proxyController.setCurrentProxy(userProxy);
                    return true;
                })
                .subscribeOn(baseSchedulers.getIo())
                .observeOn(baseSchedulers.getComputation())
                .subscribe();

        compositeDisposable.add(disposable);
    }
}
