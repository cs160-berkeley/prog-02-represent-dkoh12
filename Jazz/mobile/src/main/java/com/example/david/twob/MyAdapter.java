package com.example.david.twob;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * Created by david on 2/25/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CandidateViewHolder> {
    private static String LOG_TAG = "MyAdapter";
    private List<Candidate> candidates;
    private static MyClickListener sMyClickListener;

    public static class CandidateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView c;
        TextView candidateName;
        TextView candidateParty;
        TextView candidateEmail;
        TextView candidateWebsite;
        TextView candidateTweet;
        ImageView candidatePhoto;

        CandidateViewHolder(View itemView) {
            super(itemView);
            c = (CardView) itemView.findViewById(R.id.card_view);
            candidateName = (TextView) itemView.findViewById(R.id.name);
            candidateParty = (TextView) itemView.findViewById(R.id.party);
            candidateEmail = (TextView) itemView.findViewById(R.id.email);
            candidateWebsite = (TextView) itemView.findViewById(R.id.website);
            candidateTweet = (TextView) itemView.findViewById(R.id.tweet);
            candidatePhoto = (ImageView) itemView.findViewById(R.id.photo);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this); //necessary?
        }

        @Override
        public void onClick(View v) {
            sMyClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener){
        this.sMyClickListener = myClickListener;
    }

    public MyAdapter(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    @Override
    public CandidateViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        CandidateViewHolder cvh = new CandidateViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(CandidateViewHolder canViewHolder, int i) {
        canViewHolder.candidateName.setText(candidates.get(i).mName);
        canViewHolder.candidateParty.setText(candidates.get(i).mParty);
        canViewHolder.candidateEmail.setText(candidates.get(i).mEmail);
        canViewHolder.candidateWebsite.setText(candidates.get(i).mWebsite);
        canViewHolder.candidateTweet.setText(candidates.get(i).mTweet);
        canViewHolder.candidatePhoto.setImageResource(candidates.get(i).mPhotoId);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
