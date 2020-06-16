package com.mp2.baymax20.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.mp2.baymax20.R;

import java.util.ArrayList;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.NewViewHolder>{

    Context context;
    private ArrayList<String> data;
    private OnAcademyVideoClick onAcademyVideoClick;
    public static final String DEVELOPER_KEY = "AIzaSyBw6ndLwZ2dGREZP32jHlbBYaTr2V2k7iY";

    public NewAdapter(Context context, OnAcademyVideoClick onAcademyVideoClick, ArrayList<String> data){
        this.context = context;
        this.onAcademyVideoClick = onAcademyVideoClick;
        this.data = data;
    }


    class NewViewHolder extends RecyclerView.ViewHolder{

        protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        YouTubeThumbnailView youTubeThumbnailView;
        protected ImageView playButton;

        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            playButton=(ImageView)itemView.findViewById(R.id.btnYoutube_player);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, DEVELOPER_KEY, data.get(getLayoutPosition()));
                    context.startActivity(intent);
                }
            });
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
        }
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_new,parent,false);
        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewViewHolder holder, final int position) {

        final YouTubeThumbnailLoader.OnThumbnailLoadedListener  onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener(){
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                onAcademyVideoClick.onAcademyVideoReady();
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                onAcademyVideoClick.onAcademyVideoReady();
            }
        };

        holder.youTubeThumbnailView.initialize(DEVELOPER_KEY, new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(data.get(position));
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnAcademyVideoClick{
        void onAcademyVideoReady();
    }


}
