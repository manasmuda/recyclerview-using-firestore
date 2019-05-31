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
    private DocumentSnapshot userdatasnap;
    private LoaderTextView loaderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvfirestore=FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recyclelist);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        lvfirestore.collection("USER").orderBy("createdDate").addSnapshotListener(new EventListener<QuerySnapshot>() {
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



        lvfirestore.collection("USER").document().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userdatasnap=documentSnapshot;
                mydataset1=userdatasnap.getData();
            }
        });

        // specify an adapter (see also next example)
        //mAdapter = new MyAdapter(myDataset);
        //recyclerView.setAdapter(mAdapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView nameTextView;
            public TextView ageTextView;
            public ShimmerLayout shimmerLayout;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);
                nameTextView = (TextView) itemView.findViewById(R.id.nametext);
                ageTextView = (TextView) itemView.findViewById(R.id.agetext);
            }
        }

        // Store a member variable for the contacts
        private ArrayList<Map<String,Object>> ulist;

        // Pass in the contact array into the constructor
        public MyAdapter(ArrayList<Map<String,Object>> userlist) {
            ulist=userlist;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.listitem, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
        }

        // Involves populating data into the item through holder
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int position) {
            // Get the data model based on position
            TextView nametext=viewHolder.nameTextView;
            TextView agetext=viewHolder.ageTextView;
            nametext.setText("Name: "+ulist.get(position).get("Name").toString());
            agetext.setText("Age: "+ulist.get(position).get("Age").toString());
        }

        // Returns the total count of items in the list
        @Override
        public int getItemCount() {
            return ulist.size();
        }
    }
}
