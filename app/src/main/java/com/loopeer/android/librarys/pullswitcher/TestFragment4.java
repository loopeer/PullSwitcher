package com.loopeer.android.librarys.pullswitcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TestFragment4 extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static TestFragment4 newInstance() {
        TestFragment4 testFragment = new TestFragment4();
        return testFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fragment4, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initTestRecyclerView(view);
    }

    private void initTestRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        mAdapter = new TestRecyclerAdapter(new String[]{
                "We've partnered with Udacity to laun",
                "ch a new online course - Google Cast and Android TV Development. This course ",
                "teaches you how to extend your existing Android app to work with these technologies. It’s packed with practical advice,",
                " code snippets, and deep dives into sample code." ,
                "You can take advantage of ",
                "both, without",
                " having to rewrite your app.",
                " Android TV is just ",
                "Android on a new form factor, and the Leanback library makes it easy to add a big screen ",
                "and cinematic UI to your app. Google Cast comes with great samples and guides to help you get started. Google also provides the Cast Companion Library",
                " which makes it faster and easier to add cast to your Android app.",
        });
        mRecyclerView.setAdapter(mAdapter);
    }
}

