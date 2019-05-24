package com.example.reciprice.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.reciprice.R;
import com.example.reciprice.model.Items;
import com.example.reciprice.model.Offer;
import com.example.reciprice.model.ProductResponse;
import com.example.reciprice.repo.ProductService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PriceActivity extends AppCompatActivity {
    private String upc;
    private List<Offer> offers;
    private RecyclerView recyclerView;
    private PriceAdapter priceAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ImageView imageViewImage;
    private TextView textViewTitle;
    private TextView textViewBrand;
    private TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        Intent intent = getIntent();
        //TODO: make sure names match up from ingredient activity
        upc = intent.getStringExtra("upc");

        wireWidgets();
        searchPrices();

        offers = new ArrayList<>();
        priceAdapter = new PriceAdapter(offers);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(priceAdapter);
    }

    private void wireWidgets() {
        recyclerView = findViewById(R.id.recyclerView_price);
        imageViewImage = findViewById(R.id.imageView_price_image);
        textViewTitle = findViewById(R.id.textView_price_title);
        textViewBrand = findViewById(R.id.textView_price_brand);
        textViewDescription = findViewById(R.id.textView_price_description);
    }

    private void searchPrices(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.upcitemdb.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProductService service = retrofit.create(ProductService.class);
        final Call<ProductResponse> productServiceCall = service.findByUpc(upc);

        productServiceCall.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                Items information = response.body().getItems().get(0);
                offers = response.body().getItems().get(0).getOffers();
                priceAdapter.notifyDataSetChanged();

                textViewTitle.setText(information.getTitle());
                textViewBrand.setText(information.getBrand());
                textViewDescription.setText(information.getDescription());
                Glide.with(imageViewImage).load(information.getImages().get(0)).into(imageViewImage);
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("ENQUEUE", "onFailure: " + t.getMessage());
            }
        });
    }
}
