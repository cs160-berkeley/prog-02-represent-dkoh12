package com.example.david.twob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class CongressionalView extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String zipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidate_list);

        Intent intent = getIntent();
        zipCode = intent.getStringExtra("zipCode");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(getDataSet(zipCode));
        mRecyclerView.setAdapter(mAdapter);
    }

    //from zip code get data
    public ArrayList<Candidate> getDataSet(String zipCode) {
        ArrayList<Candidate> candidates = new ArrayList<>();
        if(zipCode.equals("94704")) {
            candidates.add(new Candidate("Kevin McCarthy", "Republican", "kmccarthy@gmail.com", "https://kevinmccarthy.house.gov", "First Tweet", R.drawable.kevin_mccarthy));
            candidates.add(new Candidate("Barbara Boxer", "Democrat", "bboxer@gmail.com", "www.boxer.senate.gov", "2nd tweet", R.drawable.senator_barbara_boxer));
            candidates.add(new Candidate("Dianne Feinstein", "Democrat", "dfeinstein@gmail.com", "www.feinstein.senate.gov", "3rd tweet", R.drawable.senator_dianne_feinstein));
        } else { //80001
            candidates.add(new Candidate("Cory Gardner", "Republican", "cgardner@gmail.com", "www.gardner.senate.gov", "#Hello!", R.drawable.kevin_mccarthy));
            candidates.add(new Candidate("Michael Bennet", "Democrat", "mbennet@gmail.com", "www.bennet.senate.gov", "#Goodbye!", R.drawable.kevin_mccarthy));
            candidates.add(new Candidate("Mike Coffman", "Republican", "mcoffman@gmail.com", "https://mikecoffman.house.gov", "#Yolo!", R.drawable.kevin_mccarthy));
        }

        //Collections.shuffle(list);

        return candidates;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MyAdapter) mAdapter).setOnItemClickListener(new MyAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent i = new Intent(CongressionalView.this, DetailedView.class);

                ArrayList<Candidate> newList = getDataSet(zipCode);

                i.putExtra("photo", newList.get(position).getPhotoId());
                i.putExtra("name", newList.get(position).getName());
                i.putExtra("party", newList.get(position).getParty());

                startActivity(i);
            }
        });
    }

}
