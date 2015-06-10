package com.bearenterprises.shadowseeker;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.bearenterprises.Adapter.LevelListAdapter;
import com.bearenterprises.EXTRAS.EXTRAS;
import com.bearenterprises.EXTRAS.Utilities;

import java.util.ArrayList;
import java.util.Arrays;


public class LevelFragment extends Fragment {

    private ListView levels;
    private onLevelListInteraction mListener;
    private Button backButton;


    public LevelFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView =  inflater.inflate(R.layout.fragment_level, container, false);
        levels = (ListView) rootView.findViewById(R.id.listView_levels);
        backButton = (Button) rootView.findViewById(R.id.back_button);
        ArrayList<String> names = Utilities.getAllLevels(getActivity());
        LevelListAdapter adapter = new LevelListAdapter(getActivity(),names);
        levels.setAdapter(adapter);
        levels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onButtonPressed(position);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void onButtonPressed(int position) {
        if (mListener != null) {
            mListener.onLevelClicked(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onLevelListInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onLevelListInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface onLevelListInteraction {
        public void onLevelClicked(int position);
    }

}
