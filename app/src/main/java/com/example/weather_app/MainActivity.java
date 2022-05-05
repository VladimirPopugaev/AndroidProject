package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private String KEY_WEATHER = "ae2c094d17c9521b2fad4657fcb75a31";
    private EditText user_field;
    private Button main_btn;
    private TextView result_info;


    private View.OnClickListener mSetOnMainBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (user_field.getText().toString().trim().equals("")) {
                Toast.makeText(MainActivity.this,R.string.user_field_is_empty, Toast.LENGTH_SHORT);
            } else {
                String city = user_field.getText().toString();
                String url = "https://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid="+ KEY_WEATHER +"&units=metric&lang=ru";

                new GetURLData().execute(url);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);
        main_btn.setOnClickListener(mSetOnMainBtnListener);

    }

    // Асинхронный класс для получения инфы из ссылки
    private class GetURLData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() { //Перед самым отправлением url в интернет
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();

                try {
                    if (reader != null) reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                result_info.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}