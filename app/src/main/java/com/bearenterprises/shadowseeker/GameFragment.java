package com.bearenterprises.shadowseeker;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bearenterprises.EXTRAS.EXTRAS;


public class GameFragment extends Fragment {

    private GameView game;
    private String levelName;


    public GameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_game, container, false);
        game = (GameView) view.findViewById(R.id.game_view);
        Bundle args = this.getArguments();
        String level = args.getString(EXTRAS.GET_LEVEL_NAME);
        game.setLevelName(level);
        game.initGame(view.getContext());
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
