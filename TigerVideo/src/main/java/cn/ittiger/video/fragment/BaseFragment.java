package cn.ittiger.video.fragment;

import cn.ittiger.video.R;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceFragment;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.TimeUnit;

/**
 * @author laohu
 * @site http://ittiger.cn
 */
public abstract class BaseFragment<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
        extends MvpLceFragment<CV, M, V, P> {

    protected Context mContext;
    private Subscription mSubscription;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.base_fragment_layout, container, false);

        view.addView(getContentView(inflater, savedInstanceState));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if(isInitRefreshEnable() && isDelayRefreshEnable() == false) {
            loadData(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isInitRefreshEnable() == false && isDelayRefreshEnable()) {
            loadData(false);
        }
    }

    private void refreshData(final boolean pullToRefresh) {

        if(presenter != null) {
            loadData(pullToRefresh);
        } else {
            mSubscription = Observable.timer(50, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            refreshData(pullToRefresh);
                        }
                    });
        }
    }

    /**
     * Fragment数据视图
     * @param inflater
     * @param savedInstanceState
     * @return
     */
    public abstract View getContentView(LayoutInflater inflater, @Nullable Bundle savedInstanceState);

    public boolean isInitRefreshEnable() {

        return true;
    }

    public boolean isDelayRefreshEnable() {

        return false;
    }

    public abstract int getName();

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {

        return getActivity().getString(R.string.load_failed);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        if(mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}