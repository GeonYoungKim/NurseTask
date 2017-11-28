package skuniv.ac.kr.nursetask.Core.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gunyoungkim on 2017-11-07.
 */

public class back extends AsyncTask<String, Integer, Bitmap> {
    ImageView imageView;
    Bitmap bmImg;

    public back(ImageView imageView,Bitmap bmImg) {
        this.imageView = imageView;
        this.bmImg=bmImg;
    }
    @Override
    protected Bitmap doInBackground(String... urls) {
        // TODO Auto-generated method stub
        try {
            URL myFileUrl = new URL(urls[0]);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bmImg = BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmImg;
    }
    protected void onPostExecute(Bitmap img) {
        imageView.setImageBitmap(bmImg);
    }
}