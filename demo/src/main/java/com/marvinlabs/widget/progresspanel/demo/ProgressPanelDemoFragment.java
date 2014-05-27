package com.marvinlabs.widget.progresspanel.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fr.marvinlabs.widget.demo.R;

/**
 * @author Vincent Mimoun-Prat @ MarvinLabs
 * @date {17/06/13}
 */
public class ProgressPanelDemoFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<String[]> {

    private static final String ARG_PROGRESS_PANEL_TYPE = "ProgressPanelType";
    private ProgressFragment progressFragment;

    public static ProgressPanelDemoFragment newInstance(ProgressPanelType type) {
        ProgressPanelDemoFragment f = new ProgressPanelDemoFragment();
        f.setArguments(getArguments(type));
        return f;
    }

    public static Bundle getArguments(ProgressPanelType type) {
        Bundle args = new Bundle(1);
        args.putInt(ARG_PROGRESS_PANEL_TYPE, type.ordinal());
        return args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_progresspanel_demo, null, false);

        // Load the fragment we want according to the arguments
        ProgressPanelType type = ProgressPanelType.values()[getArguments().getInt(ARG_PROGRESS_PANEL_TYPE)];
        switch (type) {
            case SIMPLE:
                progressFragment = ProgressFragment.newInstance(R.layout.fragment_progresspanel_simple);
                break;
            case CUSTOM:
                progressFragment = ProgressFragment.newInstance(R.layout.fragment_progresspanel_custom);
                break;
            default:
                throw new IllegalArgumentException("Progress panel type must be specified");
        }
        getChildFragmentManager().beginTransaction().replace(R.id.child_container, progressFragment).commit();

        // Bind buttons
        Button withoutDataButton = (Button) root.findViewById(R.id.btn_reload_data);
        withoutDataButton.setOnClickListener(this);

        Button withDataButton = (Button) root.findViewById(R.id.btn_reload_nodata);
        withDataButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.btn_reload_data) {
            progressFragment.setLoading();
            getLoaderManager().restartLoader(DummyStringLoader.LOADER_ID, null, this);
        } else if (id == R.id.btn_reload_nodata) {
            progressFragment.setLoading();
            getLoaderManager().restartLoader(DummyNullLoader.LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle bundle) {
        if (id == DummyNullLoader.LOADER_ID) {
            return new DummyNullLoader(getActivity());
        } else if (id == DummyStringLoader.LOADER_ID) {
            return new DummyStringLoader(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] strings) {
        progressFragment.setData(strings);
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        progressFragment.setLoading();
    }

    public static enum ProgressPanelType {
        SIMPLE, CUSTOM
    }

    /**
     * Loader that waits 3 seconds before returning null
     */
    private static class DummyNullLoader extends AsyncTaskLoader<String[]> {

        static final int LOADER_ID = 0x10;

        public DummyNullLoader(Context context) {
            super(context);
        }

        @Override
        public String[] loadInBackground() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            return null;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    /**
     * Loader that waits 3 seconds before returning the list of countries in the EU
     */
    private static class DummyStringLoader extends AsyncTaskLoader<String[]> {

        static final int LOADER_ID = 0x20;

        public DummyStringLoader(Context context) {
            super(context);
        }

        @Override
        public String[] loadInBackground() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }

            return new String[]{
                    "Austria",
                    "Belgium",
                    "Bulgaria",
                    "Cyprus",
                    "Czech Republic",
                    "Denmark",
                    "Estonia",
                    "Finland",
                    "France",
                    "Germany",
                    "Greece",
                    "Hungary",
                    "Ireland",
                    "Italy",
                    "Latvia",
                    "Lithuania",
                    "Luxembourg",
                    "Malta",
                    "Netherlands",
                    "Poland",
                    "Portugal",
                    "Romania",
                    "Slovakia",
                    "Slovenia",
                    "Spain",
                    "Sweden",
                    "United Kingdom"
            };
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
