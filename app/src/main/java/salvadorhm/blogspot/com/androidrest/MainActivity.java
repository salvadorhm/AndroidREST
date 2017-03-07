package salvadorhm.blogspot.com.androidrest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ListView lv_products_list;
    private TextView tv_title;
    private ArrayAdapter adapter;
    private String getAllContactsURL = "https://acmestore.herokuapp.com/json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        lv_products_list = (ListView)findViewById(R.id.lv_products_list);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        lv_products_list.setAdapter(adapter);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setOnClickListener(onClickListener);

        webServiceRest(getAllContactsURL);
        Log.i("url", getAllContactsURL);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == tv_title)
                webServiceRest(getAllContactsURL);
        }
    };

    private void webServiceRest(String requestURL){
        try{
            Log.i("recibiendo datos", requestURL);
            URL url = new URL(requestURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.i("Conectado",connection.toString());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String webServiceResult = "";
            while ((line = bufferedReader.readLine()) != null){
                Log.i("line", line);
                webServiceResult += line;
            }
            bufferedReader.close();
            parseInformation(webServiceResult);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void parseInformation(String jsonResult){
        JSONArray jsonArray = null;
        String id_producto;
        String producto;
        String existencias;
        String descripcion;
        String precio_compra;
        String precio_venta;

        adapter.clear();
        try{
            jsonArray = new JSONArray(jsonResult);
        }catch (JSONException e){
            e.printStackTrace();
        }
        for(int i=0;i<jsonArray.length();i++){
            try{
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                id_producto = jsonObject.getString("id_producto");
                producto = jsonObject.getString("producto");
                existencias = jsonObject.getString("existencias");
                descripcion = jsonObject.getString("descripcion");
                precio_compra = jsonObject.getString("precio_compra");
                precio_venta = jsonObject.getString("precio_venta");
                adapter.add(id_producto + ":" + producto);
                Log.i("producto",producto);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}
