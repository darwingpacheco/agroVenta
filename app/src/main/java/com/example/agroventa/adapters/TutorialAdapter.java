package com.example.agroventa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agroventa.R;
import com.example.agroventa.data.Tutorial;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {
    private List<Tutorial> tutorials;
    private Context context;

    public TutorialAdapter(List<Tutorial> tutorials, Context context) {
        this.tutorials = tutorials;
        this.context = context;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tutorial, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        Tutorial tutorial = tutorials.get(position);
        holder.title.setText(tutorial.getTitle());

        // Configurar el WebView para reproducir el video
        String videoHtml = "<html><body>" +
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + getYouTubeId(tutorial.getVideoUrl()) + "\" frameborder=\"0\" allowfullscreen></iframe>" +
                "</body></html>";
        holder.webView.getSettings().setJavaScriptEnabled(true);
        holder.webView.loadData(videoHtml, "text/html", "utf-8");
    }

    @Override
    public int getItemCount() {
        return tutorials.size();
    }

    public static class TutorialViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        WebView webView;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            webView = itemView.findViewById(R.id.webView);
        }
    }

    // Método para extraer el ID del video de YouTube
    private String getYouTubeId(String youtubeUrl) {
        String videoId = "";
        if (youtubeUrl != null && youtubeUrl.contains("v=")) {
            int start = youtubeUrl.indexOf("v=") + 2;
            int end = youtubeUrl.indexOf("&", start);
            videoId = (end == -1) ? youtubeUrl.substring(start) : youtubeUrl.substring(start, end);
        }
        return videoId;
    }
}
