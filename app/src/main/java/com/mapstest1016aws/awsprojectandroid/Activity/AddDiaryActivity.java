package com.mapstest1016aws.awsprojectandroid.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateDiaryMutation;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.mapstest1016aws.awsprojectandroid.Network.ClientFactory;
import com.mapstest1016aws.awsprojectandroid.R;

import type.CreateDiaryInput;

import static com.mapstest1016aws.awsprojectandroid.Utils.Appcontroll.sharedpreferences;

public class AddDiaryActivity extends AppCompatActivity {
    private static final String TAG = AddDiaryActivity.class.getSimpleName();
    String iduser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        iduser = sharedpreferences.getString("userid","");

        Log.d(TAG, "onCreate:userid"+iduser);
        ClientFactory.init(this);
        Button btnAddItem = findViewById(R.id.btn_save);
        btnAddItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               save(iduser);
            }
        });

    }

    private void save(String iduser) {
        Log.d("iduser",iduser);
        final String name = ((EditText) findViewById(R.id.editTxt_name)).getText().toString();
        final String description = ((EditText) findViewById(R.id.editText_description)).getText().toString();

         CreateDiaryInput input = CreateDiaryInput
                 .builder()
                 .desc(description)
                 .title(name)
                 .userId(iduser)
                 .build();

        CreateDiaryMutation addDiaryMutation = CreateDiaryMutation.builder()
                .input(input)
                .build();
        ClientFactory.appSyncClient().mutate(addDiaryMutation).enqueue(new GraphQLCall.Callback<CreateDiaryMutation.Data>(){
            @Override
            public void onResponse( Response<CreateDiaryMutation.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddDiaryActivity.this, "Added Diary", Toast.LENGTH_SHORT).show();
                        AddDiaryActivity.this.finish();
                    }
                });

            }
            @Override
            public void onFailure( ApolloException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("", "Failed to perform AddDiaryMutation");
                        Toast.makeText(AddDiaryActivity.this, "Failed to add Diary", Toast.LENGTH_SHORT).show();
                        AddDiaryActivity.this.finish();
                    }
                });
            }
        });
    }


}
