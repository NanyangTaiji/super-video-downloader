package com.myAllVideoBrowser.ui.main.proxies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.myAllVideoBrowser.data.local.model.Proxy;
import com.myAllVideoBrowser.databinding.FragmentProxiesBinding;
import com.myAllVideoBrowser.ui.component.adapter.ProxiesListener;
import com.myAllVideoBrowser.ui.main.base.BaseFragment;
import com.myAllVideoBrowser.ui.main.home.MainActivity;
import com.myAllVideoBrowser.util.proxy_utils.CustomProxyController;
import javax.inject.Inject;

public class ProxiesFragment extends BaseFragment {

    public static ProxiesFragment newInstance() {
        return new ProxiesFragment();
    }

    @Inject
    CustomProxyController proxyController;

    @Inject
    MainActivity mainActivity;

    private FragmentProxiesBinding dataBinding;
    private ProxiesViewModel proxiesViewModel;
    private final ProxiesListener proxiesListener = new ProxiesListener() {
        @Override
        public void onProxyClicked(View view, Proxy proxy) {
            setProxy(proxy);
        }

        @Override
        public void onProxyToggle(boolean isChecked) {
            if (isChecked) {
                proxiesViewModel.turnOnProxy();
            } else {
                proxiesViewModel.turnOffProxy();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        proxiesViewModel = mainActivity.proxiesViewModel;

        dataBinding = FragmentProxiesBinding.inflate(inflater, container, false);
        dataBinding.saveProxyButton.setOnClickListener(view -> {
            String host = dataBinding.hostEditText.getText().toString();
            String portStr = dataBinding.portEditText.getText().toString();
            Integer port = null;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                // port remains null
            }
            String user = dataBinding.loginEditText.getText().toString();
            String password = dataBinding.passwordEditText.getText().toString();

            if (isValidHost(host) && isValidPort(portStr)) {
                Proxy newProxy = new Proxy(
                        String.valueOf(host.hashCode()),
                        host,
                        portStr,
                        user,
                        password,
                        false,
                        "",
                        "",
                        "",
                        ""
                );
                setProxy(newProxy);
            } else {
                Toast.makeText(getContext(), "Invalid host or port", Toast.LENGTH_SHORT).show();
            }
        });
        dataBinding.setListener(proxiesListener);
        dataBinding.setViewModel(proxiesViewModel);

        int color = getThemeBackgroundColor();
        dataBinding.proxiesContainer.setBackgroundColor(color);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().popBackStack();
            }
        });

        return dataBinding.getRoot();
    }

    private void setProxy(Proxy proxy) {
        proxiesViewModel.setUserProxy(proxy);
        proxiesViewModel.setProxy(proxy);
    }

    private boolean isValidPort(String port) {
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber >= 1 && portNumber <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidHost(String host) {
        return !host.isEmpty();
    }
}
