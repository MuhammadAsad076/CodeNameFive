package com.itridtechnologies.codenamefive.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.itridtechnologies.codenamefive.Models.GoogleMapsClusterItem;

public class ClusterImageRenderer extends DefaultClusterRenderer<GoogleMapsClusterItem> {

    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private final int markerHeight;
    private final int markerWidth;

    //constructor with params
    public ClusterImageRenderer(Context context, GoogleMap map, ClusterManager<GoogleMapsClusterItem> clusterManager) {
        super(context, map, clusterManager);

        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = 24;
        markerHeight = 24;
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        imageView.setPadding(4, 4, 4, 4);
        iconGenerator.setContentView(imageView);

    }

    //override methods
    @Override
    protected void onBeforeClusterItemRendered(@NonNull GoogleMapsClusterItem item, @NonNull MarkerOptions markerOptions) {

        imageView.setImageResource(item.getMarkerIcon());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<GoogleMapsClusterItem> cluster) {
        return false;
    }

}//end class
