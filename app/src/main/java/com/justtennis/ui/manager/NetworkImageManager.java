package com.justtennis.ui.manager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justtennis.R;
import com.justtennis.ui.picasso.transformation.CropTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkImageManager {

    private static final String TAG = NetworkImageManager.class.getName();
    private static final String NB_RESULT_PER_PAGE = "20";
    private static final String SEARCH_MODEL = "atp tennis";

    private static String baseURLString = "https://api.flickr.com/services/rest";
    private static String apiKey = "a6d819499131071f158fd740860a5a88";

    private static OkHttpClient httpclient = new OkHttpClient();
    private static ObjectMapper mapper = new ObjectMapper();
    private static NetworkImageManager instance;
    private static List<String> resultURLs = new ArrayList<>();
    private static CropTransformation.GravityHorizontal[] h = new CropTransformation.GravityHorizontal[] {CropTransformation.GravityHorizontal.LEFT, CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityHorizontal.RIGHT};
    private static CropTransformation.GravityVertical[] v = new CropTransformation.GravityVertical[] {CropTransformation.GravityVertical.TOP, CropTransformation.GravityVertical.CENTER, CropTransformation.GravityVertical.BOTTOM};
    private static int cropSize = 3;
    private static Random rnd = new Random(1);

    private NetworkImageManager() {
    }

    public static NetworkImageManager getInstance() {
        if (instance == null) {
            instance = new NetworkImageManager();
        }
        return instance;
    }

    public void getPhoto(final ImageView imageView, final String model) {
        getPhoto( imageView, null, model);
    }

    private void getPhoto(final ImageView imageView, final ProgressBar progress, final String model) {
        final LoaderImageTask task = new LoaderImageTask() {
            @Override
            public void run() {
                Picasso.get()
                        .load(getImage())
                        .transform(new CropTransformation(500, 150, h[rnd.nextInt(cropSize)], v[rnd.nextInt(cropSize)]))
                        .into(imageView);
            }
        };
        getPhoto(task, progress, model);
    }

    public void getPhotoInBackground(final Context context, final View view) {
        getPhotoInBackground(context, view, SEARCH_MODEL);
    }

    private void getPhotoInBackground(final Context context, final View view, final String model) {
        getPhotoInBackground(context, view, null, model);
    }

    private void getPhotoInBackground(final Context context, final View view, final ProgressBar progress, final String model) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Resources resources = context.getResources();
                BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
                drawable.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.OVERLAY);
                view.setBackground(drawable);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Nothing to do
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Nothing to do
            }
        };
        final LoaderImageTask task = new LoaderImageTask() {
            @Override
            public void run() {
                Picasso.get()
                        .load(getImage())
                        .transform(new CropTransformation(500, 150, h[rnd.nextInt(cropSize)], v[rnd.nextInt(cropSize)]))
                        .into(target);
            }
        };
        getPhoto(task, progress, model);
    }

    private void getPhoto(final LoaderImageTask task, final ProgressBar progress, final String model) {
        // https://www.flickr.com/services/api/flickr.photos.search.html
        // https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=a6d819499131071f158fd740860a5a88&extras=url_h,date_taken&per_page=10&text=atp+tennis+court

        HttpUrl route = Objects.requireNonNull(HttpUrl.parse(baseURLString))
                .newBuilder()
                .addQueryParameter("per_page", NB_RESULT_PER_PAGE)
                .addQueryParameter("text", model)
                .addQueryParameter("media", "photos")
                .addQueryParameter("content_type", "1") // 1 = Photo Only
                .addQueryParameter("tag_mode", "all") //Either 'any' for an OR combination of tags, or 'all' for an AND combination. Defaults to 'any' if not specified.
                .addQueryParameter("extras", "url_h,date_taken")
                .addQueryParameter("method", "flickr.photos.search")
                .addQueryParameter("format", "json")
                .addQueryParameter("nojsoncallback", "1")
                .addQueryParameter("api_key", apiKey)
                .build();
        Log.i(TAG, "route:" + route.toString());
        Request request = new Request.Builder().url(route).get().build();

        httpclient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {
                        Log.e(TAG, e.getMessage());
                        if (progress != null) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> progress.setVisibility(View.GONE));
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        byte[] bytes = response.body().bytes();
                        JsonNode rootNode = mapper.readTree(bytes);

                        JsonNode photosNode = rootNode.path("photos").path("photo");
                        Iterator<JsonNode> elements = photosNode.elements();
                        while (elements.hasNext()) {
                            JsonNode photoNode = elements.next();
                            String url = photoNode.path("url_h").asText();
                            if (url != null && !url.trim().isEmpty() && !resultURLs.contains(url)) {
                                resultURLs.add(url);
                            }
                        }

                        Handler handler = new Handler(Looper.getMainLooper());
                        String url = find1stNotEmpty();
                        if (url != null) {
                            handler.post(() -> task.setImage(url));
                            handler.post(task);
                        }
                        handler.post(() -> {
                            if (progress != null) {
                                progress.setVisibility(View.GONE);
                            }
                        });
                    }
                });

        resultURLs.clear();
    }

    private String find1stNotEmpty() {
        if (!resultURLs.isEmpty()) {
            int index = rnd.nextInt(resultURLs.size());
            String url = resultURLs.get(index);
            Log.i(TAG, "resultURLs size:" + resultURLs.size() + " index:" + index + " url:" + url);
            return url;
        }
        return null;
    }

    private abstract class LoaderImageTask implements Runnable {

        private String image;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}