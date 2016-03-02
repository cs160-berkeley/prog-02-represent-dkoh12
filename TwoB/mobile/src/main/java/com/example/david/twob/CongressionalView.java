package com.example.david.twob;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CongressionalView extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidate_list);

        Intent intent = getIntent();
        String zipCode = intent.getStringExtra("zipCode");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<Candidate> getDataSet() {
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate("Kevin McCarthy", "Republican", "kmccarthy@gmail.com", "https://kevinmccarthy.house.gov", "First Tweet", R.drawable.kevin_mccarthy));
        candidates.add(new Candidate("Barbara Boxer", "Democrat", "bboxer@gmail.com", "www.boxer.senate.gov", "2nd tweet", R.drawable.senator_barbara_boxer));
        candidates.add(new Candidate("Dianne Feinstein", "Democrat", "dfeinstein@gmail.com", "www.feinstein.senate.gov", "3rd tweet", R.drawable.senator_dianne_feinstein));

        return candidates;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MyAdapter) mAdapter).setOnItemClickListener(new MyAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //Log.i(LOG_TAG, " Clicked on Item " + position);
                Intent i = new Intent(CongressionalView.this, DetailedView.class);

                List<Candidate> newList = getDataSet();

                i.putExtra("photo", newList.get(position).getPhotoId());
                i.putExtra("name", newList.get(position).getName());
                i.putExtra("party", newList.get(position).getParty());

                startActivity(i);
            }
        });
    }

}
