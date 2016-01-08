package com.loopeer.android.librarys.pullswitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopeer.android.librarys.PullDefaultHandler;
import com.loopeer.android.librarys.PullHandler;
import com.loopeer.android.librarys.PullSwitchView;
import com.loopeer.android.librarys.SwitcherHolder;

public class TestFragment2Web extends Fragment implements PullHandler {

    private SwitcherHolder switcherHolder;
    private WebView mWebView;

    public static TestFragment2Web newInstance(SwitcherHolder switcherHolder) {
        TestFragment2Web testFragment = new TestFragment2Web();
        testFragment.switcherHolder = switcherHolder;
        return testFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PullSwitchView pullSwitchView = (PullSwitchView) view.findViewById(R.id.switcher);
        pullSwitchView.setPullHandler(this);
        pullSwitchView.setSwitcherHolder(switcherHolder);
        mWebView = (WebView) view.findViewById(R.id.web_test);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl("http://www.baidu.com/");
    }

    @Override
    public boolean checkCanDoPullDown(View content) {
        return PullDefaultHandler.checkContentCanBePulledDown(content);
    }

    @Override
    public boolean checkCanDoPullUp(View content) {
        return PullDefaultHandler.checkContentCanBePulledUp(content);
    }

}
