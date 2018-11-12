package com.example.nallely.dm2;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TipoUsuario extends AppCompatActivity  {

    EditText codigo_usuario;
    private AlertDialog alertDialog;
    private String resultado;
    Button btn_verificar_usuario, btn_usuario_nuevo;
    String dato_codigo_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_usuario);


        /* Enlazando elementos */
        codigo_usuario=(EditText)findViewById(R.id.edt_clave_usuario);
        btn_verificar_usuario=(Button)findViewById(R.id.btn_usuario_existente);
        btn_usuario_nuevo=(Button)findViewById(R.id.btn_usuario_nuevo);

        /* Obtener dato de input */



        /* Enlazando evento: btn_verificar_usuario*/
        btn_verificar_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dato_codigo_usuario=codigo_usuario.getText().toString();
                verificar(dato_codigo_usuario);

            }
        });




        btn_usuario_nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TipoUsuario.this, Verificacion.class);
                startActivity(intent);
            }
        });


    }



    public void verificar(final String codigo_verificar) {
        Thread tr=new Thread(){
            @Override
            public void run() {

                final String resultado= POST("Validate","ClaveUser", dato_codigo_usuario);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject res=new JSONObject(resultado);
                            Boolean valor= Boolean.valueOf(res.getString("CODIGO"));

                            if (valor){
                                Intent intent=new Intent(TipoUsuario.this, MainActivity.class);
                                Bundle miBundle=new Bundle();
                                miBundle.putString("dato_codigo_usuario",dato_codigo_usuario);
                                intent.putExtras(miBundle);
                                startActivity(intent);
                            }else{
                                String valor_error= res.getString("DATOS");
                               Toasty.error(TipoUsuario.this,valor_error, Toast.LENGTH_SHORT,true).show();
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





    public String POST(String opcion, String action, String dato_codigo_usuario){
        parameters parameters=new parameters();
        String resultPOST="";
        try{
            HttpClient send=new DefaultHttpClient();
            HttpPost post=new HttpPost(parameters.getUrlPOST());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("csrf_token","token_usuario"));
            params.add(new BasicNameValuePair("opcion",opcion));
            params.add(new BasicNameValuePair("action",action));
            params.add(new BasicNameValuePair("value",dato_codigo_usuario));
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse resp= send.execute(post);
            HttpEntity ent=resp.getEntity();
            resultPOST= EntityUtils.toString(ent);
        }catch (Exception e){}


        return resultPOST;



    }


}
