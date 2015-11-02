# PullSwitcher

This library is learn from [liaohuqiu/android-Ultra-Pull-To-Refresh](https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh) and learn the 
pull to next layout effect from [zzz40500/Android-PullToNextLayout](https://github.com/zzz40500/Android-PullToNextLayout)

Screeshot
====
![](/screenshot/screenshot.gif)

Usage
====
###1. Add adapter and holder in activity

```java
    private SwitcherAdapter adapter;
    private SwitcherHolder mSwitchHolder;
    
    ...{
        mSwitchHolder = new SwitcherHolder(containerLayout);
        adapter = new TestSwitcherAdapter(getSupportFragmentManager(), mSwitchHolder);
        mSwitchHolder.setAdapter(adapter);
        }

```
Then, create your custom switcher adapter.
```java
public class TestSwitcherAdapter extends SwitchFragmentAdapter {

    public TestSwitcherAdapter(FragmentManager fm, SwitcherHolder switchHolder) {
        super(fm, switchHolder);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = TestFragment1Recycler.newInstance(getSwitcherHolder());
                break;
            case 1:
                fragment = TestFragment2Scroll.newInstance(getSwitcherHolder());
                break;
            case 2:
                fragment = TestFragment3ViewPager.newInstance(getSwitcherHolder());
                break;
            default:
                fragment = null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
```
###2. Create cutom fragment

You can create the single fragment or view by the **PullSwitchView**, add the layout. set the **PullSwitchView**as your layou which layout you want to move. PullSwitchView can host only one direct child. And the footer and header must implements FooterImpl or HeaderImpl. The pullSwitchView will add header and footer for you by default. But you can set your custom view which must implements the Impl.
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.loopeer.android.librarys.PullSwitchView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/switcher"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#dddddd">

    <LinearLayout
        android:id="@+id/text_wrapper"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/my_recycler_view"
            android:scrollbars="vertical"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </LinearLayout>

</com.loopeer.android.librarys.PullSwitchView>
```
init your pullswitchview
```java
    private void initSwitchView(View view) {
        pullSwitchView = (PullSwitchView) view.findViewById(R.id.switcher);
        pullSwitchView.setPullHandler(this);
        pullSwitchView.setSwitcherHolder(onPageChangeListener);
    }
```
**setPullHandler()** to add which one you want to observe. You can set your header and footer. Can set your custom tip show text. 

Make fragment implements **PullHandler**.To concrete some method as:
```java
@Override
    public boolean checkCanDoPullDown(View content) {
        return PullDefaultHandler.checkContentCanBePulledDown(mRecyclerView);
    }

    @Override
    public boolean checkCanDoPullUp(View content) {
        return PullDefaultHandler.checkContentCanBePulledUp(mRecyclerView);
    }

    @Override
    public void pullDownStartSwitch() {
        onPageChangeListener.onPrePage();
    }

    @Override
    public void pullUpStartSwitch() {
        onPageChangeListener.onNextPage();
    }
```
**checkContentCanBePulledDown(View view)** The view set is which view you want to observe. The wrapper will onLayout by the view scroll status.


License
====
<pre>
Copyright 2015 Loopeer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
