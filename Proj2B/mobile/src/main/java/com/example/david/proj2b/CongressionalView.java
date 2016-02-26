package com.example.david.proj2b;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.util.List;
import java.util.ArrayList;
import android.content.Context;

public class CongressionalView extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";

//    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
//        Intent i = new Intent(packageContext, CongressionalView.class);
//        i.putExtra(ExtraAnswer, answerIsTrue);
//        return i;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional_view);

        //why does this return null?
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

    }

    private List<Candidate> candidates;

    //private void initializeDate() {
    private List<Candidate> getDataSet() {
        candidates = new ArrayList<>();
        candidates.add(new Candidate("Kevin McCarthy", "Republican", "kmccarthy@gmail.com", "www.mccarthy.com", "First Tweet", R.drawable.kevin_mccarthy));
        candidates.add(new Candidate("Barbara Boxer", "Democrat", "bboxer@gmail.com", "www.boxer.com", "2nd tweet", R.drawable.senator_barbara_boxer));
        candidates.add(new Candidate("Dianne Feinstein", "Democrat", "dfeinstein@gmail.com", "www.feinstein.com", "3rd tweet", R.drawable.senator_dianne_feinstein));

        return candidates;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyAdapter) mAdapter).setOnItemClickListener(new MyAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }



}
