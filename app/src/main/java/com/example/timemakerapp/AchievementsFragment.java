package com.example.timemakerapp;


import android.content.Context;
import android.os.Bundle;

import android.text.Layout;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.timemakerapp.R.layout.achievements_row;


/**
 * A simple {@link Fragment} subclass.
 */
public class AchievementsFragment extends Fragment {

    ListView listview;
    Context mC;
    String mTitle[] = {"First Use", "3 Days in a Row", "Completed 10 Goals"};
    String mDescription[] = {"Complete your first goal", "....bar...", "...bar..."};
    int images [] = {R.drawable.achievements_firstprice,R.drawable.achievements_firstprice,R.drawable.achievements_firstprice};


    public AchievementsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View fragView = inflater.inflate(R.layout.fragment_achievements, container, false);

        listview =(ListView) fragView.findViewById(R.id.listview);
        MyAdapter adapter = new MyAdapter(getActivity(), mTitle, mDescription, images);
        listview.setAdapter(adapter);
        return fragView;
    }

    class MyAdapter extends ArrayAdapter<String>{

        private LayoutInflater mInflater;
        private Context context;
        private String rTitle[];
        private String rDescription[];
        private int rImgs[];

        MyAdapter(Context c, String title[], String description[], int imgs[])
        {
            super(c, R.layout.achievements_row, R.id.textview1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = mInflater.inflate(achievements_row, null);
            ImageView images = convertView.findViewById(R.id.image1);
            TextView myTitle = convertView.findViewById(R.id.textview1);
            TextView myDescription = convertView.findViewById(R.id.textview2);

            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);

            return convertView;
        }
    }

}
