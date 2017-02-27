package com.homike.user.HoBook;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * Created by User on 2/11/2017.
 */

public class ContactUs extends AppCompatActivity {
    SpannableStringBuilder builder = new SpannableStringBuilder(); // to color certain text
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);
        Toolbar toolBarContactUs = (Toolbar)findViewById(R.id.toolbarContactUs);
        setSupportActionBar(toolBarContactUs);

        //to color certain text
        SpannableString str1 = new SpannableString("Submit any warehouse sales that you know to ");
        str1.setSpan(new ForegroundColorSpan(Color.BLACK),0,str1.length(),0);
        builder.append(str1);
        SpannableString str2 = new SpannableString("khlim1993@gmail.com");
        str2.setSpan(new ForegroundColorSpan(Color.BLUE),0,str2.length(),0);
        builder.append(str2);
        SpannableString str3 = new SpannableString(" and get listed within minutes!");
        str3.setSpan(new ForegroundColorSpan(Color.BLACK),0,str3.length(),0);
        builder.append(str3);
        TextView contactUs = (TextView)findViewById(R.id.contactUsMessage);
        contactUs.setText(builder, TextView.BufferType.SPANNABLE);
    }
}
