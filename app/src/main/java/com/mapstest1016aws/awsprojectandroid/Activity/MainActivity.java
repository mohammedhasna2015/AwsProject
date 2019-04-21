package com.mapstest1016aws.awsprojectandroid.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateDiaryMutation;
import com.amazonaws.amplify.generated.graphql.DeleteDiaryMutation;
import com.amazonaws.amplify.generated.graphql.ListDiarysQuery;
import com.amazonaws.amplify.generated.graphql.UpdateDiaryMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;

import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.mapstest1016aws.awsprojectandroid.Adabter.CustomAdapter;
import com.mapstest1016aws.awsprojectandroid.Network.ClientFactory;
import com.mapstest1016aws.awsprojectandroid.R;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import type.CreateDiaryInput;
import type.DeleteDiaryInput;
import type.UpdateDiaryInput;

import static com.mapstest1016aws.awsprojectandroid.Utils.Appcontroll.sharedpreferences;

public class MainActivity extends AppCompatActivity implements CustomAdapter.OnItemSelectedListener{

    RecyclerView mRecyclerView;
    FloatingActionButton btnAddPet;
    private ArrayList<ListDiarysQuery.Item> mPets;
    private Dialog activationDialogUpdate;
    Button butt_update_Diary_dailog,butt_cancle_update_Diary_dailog ;
    SweetAlertDialog pDialog;
    String iduser;
    private final String TAG = MainActivity.class.getSimpleName();

     Button siginout ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          ButterKnife.bind(this);
        ClientFactory.init(this);
        iduser = sharedpreferences.getString("userid","");
       /* siginout=(Button)findViewById(R.id.siginout);
        siginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AWSMobileClient.getInstance().signOut();
              showSignIn();
            }
        });*/
        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        btnAddPet= (FloatingActionButton)findViewById(R.id.btn_addPet);
        btnAddPet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent addPetIntent = new Intent(MainActivity.this, AddDiaryActivity.class);
                addPetIntent.putExtra("iduser",iduser);
                MainActivity.this.startActivity(addPetIntent);
            }
        });
    }
    private GraphQLCall.Callback<ListDiarysQuery.Data> queryCallback = new GraphQLCall.Callback<ListDiarysQuery.Data>() {
        @Override
        public void onResponse(@Nonnull final Response<ListDiarysQuery.Data> response) {

            mPets = new ArrayList<>(response.data().listDiarys().items());

            Log.i(TAG, "Retrieved list items: " + mPets.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPets = new ArrayList<>(response.data().listDiarys().items());
                            Log.i(TAG, "Retrieved list items: " + mPets.toString());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                    CustomAdapter customAdapter = new CustomAdapter(mPets, MainActivity.this, true);
                                    mRecyclerView.setAdapter(customAdapter);
                                    customAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            });
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
        }
    };
    public void query2(){
        ClientFactory.appSyncClient().query(ListDiarysQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(queryCallback);
    }

    private void showSignIn() {
        try {

            AWSMobileClient.getInstance().showSignIn(this,
                    SignInUIOptions.builder().nextActivity(MainActivity.class).build());

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // Query list data when we return to the screen
        query2();
       setupObjectList();
    }
    @Override
    public void onSelected(ListDiarysQuery.Item object) {
    }
    @Override
    public void onMenuAction(ListDiarysQuery.Item  object, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_custom_edit:
               showUpdateLayout(object);
                break;
            case R.id.menu_custom_delete:
                mPets.remove(object);
                deleteDiary(object);
               setupObjectList();
                break;
        }
    }

    private void deleteDiary(ListDiarysQuery.Item object) {

        DeleteDiaryInput input = DeleteDiaryInput
                .builder()
                .id(object.id())
                .build();

        DeleteDiaryMutation deleteDiaryMutation = DeleteDiaryMutation.builder()
                .input(input)
                .build();
        ClientFactory.appSyncClient().mutate(deleteDiaryMutation).enqueue(new GraphQLCall.Callback<DeleteDiaryMutation.Data>(){
            @Override
            public void onResponse( Response<DeleteDiaryMutation.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Deleted Diary", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent( MainActivity.this,MainActivity.class));

                    }
                });
            }
            @Override
            public void onFailure( ApolloException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("", "Failed to perform Delete DiaryMutation");
                        Toast.makeText(MainActivity.this, "Failed to add Diary", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent( MainActivity.this,MainActivity.class));

                    }
                });
            }
        });

    }

    private void showUpdateLayout(final ListDiarysQuery.Item  object) {
        activationDialogUpdate = new Dialog(MainActivity.this, R.style.Theme_Dialog);
        activationDialogUpdate.setContentView(R.layout.update_diary_dailog);
        activationDialogUpdate.show();
        final EditText edt_description_update_dailog = (EditText) activationDialogUpdate.findViewById(R.id.edt_description_update_dailog);
        final EditText edt_name_diary_update_dailog = (EditText) activationDialogUpdate.findViewById(R.id.edt_name_diary_update_dailog);

          edt_description_update_dailog.setText(object.title());
          edt_name_diary_update_dailog.setText(object.desc());


        butt_update_Diary_dailog = (Button) activationDialogUpdate.findViewById(R.id.butt_update_Diary_dailog);
        butt_cancle_update_Diary_dailog = (Button) activationDialogUpdate.findViewById(R.id.butt_cancle_update_Diary_dailog);
        butt_update_Diary_dailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_description_update_dailog.getText().toString().matches("")&&edt_name_diary_update_dailog.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "Some Field Empty ... ", Toast.LENGTH_SHORT).show();

                } else {
                    pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#78B844"));
                    pDialog.setTitleText("Update Dairy....");
                    pDialog.setCancelable(false);
                    pDialog.show();


                    UpdateDiaryInput input = UpdateDiaryInput
                            .builder()
                            .desc(edt_description_update_dailog.getText().toString())
                            .title(edt_name_diary_update_dailog.getText().toString())
                            .id(object.id())
                            .userId(iduser)
                            .build();

                    UpdateDiaryMutation updateDiaryMutation = UpdateDiaryMutation.builder()
                            .input(input)
                            .build();
                    ClientFactory.appSyncClient().mutate(updateDiaryMutation).enqueue(new GraphQLCall.Callback<UpdateDiaryMutation.Data>(){
                        @Override
                        public void onResponse( Response<UpdateDiaryMutation.Data> response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "updated Diary done ", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent( MainActivity.this,MainActivity.class));

                                }
                            });

                        }
                        @Override
                        public void onFailure( ApolloException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    Log.e("", "Failed to perform update DiaryMutation");
                                    Toast.makeText(MainActivity.this, "Failed to update Diary", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent( MainActivity.this,MainActivity.class));

                                }
                            });
                        }
                    });



                }
            }
        });

        butt_cancle_update_Diary_dailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activationDialogUpdate.dismiss();
            }
        });

    }

    private void setupObjectList() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomAdapter customAdapter = new CustomAdapter(mPets, this, true);
        mRecyclerView.setAdapter(customAdapter);
    }


}