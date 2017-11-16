package com.um56.jefhunts.windows.inshortapp;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    Map<Integer,String> articleURLs;
    Map<Integer,String> articleTitles = new HashMap<>();
    ArrayList<Integer> articleIds = new ArrayList<>();

    SQLiteDatabase articlesDB;
    ArrayList<String> titles;
    ArrayAdapter<String> arrayAdapter;

    ArrayList<String> urls = new ArrayList<>();
    ArrayList<String> content;

    {
        content = new ArrayList<>();
        articleURLs = new HashMap<>();
        titles = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_activity, titles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent(getApplicationContext(),ArticleActivity.class);
                i.putExtra("articleURL",urls.get(position));
                i.putExtra("content",content.get(position));
                startActivity(i);
                /*

                Log.i("articleUrls",urls.get(position));
                */
            }
        });

        /* Creating database to store data */
        articlesDB = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY," +
                " articleId INTEGER, url VARCHAR, title VARCHAR, content VARCHAR)");

        updateListView();

        DownloadTask task = new DownloadTask();
        try {
            String result = task.execute("https://api.myjson.com/bins/clxhd").get();


            JSONArray jsonArray = new JSONArray(result);
            for (int i =0; i<20;i++) {

                String articleinfo = jsonArray.getString(i);
                // Log.i("Articles",articleinfo);
                JSONObject jsonObject = new JSONObject(articleinfo);
                String articleTITLE = jsonObject.getString("TITLE");
                String articleURL = jsonObject.getString("URL");
                String articleID = jsonObject.getString("ID");

                String articleContent = " ";

                articleIds.add(Integer.valueOf(articleID));
                articleTitles.put(Integer.valueOf(articleID), articleTITLE);
                articleURLs.put(Integer.valueOf(articleID), articleURL);


                String sql = "INSERT INTO articles (articleId, url, title, content) VALUES (? , ? , ? , ?)";

                SQLiteStatement statement = articlesDB.compileStatement(sql);

                statement.bindString(1, articleID);
                statement.bindString(2, articleURL);
                statement.bindString(3, articleTITLE);
                statement.bindString(4, articleContent);

                statement.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateListView() {

        try {

            Log.i("UI UPDATED", "DONE");

            Cursor c = articlesDB.rawQuery("SELECT * FROM articles ORDER BY articleId ", null);

            int contentIndex = c.getColumnIndex("content");
            int urlIndex = c.getColumnIndex("url");
            int titleIndex = c.getColumnIndex("title");

            c.moveToFirst();

            titles.clear();
            urls.clear();


            while (true) {

                titles.add(c.getString(titleIndex));
                urls.add(c.getString(urlIndex));
                content.add(c.getString(contentIndex));

                c.moveToNext();

            }

            /* arrayAdapter.notifyDataSetChanged(); */

        }catch (Exception e) {

            e.printStackTrace();

        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            //JSONObject jsonObject=null;
            HttpURLConnection urlConnection;
            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);


                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                articlesDB.execSQL("DELETE FROM articles");

            }catch (Exception e){
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            updateListView();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

