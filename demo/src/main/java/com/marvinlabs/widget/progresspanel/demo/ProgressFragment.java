package com.marvinlabs.widget.progresspanel.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.marvinlabs.widget.demo.R;
import fr.marvinlabs.widget.progress.ProgressPanel;

/**
 * @author Vincent Mimoun-Prat @ MarvinLabs
 * @date {17/06/13}
 */
public class ProgressFragment extends Fragment {

    private static final String ARG_LAYOUT_ID = "LayoutId";
    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    private ListView listView;
    private ProgressPanel progressPanel;

    public static ProgressFragment newInstance(int layoutId) {
        ProgressFragment f = new ProgressFragment();
        f.setArguments(getArguments(layoutId));
        return f;
    }

    public static Bundle getArguments(int layoutId) {
        Bundle args = new Bundle(1);
        args.putInt(ARG_LAYOUT_ID, layoutId);
        return args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getArguments().getInt(ARG_LAYOUT_ID);
        View root = inflater.inflate(layoutId, null, false);


        progressPanel = (ProgressPanel) root.findViewById(R.id.progress_panel);
        listView = (ListView) root.findViewById(R.id.content_view);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        data = new ArrayList<Map<String, String>>();
        setData(null);
    }

    /**
     * Show the loading progress indicator
     */
    public void setLoading() {
        progressPanel.setContentShown(false);
    }

    /**
     * Set the data in the list view
     */
    public void setData(String[] names) {
        data.clear();
        if (names != null) {
            for (String name : names) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", name);
                data.add(map);
            }
        }

        adapter = new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_1,
                new String[]{"name"},
                new int[]{android.R.id.text1});
        listView.setAdapter(adapter);

        progressPanel.setContentEmpty(adapter.isEmpty());
        progressPanel.setContentShown(true);
    }
}
