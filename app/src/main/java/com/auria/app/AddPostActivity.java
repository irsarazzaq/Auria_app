package com.auria.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPostActivity extends AppCompatActivity {

    private Button backBtn;
    private Button postBtn, galleryBtn, cameraBtn;
    private EditText captionEditText;
    private ImageView postImageView;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private Uri selectedImageUri = null;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int CAMERA_REQUEST = 102;
    private static final int CAMERA_PERMISSION_REQUEST = 103;

    // Add this for camera photo storage
    private Uri cameraPhotoUri;

    // ✅ ImgBB API Key
    private static final String IMGBB_API_KEY = "76202053ebebe839aad818e140908e26";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        backBtn = findViewById(R.id.backBtn);
        postBtn = findViewById(R.id.postBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        captionEditText = findViewById(R.id.captionEditText);
        postImageView = findViewById(R.id.postImageView);
        TextView userNameText = findViewById(R.id.userNameText);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        if (currentUser != null && currentUser.getEmail() != null) {
            String email = currentUser.getEmail();
            String userName = email.split("@")[0];
            userNameText.setText(userName);
        }

        backBtn.setOnClickListener(v -> finish());
        galleryBtn.setOnClickListener(v -> openGallery());
        cameraBtn.setOnClickListener(v -> checkCameraPermission());
        postBtn.setOnClickListener(v -> uploadPost());

        captionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePostButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        updatePostButtonState();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Fixed Camera Permission Check
    private void checkCameraPermission() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_REQUEST);
        }
    }

    // Fixed Camera Opening Method
    private void openCamera() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Create a file to save the image
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Auria_Post_" + System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken from Auria app");
            cameraPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Add the URI to the intent
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);

            // Check if there's an app to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Camera error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                selectedImageUri = data.getData();
                displaySelectedImage();
            } else if (requestCode == CAMERA_REQUEST) {
                // Handle camera result
                if (cameraPhotoUri != null) {
                    selectedImageUri = cameraPhotoUri;
                    displaySelectedImage();
                    Toast.makeText(this, "Photo taken successfully", Toast.LENGTH_SHORT).show();
                } else if (data != null && data.getExtras() != null) {
                    // Fallback: if URI method fails, use bitmap
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (photo != null) {
                        selectedImageUri = getImageUri(photo);
                        postImageView.setImageBitmap(photo);
                        postImageView.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Photo taken successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            updatePostButtonState();
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "AURIA_" + timeStamp + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Auria");
        }

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            byte[] bitmapData = bos.toByteArray();
            getContentResolver().openOutputStream(uri).write(bitmapData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uri;
    }

    private void displaySelectedImage() {
        if (selectedImageUri != null) {
            postImageView.setImageURI(selectedImageUri);
            postImageView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPost() {
        String caption = captionEditText.getText().toString().trim();

        if (caption.isEmpty() && selectedImageUri == null) {
            Toast.makeText(this, "Please add caption or photo", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (selectedImageUri != null) {
            uploadToImgBB(caption);
        } else {
            getUsernameAndSavePost(caption, null);
        }
    }

    private void uploadToImgBB(String caption) {
        progressDialog.setMessage("Uploading image...");

        new Thread(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                inputStream.close();

                String base64Image = Base64.encodeToString(buffer, Base64.DEFAULT);
                base64Image = base64Image.replace("\n", "");

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

                String encodedImage = java.net.URLEncoder.encode(base64Image, "UTF-8");
                String requestBodyString = "image=" + encodedImage;

                RequestBody body = RequestBody.create(mediaType, requestBodyString);

                Request request = new Request.Builder()
                        .url("https://api.imgbb.com/1/upload?key=" + IMGBB_API_KEY)
                        .post(body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

                    Gson gson = new Gson();
                    ImgBBResponse imgBBResponse = gson.fromJson(jsonResponse, ImgBBResponse.class);

                    if (imgBBResponse.success) {
                        String imageUrl = imgBBResponse.data.url;

                        runOnUiThread(() -> {
                            getUsernameAndSavePost(caption, imageUrl);
                        });
                    } else {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "API Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
            }
        }).start();
    }

    class ImgBBResponse {
        boolean success;
        int status;
        Data data;

        class Data {
            String url;
        }
    }

    private void getUsernameAndSavePost(String caption, String imageUrl) {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = "User";
                    if (documentSnapshot.exists() && documentSnapshot.contains("username")) {
                        userName = documentSnapshot.getString("username");
                    } else if (currentUser.getEmail() != null) {
                        userName = currentUser.getEmail().split("@")[0];
                    }
                    savePostToFirestore(caption, imageUrl, userName);
                })
                .addOnFailureListener(e -> {
                    String userName = currentUser.getEmail() != null ?
                            currentUser.getEmail().split("@")[0] : "User";
                    savePostToFirestore(caption, imageUrl, userName);
                });
    }

    private void savePostToFirestore(String caption, String imageUrl, String userName) {
        Map<String, Object> post = new HashMap<>();
        String postId = UUID.randomUUID().toString();

        post.put("postId", postId);
        post.put("userId", currentUser.getUid());
        post.put("userName", userName);
        post.put("caption", caption);
        post.put("imageUrl", imageUrl);
        post.put("timestamp", System.currentTimeMillis());
        post.put("likesCount", 0);
        post.put("commentsCount", 0);
        post.put("likes", new HashMap<>());
        post.put("archived", false);
        post.put("deleted", false);

        db.collection("posts")
                .document(postId)
                .set(post)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Posted successfully!", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("refresh", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updatePostButtonState() {
        boolean hasContent = !captionEditText.getText().toString().trim().isEmpty()
                || selectedImageUri != null;

        postBtn.setEnabled(hasContent);
        postBtn.setTextColor(ContextCompat.getColor(this,
                hasContent ? R.color.auria_primary : R.color.auria_text_secondary));
    }
}