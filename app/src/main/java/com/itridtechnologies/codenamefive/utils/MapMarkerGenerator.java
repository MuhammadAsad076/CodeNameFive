package com.itridtechnologies.codenamefive.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.maps.android.ui.IconGenerator;

public final class MapMarkerGenerator {

    public static Bitmap getMarkerIcon(Context context, int imageRes) {

        ImageView imageView = new ImageView(context.getApplicationContext());
        IconGenerator iconGenerator = new IconGenerator(context.getApplicationContext());

        imageView.setImageResource(imageRes);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(128, 128));
        iconGenerator.setContentView(imageView);
        iconGenerator.setBackground(null);

        return iconGenerator.makeIcon();
    }// end method

}//end class
