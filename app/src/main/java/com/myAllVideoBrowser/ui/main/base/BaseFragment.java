package com.myAllVideoBrowser.ui.main.base;

import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.google.android.material.color.MaterialColors;
import com.myAllVideoBrowser.R;
import dagger.android.support.DaggerFragment;

public abstract class BaseFragment extends DaggerFragment {

    public View fixPopup(FrameLayout container, View popupSource) {
        View myView = new View(container.getContext());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(30, 40);
        int[] location = new int[2];
        popupSource.getLocationOnScreen(location);
        myView.setVisibility(View.INVISIBLE);
        int x = location[0];
        int y = location[1];
        params.topMargin = y;
        params.leftMargin = x;
        myView.setLayoutParams(params);

        container.removeAllViews();
        container.addView(myView);

        return myView;
    }

    public int getThemeBackgroundColor() {
        int color = MaterialColors.getColor(requireContext(), R.attr.colorSurface, Color.YELLOW);
        return color;
    }
}
