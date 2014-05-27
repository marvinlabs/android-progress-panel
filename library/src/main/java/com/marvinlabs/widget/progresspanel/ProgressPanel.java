package com.marvinlabs.widget.progresspanel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marvinlabs.widget.progresspanel.R;

/**
 * A panel that can either show a progress indicator, a no-data indicator or some content. This is derived from the
 * ListFragment.
 *
 * The content can either be set programmatically (using one of the setContentView methods) or can be taken from the
 * XML file (in that case, it should be set as a child of the ProgressPanel with the id "@id/content_view").
 *
 * You can also set custom views for the no-data indicator and the progress indicator. These views can also be set
 * programmatically or specified as children of the ProgressPanel within an XML layout (with the respective ids
 * "@id/empty_view" and "@id/progress_view").
 *
 * @author Vincent Mimoun-Prat @ MarvinLabs (www.marvinlabs.com)
 */
public class ProgressPanel extends RelativeLayout {

    private ViewGroup mEmptyContainer;
    private ViewGroup mProgressContainer;
    private ViewGroup mContentContainer;
    private View mProgressView;
    private View mContentView;
    private View mEmptyView;
    private boolean mContentShown;
    private boolean mIsContentEmpty;

    public ProgressPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ProgressPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressPanel(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Get user views if any
        View userContentView = findViewById(R.id.content_view);
        View userEmptyView = findViewById(R.id.empty_view);
        View userProgressView = findViewById(R.id.progress_view);

        // Clear our current hierarchy and replace with the default panel structure
        removeAllViews();
        LayoutInflater.from(getContext()).inflate(R.layout.widget_progresspanel, this, true);

        // Now replace default views with user views if any
        if (userContentView != null) {
            setContentView(userContentView);
        } else if (findViewById(R.id.content_view) != null) {
            setContentView(findViewById(R.id.content_view));
        }

        if (userEmptyView != null) {
            setEmptyView(userEmptyView);
        } else if (findViewById(R.id.empty_view) != null) {
            setEmptyView(findViewById(R.id.empty_view));
        }

        if (userProgressView != null) {
            setProgressView(userProgressView);
        } else if (findViewById(R.id.progress_view) != null) {
            setProgressView(findViewById(R.id.progress_view));
        }
    }

    /**
     * Return content view or null if the content view has not been initialized.
     *
     * @return content view or null
     * @see #setContentView(android.view.View)
     * @see #setContentView(int)
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * Set the content view to an explicit view. If the content view was installed earlier, the content will be replaced
     * with a new view.
     *
     * @param view The desired content to display. Value can't be null.
     * @see #setContentView(int)
     * @see #getContentView()
     */
    public void setContentView(View view) {
        ensureContent();
        if (view == null) {
            throw new IllegalArgumentException("Content view can't be null");
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        if (mContentView == null) {
            mContentContainer.addView(view);
        } else {
            int index = mContentContainer.indexOfChild(mContentView);
            // replace content view
            mContentContainer.removeView(mContentView);
            mContentContainer.addView(view, index, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
        }
        mContentView = view;
    }

    /**
     * Set the content content from a layout resource.
     *
     * @param layoutResId Resource ID to be inflated.
     * @see #setContentView(android.view.View)
     * @see #getContentView()
     */
    public void setContentView(int layoutResId) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View contentView = layoutInflater.inflate(layoutResId, null);
        setContentView(contentView);
    }

    /**
     * The default content for a ProgressFragment has a TextView that can be shown when the content is empty {@link
     * #setContentEmpty(boolean)}. If you would like to have it shown, call this method to supply the text it should
     * use.
     *
     * @param resId Identification of string from a resources
     * @see #setEmptyText(CharSequence)
     */
    public void setEmptyText(int resId) {
        setEmptyText(getContext().getString(resId));
    }

    /**
     * The default content for a ProgressFragment has a TextView that can be shown when the content is empty {@link
     * #setContentEmpty(boolean)}. If you would like to have it shown, call this method to supply the text it should
     * use.
     *
     * @param text Text for empty view
     * @see #setEmptyText(int)
     */
    public void setEmptyText(CharSequence text) {
        ensureContent();
        if (mEmptyView != null && mEmptyView instanceof TextView) {
            ((TextView) mEmptyView).setText(text);
        } else {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
    }

    /**
     * Return empty view or null if the empty view has not been initialized.
     *
     * @return empty view or null
     * @see #setEmptyView(android.view.View)
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * Set the empty view to an explicit view. If the view was installed earlier, it will be replaced with a new view.
     *
     * @param view The desired view. Value can't be null.
     */
    public void setEmptyView(View view) {
        ensureContent();
        if (view == null) {
            throw new IllegalArgumentException("Empty view can't be null");
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        if (mEmptyView == null) {
            mEmptyContainer.addView(view);
        } else {
            int index = mEmptyContainer.indexOfChild(mEmptyView);
            // replace content view
            mEmptyContainer.removeView(mEmptyView);
            mEmptyContainer.addView(view, index);
        }
        mEmptyView = view;
    }

    /**
     * Set the progress view to an explicit view. If the view was installed earlier, it will be replaced with a new
     * view.
     *
     * @param view The desired view. Value can't be null.
     */
    public void setProgressView(View view) {
        ensureContent();
        if (view == null) {
            throw new IllegalArgumentException("Progress view can't be null");
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        if (mProgressContainer.getChildCount() == 0) {
            mProgressContainer.addView(view);
        } else {
            mProgressContainer.removeAllViews();
            mProgressContainer.addView(view);
        }
    }

    /**
     * Control whether the content is being displayed. You can make it not displayed if you are waiting for the initial
     * data to show in it. During this time an indeterminant progress indicator will be shown instead.
     *
     * @param shown If true, the content view is shown; if false, the progress indicator. The initial value is true.
     * @see #setContentShownNoAnimation(boolean)
     */
    public void setContentShown(boolean shown) {
        setContentShown(shown, true);
    }

    /**
     * Like {@link #setContentShown(boolean)}, but no animation is used when transitioning from the previous state.
     *
     * @param shown If true, the content view is shown; if false, the progress indicator. The initial value is true.
     * @see #setContentShown(boolean)
     */
    public void setContentShownNoAnimation(boolean shown) {
        setContentShown(shown, false);
    }

    /**
     * Control whether the content is being displayed. You can make it not displayed if you are waiting for the initial
     * data to show in it. During this time an indeterminant progress indicator will be shown instead.
     *
     * @param shown   If true, the content view is shown; if false, the progress indicator. The initial value is true.
     * @param animate If true, an animation will be used to transition to the new state.
     */
    private void setContentShown(boolean shown, boolean animate) {
        ensureContent();
        if (mContentShown == shown) {
            return;
        }

        View containerPicked = isContentEmpty() ? mEmptyContainer : mContentContainer;

        mContentShown = shown;
        if (shown) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
                containerPicked.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                containerPicked.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            containerPicked.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                containerPicked.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                containerPicked.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            containerPicked.setVisibility(View.GONE);
        }
    }

    /**
     * Returns true if content is empty. The default content is not empty.
     *
     * @return true if content is null or empty
     * @see #setContentEmpty(boolean)
     */
    public boolean isContentEmpty() {
        return mIsContentEmpty;
    }

    /**
     * If the content is empty, then set true otherwise false. The default content is not empty.
     *
     * @param isEmpty true if content is empty else false
     * @see #isContentEmpty()
     */
    public void setContentEmpty(boolean isEmpty) {
        mIsContentEmpty = isEmpty;

        if (mProgressContainer.getVisibility() != View.VISIBLE) {
            if (isEmpty) {
                mContentContainer.setVisibility(View.GONE);
                mEmptyContainer.setVisibility(View.VISIBLE);
            } else {
                mContentContainer.setVisibility(View.VISIBLE);
                mEmptyContainer.setVisibility(View.GONE);
            }
        }
    }

    protected View getContentContainer() {
        return mContentContainer;
    }

    protected View getProgressContainer() {
        return mProgressContainer;
    }

    protected View getEmptyContainer() {
        return mEmptyContainer;
    }

    /**
     * Initialization views.
     */
    private void ensureContent() {
        if (mContentContainer != null && mProgressContainer != null && mEmptyContainer != null) {
            return;
        }

        mProgressContainer = (ViewGroup) findViewById(R.id.progress_container);
        if (mProgressContainer == null) {
            throw new RuntimeException(
                    "Your content must have a ViewGroup whose id attribute is 'R.id.progress_container'");
        }

        mContentView = findViewById(R.id.content_view);
        mContentContainer = (ViewGroup) findViewById(R.id.content_container);
        if (mContentContainer == null) {
            throw new RuntimeException(
                    "Your content must have a ViewGroup whose id attribute is 'R.id.content_container'");
        }

        mEmptyView = findViewById(R.id.empty_view);
        mEmptyContainer = (ViewGroup) findViewById(R.id.empty_container);
        if (mEmptyContainer == null) {
            throw new RuntimeException(
                    "Your content must have a ViewGroup whose id attribute is 'R.id.empty_container'");
        }

        mEmptyContainer.setVisibility(View.GONE);

        // We are starting without a content, so assume we won't
        // have our data right away and start with the progress indicator.
        if (mContentView == null) {
            setContentShown(false, false);
        }
    }
}
