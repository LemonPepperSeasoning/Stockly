//package com.larkspur.stockly.Activities.Search;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.firebase.database.collection.LLRBNode;
//import com.larkspur.stockly.R;
//import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * This class is a method for search implementation.
// * Source: http://www.codeplayon.com/2019/07/android-searchable-spinner-example/
// */
//
//public class SearchActivity extends AppCompatActivity {
//
//    private Spinner PartSpinner;
//    List<String> PartName;
//    List<String> PartId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        PartSpinner=(Spinner)findViewById(R.id.spinnerPart);
//        PartName=new ArrayList<>();
//        PartId=new ArrayList<>();
//    }
//
//    private void PartList() {
//        final ProgressDialog loading = new ProgressDialog(CompleteCondition.this);
//        loading.setMessage("Please Wait...");
//        loading.show();
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfiURL.Parts_List_URl,
//                new Response.Listener<String>() {
//
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            Log.d("JSON", response);
//                            loading.dismiss();
//                            JSONObject eventObject = new JSONObject(response);
//                            String error_status=eventObject.getString("error");
//                            if(error_status.equals("true")){
//                                String error_msg=eventObject.getString("msg");
//                                Toast.makeText(CompleteCondition.this, error_msg, Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                Resouces(eventObject);
//                            }
//                        } catch (Exception e) {
//                            Log.d("Tag",e.getMessage());
//
//                        }
//                    }
//                },
//
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        loading.dismiss();
//                        Toast.makeText(Compleprivate Spinner PartSpinner;
//                        List<String> PartName;
//                        List<String> PartId;
//
//                        @Override
//                        protected void onCreate(Bundle savedInstanceState) {
//                            super.onCreate(savedInstanceState);
//                            setContentView(R.layout.activity_main);
//
//                            PartSpinner=(Spinner)findViewById(R.id.spinnerPart);
//                            PartName=new ArrayList<>();
//                            PartId=new ArrayList<>();
//                        }
//
//                        private void PartList() {
//                            final ProgressDialog loading = new ProgressDialog(CompleteCondition.this);
//                            loading.setMessage("Please Wait...");
//                            loading.show();
//                            StringRequest stringRequest = new StringRequest(Request.Method.POST, ConfiURL.Parts_List_URl,
//                                    new Response.Listener<String>() {
//
//                                        @Override
//                                        public void onResponse(String response) {
//                                            try {
//                                                Log.d("JSON", response);
//                                                loading.dismiss();
//                                                JSONObject eventObject = new JSONObject(response);
//                                                String error_status=eventObject.getString("error");
//                                                if(error_status.equals("true")){
//                                                    String error_msg=eventObject.getString("msg");
//                                                    Toast.makeText(CompleteCondition.this, error_msg, Toast.LENGTH_SHORT).show();
//                                                }
//                                                else{
//                                                    Resouces(eventObject);
//                                                }
//                                            } catch (Exception e) {
//                                                Log.d("Tag",e.getMessage());
//
//                                            }
//                                        }
//                                    },
//
//                                    new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            loading.dismiss();
//                                            Toast.makeText(CompleteCondition.this,error.toString(), Toast.LENGTH_LONG ).show();
//
//                                        }
//                                    }){
//                                @Override
//                                protected Map<String, String> getParams() throws AuthFailureError {
//                                    Map<String,String> map = new HashMap<String,String>();
//                                    return map;
//                                }
//                            };
//
//                            RequestQueue requestQueue = Volley.newRequestQueue(this);
//                            requestQueue.add(stringRequest);
//                        }
//                        /*hare to pares the json response in list and show to spener*/
//                        public void Resouces(JSONObject Ward) {
//                            String Resourc_Name;
//                            String ResourceID;
//                            PartName.clear();
//                            PartId.clear();
//                            PartName.add("Select Part");
//                            PartId.add("Select Part");
//                            try {
//                                JSONArray projectNameArray = Ward.getJSONArray("Available Parts");
//                                for (int i = 0; i <= projectNameArray.length(); i++) {
//                                    JSONObject obj = projectNameArray.getJSONObject(i);
//                                    Resourc_Name = obj.getString("part_name");
//                                    ResourceID = obj.getString("part_id");
//                                    Log.d("name", Resourc_Name);
//                                    PartName.add(Resourc_Name);
//                                    PartId.add(ResourceID);
//
//                                    //Log.d("Dropdown",Dropdown.get(i));
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, PartName);
//                            PartSpinner.setAdapter(adapter);
//                            PartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//                                @Override
//                                public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
//                                    // On selecting a spinner item
//                                    ((TextView) adapter.getChildAt(0)).setTextColor(LLRBNode.Color.BLACK);
//                                    Part_Id = PartId.get(position);
//
//                                }
//
//                                @Override
//                                public void onNothingSelected(AdapterView<?> arg0) {
//                                    // TODO Auto-generated method stub
//                                }
//                            });
//                        }
//                    }teCondition.this,error.toString(), Toast.LENGTH_LONG ).show();
//
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> map = new HashMap<String,String>();
//                return map;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }
//    /*hare to pares the json response in list and show to spener*/
//    public void Resouces(JSONObject Ward) {
//        String Resourc_Name;
//        String ResourceID;
//        PartName.clear();
//        PartId.clear();
//        PartName.add("Select Part");
//        PartId.add("Select Part");
//        try {
//            JSONArray projectNameArray = Ward.getJSONArray("Available Parts");
//            for (int i = 0; i <= projectNameArray.length(); i++) {
//                JSONObject obj = projectNameArray.getJSONObject(i);
//                Resourc_Name = obj.getString("part_name");
//                ResourceID = obj.getString("part_id");
//                Log.d("name", Resourc_Name);
//                PartName.add(Resourc_Name);
//                PartId.add(ResourceID);
//
//                //Log.d("Dropdown",Dropdown.get(i));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, PartName);
//        PartSpinner.setAdapter(adapter);
//        PartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
//                // On selecting a spinner item
//                ((TextView) adapter.getChildAt(0)).setTextColor(LLRBNode.Color.BLACK);
//                Part_Id = PartId.get(position);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//                // TODO Auto-generated method stub
//            }
//        });
//    }
//}
//}
