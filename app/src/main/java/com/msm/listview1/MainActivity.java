package com.msm.listview1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<HashMap<String,Object>> myDataset=new ArrayList<>();
    private Map<String,Object> mydataset1=new HashMap<>();
    private FirebaseFirestore lvfirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvfirestore=FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recyclelist);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        lvfirestore.collection("USER").orderBy("createdDate",Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayList<Map<String,Object>> list = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    list.add(document.getData());
                    Log.e("abc", document.getData().get("createdDate").toString());
                }
                mAdapter=new MyAdapter(list);
                recyclerView.setAdapter(mAdapter);
            }
        });

        lvfirestore.collection("USER").orderBy("createdDate",Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Map<String,Object>> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getData());
                        Log.e("abc", document.getData().get("createdDate").toString());
                    }
                    mAdapter=new MyAdapter(list);
                    recyclerView.setAdapter(mAdapter);
                }
            }
        });

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView nameTextView;
            public TextView ageTextView;
            public ShimmerLayout shimmerLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTextView = (TextView) itemView.findViewById(R.id.nametext);
                ageTextView = (TextView) itemView.findViewById(R.id.agetext);
            }
        }

        private ArrayList<Map<String,Object>> ulist;

        public MyAdapter(ArrayList<Map<String,Object>> userlist) {
            ulist=userlist;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.listitem, parent, false);
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int position) {
            TextView nametext=viewHolder.nameTextView;
            TextView agetext=viewHolder.ageTextView;
            nametext.setText("Name: "+ulist.get(position).get("Name").toString());
            agetext.setText("Age: "+ulist.get(position).get("Age").toString());
        }
        @Override
        public int getItemCount() {
            return ulist.size();
        }
    }
}
