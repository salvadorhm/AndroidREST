package salvadorhm.blogspot.com.androidrest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.AlertDialog;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    private TextView tv_id_producto;
    private TextView tv_producto;
    private TextView tv_existencias;
    private TextView tv_precio_compra;
    private TextView tv_precio_venta;
    private TextView tv_descripcion;
    private EditText et_id_producto;
    private Button btn_search;

    private String getContactURL = "https://acmestore.herokuapp.com/json?user_hash=12345&id_producto=";
    //private String getContactURL = "http://10.42.0.63:8080/json?user_hash=12345&id_producto=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tv_id_producto = (TextView) findViewById(R.id.tv_id_producto);
        tv_producto = (TextView) findViewById(R.id.tv_producto);
        tv_existencias = (TextView) findViewById(R.id.tv_existencias);
        tv_descripcion = (TextView) findViewById(R.id.tv_descripcion);
        tv_precio_compra = (TextView) findViewById(R.id.tv_precio_compra);
        tv_precio_venta = (TextView) findViewById(R.id.tv_precio_venta);
        et_id_producto = (EditText) findViewById(R.id.et_id_producto);
        btn_search = (Button) findViewById(R.id.btn_search);

        btn_search.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_search)
                btn_search_onClick();
        }
    };

    private void btn_search_onClick(){
        String id_producto= et_id_producto.getText().toString();

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("id_producto", id_producto);
        Log.i("Search",id_producto);
        String queryParams = builder.build().getEncodedQuery();

        //performPostCall(getContactURL, queryParams);
        performPostCall(getContactURL, id_producto);

        Log.i("Search",getContactURL);
    }

    private void performPostCall(String requestURL, String query){
        URL url;
        String webServiceResult="";
        try{
            url = new URL(requestURL+query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            Log.i("Search",url.toString());
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                while ((line = bufferedReader.readLine()) != null){
                    webServiceResult += line;
                    Log.i("Search",line);
                }
                bufferedReader.close();
            }else {
                webServiceResult= null;
            }
            Log.i("webservice", webServiceResult);
        }catch (Exception e){
            e.printStackTrace();
            Log.i("SearchActivity",e.getMessage());
        }

        if(webServiceResult.equals("{}"))
            Message("Search","Product not found");
        else
            parseInformation(webServiceResult);

    }

    private void parseInformation(String jsonResult){
        JSONArray jsonArray = null;
        String id_producto;
        String producto;
        String existencias;
        String descripcion;
        String precio_compra;
        String precio_venta;

        try{
            if(jsonResult.equals("{}"))
                Message("Search","Product not found");
            else
                jsonArray = new JSONArray(jsonResult);
        }catch (JSONException e){
            e.printStackTrace();
        }
        try{
            if(jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    id_producto = jsonObject.getString("id_producto");
                    producto = jsonObject.getString("producto");
                    existencias = jsonObject.getString("existencias");
                    descripcion = jsonObject.getString("descripcion");
                    precio_compra = jsonObject.getString("precio_compra");
                    precio_venta = jsonObject.getString("precio_venta");

                    tv_producto.setText(producto);
                    tv_descripcion.setText(descripcion);
                    tv_existencias.setText(existencias);
                    tv_precio_compra.setText(precio_compra);
                    tv_precio_venta.setText(precio_venta);

                    Log.i("Search", id_producto);
                    Log.i("Search", producto);
                    Log.i("Search", descripcion);
                    Log.i("Search", existencias);
                    Log.i("Search", precio_compra);
                    Log.i("Search", precio_venta);
                }
            }
            else
                Message("Search","Product not found");
            }catch (JSONException e){
                e.printStackTrace();

            }
    }
    private void Message(String title, String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}
