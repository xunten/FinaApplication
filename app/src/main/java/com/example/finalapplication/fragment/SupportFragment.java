package com.example.finalapplication.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalapplication.R;

public class SupportFragment extends Fragment {
    TextView call,feedback;
    ImageView facebook, instagram, youtube, share;
    View root;

    public SupportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_support, container, false);

        initView();
        initControl();

        return root;
    }

    private void initView() {
        call = root.findViewById(R.id.call);
        feedback = root.findViewById(R.id.feedback);
        facebook = root.findViewById(R.id.facebook);
        instagram = root.findViewById(R.id.instagram);
        youtube = root.findViewById(R.id.youtube);
        share = root.findViewById(R.id.share);
    }

    private void initControl() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:0364287702"));
                startActivity(callIntent);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("tientx.21it@vku.udn.vn") + "?subject=" +
                        Uri.encode("Feedback to TXT Shoes Shop !") + "&body=" + Uri.encode("");

                Uri uri = Uri.parse(uriText);
                intent.setData(uri);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
        facebook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.facebook.com/x.ttene.1303");
            }
            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });
        instagram.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.instagram.com/xten.1303/");
            }
            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });
        youtube.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.youtube.com/channel/UCAojmkPHZI2eEW8cRO9FhTQ");
            }
            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                String sharebody = "Welcome to VKU";
                String subject = "https://vku.udn.vn/";
                i.putExtra(Intent.EXTRA_SUBJECT,sharebody);
                i.putExtra(Intent.EXTRA_TEXT,subject);
                startActivity(Intent.createChooser(i,"VKU - UDN"));
            }
        });
    }
}