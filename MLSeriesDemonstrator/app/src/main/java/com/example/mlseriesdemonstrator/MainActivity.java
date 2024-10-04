package com.example.mlseriesdemonstrator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mlseriesdemonstrator.object.ObjectDetectionActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // List of algorithms
        List<Algo> algoList = new ArrayList<>();
        algoList.add(new Algo(R.drawable.baseline_center_focus_strong_black_48, "Object Detection", ObjectDetectionActivity.class));

        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new AlgoAdapter(algoList));
    }

    // Method to start selected algorithm's activity
    private void startAlgoActivity(Algo algo) {
        Intent intent = new Intent(MainActivity.this, algo.activityClazz);
        intent.putExtra("name", algo.algoText);
        startActivity(intent);
    }

    // Adapter for RecyclerView
    class AlgoAdapter extends RecyclerView.Adapter<AlgoAdapter.AlgoViewHolder> {
        private final List<Algo> algoList;

        AlgoAdapter(List<Algo> algoList) {
            this.algoList = algoList;
        }

        @NonNull
        @Override
        public AlgoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_icons, parent, false);
            return new AlgoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlgoViewHolder holder, int position) {
            holder.bind(algoList.get(position));
        }

        @Override
        public int getItemCount() {
            return algoList.size();
        }

        // ViewHolder for RecyclerView items
        class AlgoViewHolder extends RecyclerView.ViewHolder {
            private final ImageView iconImageView;
            private final TextView algoTextView;

            AlgoViewHolder(@NonNull View itemView) {
                super(itemView);
                iconImageView = itemView.findViewById(R.id.iconImageView);
                algoTextView = itemView.findViewById(R.id.algoTextView);
                // Set click listener to start activity when item is clicked
                itemView.setOnClickListener(v -> startAlgoActivity(algoList.get(getAdapterPosition())));
            }

            void bind(Algo algo) {
                iconImageView.setImageResource(algo.iconResourceId);
                algoTextView.setText(algo.algoText);
            }
        }
    }

    // Algo class to represent algorithm info
    static class Algo {
        int iconResourceId;
        String algoText;
        Class<?> activityClazz;

        Algo(int iconResourceId, String algoText, Class<?> activityClazz) {
            this.iconResourceId = iconResourceId;
            this.algoText = algoText;
            this.activityClazz = activityClazz;
        }
    }
}
