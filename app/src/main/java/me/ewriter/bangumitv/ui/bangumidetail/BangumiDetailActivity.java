package me.ewriter.bangumitv.ui.bangumidetail;

import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import me.ewriter.bangumitv.R;
import me.ewriter.bangumitv.base.BaseActivity;
import me.ewriter.bangumitv.utils.ToastUtils;
import me.ewriter.bangumitv.utils.Tools;
import me.ewriter.bangumitv.widget.HorizonSpacingItemDecoration;

/**
 * Created by zubin on 2016/9/24.
 */

public class BangumiDetailActivity extends BaseActivity implements BangumiDetailContract.View, View.OnClickListener {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mCoverImg;
    private ViewGroup mCoverGroup;
    private TextView mCoverName, mCoverSummary, mCoverTag, mCoverScore;
    private ProgressBar mProgressbar;
    private FloatingActionButton mFab;

    private BangumiDetailContract.Presenter mPresenter;
    private String mBangumiId;
    private String mCommonImageUrl;
    private String mLargeImageUrl;
    private String mBangumiName;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_bangumi_detail;
    }

    @Override
    protected void initView() {

        mPresenter = new BangumiDetailPresenter(this);
        mPresenter.subscribe();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.bangumi_detail_recyclerview);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCoverImg = (ImageView) findViewById(R.id.cover_img);
        mCoverName = (TextView) findViewById(R.id.cover_name);
        mCoverSummary = (TextView) findViewById(R.id.cover_summary);
        mCoverTag = (TextView) findViewById(R.id.cover_tag);
        mCoverGroup = (ViewGroup) findViewById(R.id.cover_group);
        mCoverScore = (TextView) findViewById(R.id.cover_score);
        mProgressbar = (ProgressBar) findViewById(R.id.loading_progreeebar);
        mFab = (FloatingActionButton) findViewById(R.id.edit_fab);

        setUpCover();
        setUpToolbar();
        setUpRecyclerView();
        mFab.setOnClickListener(this);

        mPresenter.requestWebDetail(mBangumiId);
    }

    private void setUpRecyclerView() {
        mRecyclerView.addItemDecoration(new HorizonSpacingItemDecoration(Tools.getPixFromDip(16)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    /** 初始化封面模糊效果*/
    private void setUpCover() {
        mCoverName.setText(mBangumiName);
        Picasso.with(this).load(mLargeImageUrl)
                .placeholder(R.drawable.img_on_load)
                .error(R.drawable.img_on_error)
                .into(mCoverImg);
        mPresenter.setUpCover(this, mCoverGroup, mCommonImageUrl);
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getSupportActionBar().setTitle(getString(R.string.bangumi_detail_title));
        mCollapsingToolbarLayout.setTitleEnabled(false);
        mCollapsingToolbarLayout.setExpandedTitleGravity(GravityCompat.START);
    }

    @Override
    protected void initBefore() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Fade());
        }

        mBangumiId = getIntent().getStringExtra("bangumiId");
        mCommonImageUrl = getIntent().getStringExtra("common_url");
        mBangumiName = getIntent().getStringExtra("name");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("large_url"))) {
            mLargeImageUrl = getIntent().getStringExtra("large_url");
        } else {
            mLargeImageUrl = mCommonImageUrl.replace("/c/", "/l/");
        }
    }

    @Override
    public void setPresenter(BangumiDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void refresh(Items items) {
        MultiTypeAdapter adapter = new MultiTypeAdapter(items);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setSummary(String text) {
        mCoverSummary.setText(text);
    }

    @Override
    public void setScore(String score) {
        mCoverScore.setText(String.format(getString(R.string.score), score));
    }

    @Override
    public void setTag(String tag) {
        mCoverTag.setText(String.format(getString(R.string.tags), tag));
    }

    @Override
    public void setFabVisible(int visible) {
        mFab.setVisibility(visible);
    }

    @Override
    public void hideProgress() {
        if (mProgressbar != null) {
            mProgressbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showShortToast(this, msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_fab:
                mPresenter.clickFab(BangumiDetailActivity.this, mBangumiId);
                break;
        }
    }
}
