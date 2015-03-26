package com.example.vincentdiener.guerillasensing;


import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment implements View.OnClickListener {


    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        Button b = (Button) rootView.findViewById(R.id.button);
        b.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.0.219/guerillaSensingServer/add_device");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList< >(2);
            nameValuePairs.add(new BasicNameValuePair("mac", "A3:F3:38:27:36:16:26:37"));
            nameValuePairs.add(new BasicNameValuePair("lat", "3.12312312224"));
            nameValuePairs.add(new BasicNameValuePair("lon", "7.31274554754"));
            nameValuePairs.add(new BasicNameValuePair("height", "1233776.2132"));
            nameValuePairs.add(new BasicNameValuePair("info", "On roof."));
            nameValuePairs.add(new BasicNameValuePair("picture", "http://link.to.picture.com"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String responseStr = EntityUtils.toString(response.getEntity());
            Log.d("test", responseStr);

        } catch (ClientProtocolException e) {
            //
        } catch (IOException e) {
            //
        }


    }

    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            public void run() {
                postData();
            }
        }).start();
    }
}
