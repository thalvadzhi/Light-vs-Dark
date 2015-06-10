package com.bearenterprises.shadowseeker;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bearenterprises.EXTRAS.EXTRAS;



public class ButtonFragment extends Fragment {

    private OnButtonClickedListener mListener;

    private Button playButton, exitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_button, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playButton = (Button) view.findViewById(R.id.button_play);
        exitButton = (Button) view.findViewById(R.id.button_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(EXTRAS.EXIT_BUTTON);
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(EXTRAS.PLAY_BUTTON);
            }
        });
        TextView tv = (TextView) view.findViewById(R.id.textView_game_name_button);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "avantgarde.ttf");
        tv.setTypeface(tf);
    }


    public void onButtonPressed(String button) {
        if (mListener != null) {
            if(button == EXTRAS.PLAY_BUTTON){
                mListener.onPlayButtonClicked(button);
            }else if (button == EXTRAS.EXIT_BUTTON){
                mListener.onExitButtonClicked(button);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnButtonClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnButtonClickedListener {
        public void onPlayButtonClicked(String button);
        public void onExitButtonClicked(String button);
    }

}
