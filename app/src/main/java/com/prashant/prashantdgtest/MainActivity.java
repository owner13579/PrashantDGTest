package com.prashant.prashantdgtest;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener, ActionChangePostCount{


    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private  String BASE_URL = "https://hn.algolia.com/api/v1/search_by_date?tags=story&page=";

    int counter = 0;
    int pageLoadCount = 0;
    TextView mTitle;
    LinearLayoutManager layoutManager;


    JSONArray postList;
    PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {


        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
             mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);


            mTitle.setText("15");

            setSupportActionBar(toolbar);
            mTitle.setText(toolbar.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);//every item of the RecyclerView has a fix size
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);



        postList = new JSONArray();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        postAdapter.setSelectedListener(this);


        initLoadMore();



        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                loadRecyclerViewData();
            }
        });


    }

    private void initLoadMore() {


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // super.onScrolled(recyclerView, dx, dy);
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisiblePosition == recyclerView.getChildCount()) {
                    loadRecyclerViewData();
                }
            }
        });


    }

    @Override
    public void onRefresh() {

        postList = new JSONArray();

        loadRecyclerViewData();
    }



    private void loadRecyclerViewData()
    {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);


        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+ pageLoadCount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mSwipeRefreshLayout.setRefreshing(false);

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray temp = new JSONArray(jsonObject.getString("hits"));

                            if(temp.length() > 0){
                                for (int i = 0; i < temp.length(); i++) {

                                    try {

                                        temp.getJSONObject(i).put("IS_SELECTED", false);

                                        postList.put(temp.getJSONObject(i));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }



                                }

                                postAdapter.refresh(postList);
                                postAdapter.notifyDataSetChanged();
                                pageLoadCount++;

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    @Override
    public void getIsSelected(boolean isSelected, int position) {

        try {

            counter = 0;

            for (int i = 0; i < postList.length(); i++) {

                if( postList.getJSONObject(position).getBoolean("IS_SELECTED")){
                    counter++;
                }


            }


            String countValue = String.valueOf(counter > 0? counter:"");

            mTitle.setText(getText(R.string.app_name)+ "   "  + countValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


//    private TextView getActionBarTextView() {
//        TextView titleTextView = null;
//
//        try {
//            Field f = t.getClass().getDeclaredField("mTitleTextView");
//            f.setAccessible(true);
//            titleTextView = (TextView) f.get(mToolBar);
//        } catch (NoSuchFieldException e) {
//        } catch (IllegalAccessException e) {
//        }
//        return titleTextView;
//    }

}
