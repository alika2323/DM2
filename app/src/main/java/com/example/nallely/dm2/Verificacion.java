package com.example.nallely.dm2;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Verificacion extends AppCompatActivity {
    EditText codigo_verificar;
    private AlertDialog alertDialog;
    private String resultado;
    Button btn_verificar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacion);


        codigo_verificar=(EditText)findViewById(R.id.codigo_verifica);
        btn_verificar=(Button)findViewById(R.id.btn_verificar);


        btn_verificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificar(codigo_verificar.getText().toString());
            }
        });


    }



    public void verificar(final String codigo_verificar) {


        Thread tr=new Thread(){
            @Override
            public void run() {

                final String resultado= POST("Validate","tokenProject", codigo_verificar );
                System.out.println("resultado"+resultado);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject res=new JSONObject(resultado);

                             Boolean valor= Boolean.valueOf(res.getString("CODIGO"));
                            System.out.println(valor);

                            if (valor){
                                Intent intent=new Intent(Verificacion.this, MainActivity.class);
                                Bundle miBundle=new Bundle();
                                miBundle.putString("codigo_proyecto",codigo_verificar);
                                intent.putExtras(miBundle);
                                startActivity(intent);


                            }else{
                                String valor_error= res.getString("DATOS");
                                Toast.makeText(Verificacion.this,valor_error, Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };

        tr.start();


    }


    public String POST(String opcion, String action, String claveproyecto){
        parameters parameters=new parameters();
        String resultPOST="";
        try{
            HttpClient send=new DefaultHttpClient();
            HttpPost post=new HttpPost(parameters.getUrlPOST());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token","token_proyecto"));
            params.add(new BasicNameValuePair("opcion",opcion));
            params.add(new BasicNameValuePair("action",action));
            params.add(new BasicNameValuePair("value",claveproyecto));
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp= send.execute(post);
            HttpEntity ent=resp.getEntity();
            resultPOST= EntityUtils.toString(ent);
        }catch (Exception e){}

        return resultPOST;
    }


}
