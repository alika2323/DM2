package com.example.nallely.dm2;

import android.app.DatePickerDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    /*PARA IMEI*/
    TelephonyManager imei;
    TextView txt_imei;
    CharSequence imei_dato;


    /* PARA TOMAR FOTO*/
    FloatingActionButton btn_foto;
    private final String CARPETA_RAIZ = "misimagenesPrueba/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "misFotos";
    final int COD_FOTO = 20;
    String path;
    String nombreImagen="";
    ImageView imagen;

    private String resultado;
    private AlertDialog alertDialog;


    /*recibir datos*/
    TextView adicionales;
    String clave="";


    /*fecha*/
    Button btn_fecha;
    EditText fecha;
    String fecha_nacimiento;
    private int diaa,mess,anoo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /*  OBTENER IMEI */
        obtener_imei();

        /*  TOMAR FOTOGRAFIA USUARIO */
        tomar_foto();


        /* OBTENER CODIGO PROYECTO*/
        adicionales=(TextView)findViewById(R.id.datos_adic);
        Bundle miBundle=this.getIntent().getExtras();
        if (miBundle != null) {
           clave=miBundle.getString("codigo_proyecto");
            adicionales.setText(clave);

        }


        /* OBTENER FECHA NACIMIENTO*/
        btn_fecha=(Button)findViewById(R.id.btn_fecha);
        fecha=(EditText)findViewById(R.id.fecha);

   btn_fecha.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           DatePickerDialog datePickerDialog= new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
               @Override
               public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                   final Calendar c=Calendar.getInstance();
                   diaa=c.get(Calendar.DAY_OF_MONTH);
                   mess=c.get(Calendar.MONTH);
                   anoo=c.get(Calendar.YEAR);
                   fecha_nacimiento=dayOfMonth+"/"+(month+1)+"/"+year;
                   fecha.setText(fecha_nacimiento);

               }
           }
                   ,diaa,mess,anoo);
           datePickerDialog.show();
       }
   });

        /*      REGISTRO DE DATOS      */
        ((Button)findViewById(R.id.registrar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             String  d_nombre=((EditText)findViewById(R.id.nombre)).getText().toString();


                /* nombre,
                 * apellido_paterno
                 * apellido_materno
                 * telefono_casa
                 * telefono_celular
                 * email
                 * contraseña
                 * confirmar_contraseña
                 * perfil:default
                 * rol:dm
                 * nombreImagen
                  * */


                if (!TextUtils.isEmpty(d_nombre)){

                    ArrayList values=new ArrayList();
                    final JSONArray data;
                /*DATOS*/
                    values.add(d_nombre);
                    values.add(((EditText)findViewById(R.id.apaterno)).getText());
                    values.add(((EditText)findViewById(R.id.amaterno)).getText());
                    values.add(((EditText)findViewById(R.id.tel_contacto)).getText());
                    values.add(((EditText)findViewById(R.id.tel_trabajo)).getText());
                    values.add(nombreImagen);
                    values.add("0");/*latitud*/
                    values.add("0");/*longitud*/
                    values.add("mexico"); /*estado*/
                    values.add("ecatepec"); /*municipio*/
                    values.add("1");/*tipo usuario*/
                    values.add("1");/*perfil*/
                    values.add(((EditText)findViewById(R.id.email)).getText());
                    values.add("12345");
                    values.add(imei_dato);
                    values.add(clave);/*CLAVEPROYECTO*/
                    values.add(fecha_nacimiento);/*CURP*/
                    values.add("1");/*validacion*/



                    data=new JSONArray( values );
                    System.out.println("primero");

                    alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("STATUS REGISTRO");
                    alertDialog.setMessage("Procesando el registro....");
                    //SystemClock.sleep(1000)
                    alertDialog.show();






                    Thread tr=new Thread(){
                        @Override
                        public void run() {
                            POST(path,"Users","putUser",data.toString());
                            System.out.println("resultado"+resultado);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // System.out.println(resultado);
                                }
                            });
        }
    };

    tr.start();

    //POST("INSERTAR",data.toString());


}else{
    ((EditText)findViewById(R.id.nombre)).setError("ERROR NOMBRE");
    ((EditText)findViewById(R.id.nombre)).requestFocus();

    Toast.makeText(MainActivity.this, "NO HAS INGRESADO LOS DATOS", Toast.LENGTH_LONG).show();

}
            }
        });






    }



    /* C- OBTENER IMEI */
    private void obtener_imei() {
        txt_imei = (TextView) findViewById(R.id.txt_imei);
        imei = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final StringBuilder builder = new StringBuilder();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        builder.append("IMEI:").append(imei.getDeviceId()).append("\n");

        txt_imei.setText(builder.toString());

        //imei_dato = imei.getDeviceId();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }else{
            imei_dato=imei.getDeviceId();
        }
    }


    /*   C-TOMAR FOTO */
    public void tomar_foto(){




        btn_foto=(FloatingActionButton) findViewById(R.id.btn_foto);
        btn_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
                Boolean iscreada=fileImagen.exists();

                if (iscreada==false){
                    iscreada=fileImagen.mkdirs();
                }

                if (iscreada==true){
                    nombreImagen=(System.currentTimeMillis()/1000)+".jpg";

                }
                path=Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImagen;
                File imagen=new File(path);
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
                startActivityForResult(intent,COD_FOTO);

            }
        });
    }




    /*  D-MOSTRAR IMAGEN EN IMAGEN VIEW*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("RUTA DE ALMACENAMIENTO","PATH:"+path);
                }
            });

            imagen=(ImageView)findViewById(R.id.foto);
            Bitmap bitmap=BitmapFactory.decodeFile(path);
            System.out.println(bitmap.toString());
            imagen.setImageBitmap(bitmap);

        }
    }



    /*  C-ENVIAR DATOS  */
    public void POST( final String filename, final String opcion, final String action, final String values ) {
        System.out.println("aqui estoy");
        final String boundary = "***";
        parameters parameters=new parameters();
        final String url = parameters.getUrlPOST();

        try {

            HttpClient send = new DefaultHttpClient();
            HttpPost post = new HttpPost( url );
            final String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            MultipartUploadRequest request = new MultipartUploadRequest( getApplicationContext(), uploadId, url );
            request.addHeader( "Content-Type", "multipart/form-data; boundary=" + boundary );
            request.addFileToUpload( filename, "imagen" );
            request.addParameter( "opcion", opcion); //Adding text parameter to the request
            request.addParameter( "action", action );
            request.addParameter( "values", values );
            request.addParameter( "csrf_token","token" );
            request.setNotificationConfig( new UploadNotificationConfig() );
            request.setMaxRetries( 2 );
            request.setDelegate( new UploadStatusDelegate() {


                @Override
                public void onProgress(UploadInfo uploadInfo) {
                    System.out.println( "UPLOADINFO-> " + uploadInfo.getProgressPercent() );
                }

                @Override
                public void onError(UploadInfo uploadInfo, Exception exception) {
                    Log.d( "On error", String.valueOf( exception ) );
                    Toast.makeText( getApplicationContext(), "ERROR", Toast.LENGTH_LONG ).show();
                }

                @Override
                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {

                    System.out.println( "respuesta-> " + serverResponse.getHttpCode());

                    resultado =serverResponse.getBodyAsString().toString() ;

                    System.out.println("resultado1"+resultado);

                    try {


                        JSONObject res=new JSONObject(resultado);
                        System.out.println("res:"+res);
                        resultado =res.getString("DATOS");
                        System.out.println("resultado2"+resultado);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(resultado.equals("true")){



                        alertDialog.dismiss();
                        ((EditText)findViewById(R.id.nombre)).setText("");
                        ((EditText)findViewById(R.id.apaterno)).setText("");
                        ((EditText)findViewById(R.id.amaterno)).setText("");
                        ((EditText)findViewById(R.id.tel_contacto)).setText("");
                        ((EditText)findViewById(R.id.tel_trabajo)).setText("");
                        recreate();
                    }else {

                        alertDialog.dismiss();
                        Toast.makeText(MainActivity.this, resultado, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(UploadInfo uploadInfo) {

                }
            } );
            request.startUpload(); //Starting the upload

        } catch (Exception exc) {
            System.out.println( exc.getMessage() );
            Toast.makeText( getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT ).show();
        }
    }





}
