package com.example.memeshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.memeshare.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
getMemes();
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMemes();
            }
        });
binding.share.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        shareMemes();
    }
});

    }

    private void getMemes() {
        String url = " https://meme-api.com/gimme";
        binding.loader.setVisibility(View.VISIBLE);
        binding.memeimage.setVisibility(View.GONE);
        RequestQueue que= Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest


                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String imgurl = response.getString("url");
                            Glide.with(getApplicationContext()).load(imgurl).into(binding.memeimage);
                        binding.loader.setVisibility(View.GONE);
                        binding.memeimage.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
que.add(jsonObjectRequest);

    }

    private void shareMemes() {
        Bitmap image = getBitmapFrowmView(binding.memeimage);
        shareimgandtext(image);
}

    private void shareimgandtext(Bitmap image) {
    Uri uri = getimagetoshare(image);
    Intent intent = new Intent(Intent.ACTION_SEND);

    intent.putExtra(Intent.EXTRA_STREAM,uri); //putting img url to be shared
    intent.setType("image/png"); // setting type of img
        //calling startactivity to share
        startActivity(Intent.createChooser(intent,"Share image via : "));
    }
  //uri to filefoler
   private Uri getimagetoshare(Bitmap image){
        File imageFolder = new File(getCacheDir(),"images");
        Uri uri = null;
        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "meme.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, "com.example.memeshare.fileProvider",file);

        }
        catch(Exception e){
            Toast.makeText(this, " "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
   }

    private Bitmap getBitmapFrowmView(View view) {
//DEFINE A BITMAP WITH SAME WIDTH AND HEIGHT
        Bitmap returedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
//bind a canvas to it
        Canvas canvas = new Canvas(returedBitmap);
        //get the bg view of layout
        Drawable background = view.getBackground();
        if(background != null){
            background.draw(canvas);
        }else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returedBitmap;
    }
    }

