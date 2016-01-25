package com.emplaceme.feedback.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.emplaceme.feedback.R;

public class ThanxGiving extends AppCompatActivity {

    private RelativeLayout layoutThanx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanx_giving);

        layoutThanx = (RelativeLayout) findViewById(R.id.layoutThanx);

        layoutThanx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent screen = new Intent(ThanxGiving.this, Home.class);
                screen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(screen);
            }
        });

    }

}
