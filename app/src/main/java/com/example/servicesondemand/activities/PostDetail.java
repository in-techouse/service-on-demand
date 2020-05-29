package com.example.servicesondemand.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.servicesondemand.R;
import com.example.servicesondemand.director.Helpers;
import com.example.servicesondemand.director.Session;
import com.example.servicesondemand.model.Post;
import com.example.servicesondemand.model.User;

public class PostDetail extends AppCompatActivity {
    private static final String TAG = "PostDetail";
    private Session session;
    private User user;
    private Helpers helpers;
    private Post post;
    private SliderLayout slider;
    private TextView date, time, offers, category, status, address, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent it = getIntent();
        if (it == null) {
            Log.e(TAG, "Intent is null");
            finish();
            return;
        }

        Bundle bundle = it.getExtras();
        if (bundle == null) {
            Log.e(TAG, "Bundle is null");
            finish();
            return;
        }

        post = (Post) bundle.getSerializable("post");

        if (post == null) {
            Log.e(TAG, "Post is null");
            finish();
            return;
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeButtonEnabled(true);

        helpers = new Helpers();
        session = new Session(getApplicationContext());
        user = session.getUser();

        slider = findViewById(R.id.slider);
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.setDuration(2000);

        for (String str : post.getImages()) {
            TextSliderView textSliderView = new TextSliderView(PostDetail.this);
            textSliderView
                    .description("")
                    .image(str)
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            slider.addSlider(textSliderView);
        }

        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        offers = findViewById(R.id.offers);
        category = findViewById(R.id.category);
        status = findViewById(R.id.status);
        address = findViewById(R.id.address);
        description = findViewById(R.id.description);

        date.setText(post.getDate());
        time.setText(post.getTime());
        offers.setText(post.getOffers().size() + "");
        category.setText(post.getCategory());
        status.setText(post.getStatus());
        address.setText(post.getAddress());
        description.setText(post.getDescription());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
