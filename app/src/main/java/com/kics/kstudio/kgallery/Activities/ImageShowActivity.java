package com.kics.kstudio.kgallery.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kics.kstudio.kgallery.Activities.ImageInfoActivity;
import com.kics.kstudio.kgallery.Adapters.PicsDateAdapter;
import com.kics.kstudio.kgallery.Adapters.Sliding_Image_Adapter;
import com.kics.kstudio.kgallery.DataBase.GallerySqlite;
import com.kics.kstudio.kgallery.DataModels.Photo_Item;
import com.kics.kstudio.kgallery.DataModels.PicsWithDates;
import com.kics.kstudio.kgallery.DataModels.Positions;
import com.kics.kstudio.kgallery.Misc.Constants;
import com.kics.kstudio.kgallery.Misc.Utils;
import com.kics.kstudio.kgallery.R;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DCIM;

public class ImageShowActivity extends AppCompatActivity {

    private String TAG = ImageShowActivity.class.getSimpleName();
    Boolean fvr8 = false;
    ViewPager viewPager;
    GallerySqlite gallerySqlite;
    public static Boolean fvr8time = false;

    public static final int REQUEST_CODE_TAKE_IMAGE = 0x101;
    Uri mImageUri;
    String TempFilePath = "";

    TextView textView;
    Sliding_Image_Adapter sliding_image_adapter;
    Integer info_id = 0;
    Integer itemPos;


    static List<Photo_Item> data;
    ImageView imageView;

    public static void setDatatoShow(List<Photo_Item> li) {
        data = li;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gal_activity_image_show);
//        MainActivity.deletenot = true;
        Constants.deletTempPrivates = true;
        gallerySqlite = new GallerySqlite(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        Intent intent = getIntent();
        final Integer pos = intent.getIntExtra("Pos", -1);
        itemPos = intent.getIntExtra("itemPos", -1);

        Log.d("PrivateCheck", "onCreate: " + Constants.is_private);
        if (Constants.is_private) {
            LinearLayout linearLayout = findViewById(R.id.linearLayout_p);
            linearLayout.setVisibility(View.VISIBLE);
        }

        viewPager = (ViewPager) findViewById(R.id.pic_pager);
        textView = (TextView) findViewById(R.id.textView_count);
        textView.setText((pos + 1) + "/" + data.size());
        info_id = pos;

        imageView = findViewById(R.id.fvr8);
        if (data.get(info_id).getKEY_FVR8() != null) {

            Log.d(TAG, "onCreate: Fvr8" + data.get(info_id).getKEY_OLD_PATH() + " fvrtime" + fvr8time);
            if (fvr8time) {
                imageView.setImageResource(R.drawable.ic_action_f_star);
            } else if (data.get(info_id).getKEY_FVR8()) {
                imageView.setImageResource(R.drawable.ic_action_f_star);
            } else {
                imageView.setImageResource(R.drawable.ic_action_n_star);
            }
        }

        sliding_image_adapter = new Sliding_Image_Adapter(this, data);
        viewPager.setAdapter(sliding_image_adapter);
        viewPager.setCurrentItem(pos);


        if (gallerySqlite.isFavr8(data.get(pos).getKEY_OLD_PATH())) {
            fvr8 = true;
            imageView.setImageResource(R.drawable.ic_action_f_star);
        } else {
            fvr8 = false;
            imageView.setImageResource(R.drawable.ic_action_n_star);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textView.setText((position + 1) + "/" + data.size());
                info_id = position;
                if (gallerySqlite.isFavr8(data.get(position).getKEY_OLD_PATH())) {
                    fvr8 = true;
                    imageView.setImageResource(R.drawable.ic_action_f_star);
                } else {
                    fvr8 = false;
                    imageView.setImageResource(R.drawable.ic_action_n_star);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.more_info) {
            Intent intent = new Intent(this, ImageInfoActivity.class);
            intent.putExtra("Data", (Serializable) data.get(info_id));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void finishactivity(View view) {
        if (StatusCheck()) {
            Constants.passwordNalagana = true;
        }
        Constants.deletTempPrivates = false;
        Constants.back = true;
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishactivity(null);
    }

    public void openCamera(View view) {

        String mImagePath;
        if (Build.VERSION.SDK_INT <= 23) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //yyyy-MM-dd HH:mm:ss
            //yyyyMMdd_HHmmss
            String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date());
            File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath()
                    + "/Camera/IMG_" + timeStamp + ".jpg");

            TempFilePath = mImagePath = file.getAbsolutePath();
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, mImagePath);
            mImageUri = ImageShowActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (Utils.detectIntent(ImageShowActivity.this, intent)) {
                setResult(RESULT_OK, intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri));
                startActivityForResult(intent, REQUEST_CODE_TAKE_IMAGE);
                Toast.makeText(ImageShowActivity.this, "Take a Photo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ImageShowActivity.this, "No photo App in this phone!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REQUEST_CODE_TAKE_IMAGE &&
                resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                File file = new File(TempFilePath);
                OutputStream os = null;
                try {
                    os = new BufferedOutputStream(new FileOutputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                refreshSystemMediaScanDataBase(getApplicationContext(), file.getPath());
                addNewFileToCamera(file);
            }
        }
    }

    private void addNewFileToCamera(File file) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String date = df.format(c);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        if (Constants.Camera_photos_list.size() == 0) {
            List<Photo_Item> list = new ArrayList<>();
            list.add(new Photo_Item(file.getPath(), date, "photo_" + String.valueOf(c), "Camera", Utils.getSize(String.valueOf(file.length())), bitmap.getHeight() + " * " + bitmap.getWidth(), ".jpg", false));
            PicsWithDates picsWithDates = new PicsWithDates(date, list);
            Constants.Camera_photos_list.add(picsWithDates);
            PicsDateAdapter.i_cameraUpdate.UpdateAll();

        } else {
            if (Constants.Camera_photos_list.get(0).getDate().equals(date)) {
                Constants.Camera_photos_list.get(0).getPics().add(0, new Photo_Item(file.getPath(), date, "photo_" + String.valueOf(c), "Camera", Utils.getSize(String.valueOf(file.length())), bitmap.getHeight() + " * " + bitmap.getWidth(), ".jpg", false));
                PicsDateAdapter.i_cameraUpdate.singleUpdate(0, 0);
            } else {
                List<Photo_Item> list = new ArrayList<>();
                list.add(new Photo_Item(file.getPath(), date, "photo_" + String.valueOf(c), "Camera", Utils.getSize(String.valueOf(file.length())), bitmap.getHeight() + " * " + bitmap.getWidth(), ".jpg", false));
                PicsWithDates picsWithDates = new PicsWithDates(date, list);
                Constants.Camera_photos_list.add(picsWithDates);
                PicsDateAdapter.i_cameraUpdate.UpdateAll();
            }
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_btn:
                share();
                break;

            case R.id.sharep_btn:
                share();
                break;

            case R.id.fvr8_btn:
                addFavr8UI();
                break;

            case R.id.delete_btn:
                delete_simeple();
                break;

            case R.id.lock_btn:
                lock_simple();
                break;

            case R.id.deletep_btn:
                delete_private();
                break;

            case R.id.unlock_btn:
                unlock_Private();
                break;

        }
    }


    private Boolean StatusCheck() {
        SharedPreferences prefs = ImageShowActivity.this.getSharedPreferences("passfile", MODE_PRIVATE);
        return prefs.getBoolean("LockStatus", false);
    }


    public static void refreshSystemMediaScanDataBase(Context context, String docPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(docPath));
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fvr8time = false;
        finish();
    }


    void unlock_Private() {
        new LovelyStandardDialog(ImageShowActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.white)
                .setButtonsColorRes(R.color.red)
                .setIcon(R.drawable.ic_info)
                .setTitle("Action")
                .setMessage("Are You Sure You Want To Unlock This Photo?")
                .setPositiveButton("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (StatusCheck()) {
                                Constants.passwordNalagana = true;
                            }
                            gallerySqlite.RemovePrivatephoto(data.get(info_id), true);
                            Constants.PrivatesToRemove.add(data.get(info_id));
                            Constants.changeinPrivate = true;
                            updateSliderForPrivate();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    void delete_private() {
        new LovelyStandardDialog(ImageShowActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.white)
                .setButtonsColorRes(R.color.red)
                .setIcon(R.drawable.ic_info)
                .setTitle("Action")
                .setMessage("Are You Sure You Want to Delete this Photo?")
                .setPositiveButton("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (StatusCheck()) {
                            Constants.passwordNalagana = true;
                        }
                        if (gallerySqlite.isFavr8(data.get(info_id).getKEY_OLD_PATH())) {
                            if (gallerySqlite.RemoveFvr8(data.get(info_id).getKEY_OLD_PATH())) {

                                Constants.RemovePositionsFromVP.add(data.get(info_id));
                                Constants.change_in_favr8_fragment = true;
                                try {
                                    gallerySqlite.RemovePrivatephoto(data.get(info_id), false);
                                    Constants.PrivatesToRemove.add(data.get(info_id));
                                    Constants.changeinPrivate = true;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                gallerySqlite.RemovePrivatephoto(data.get(info_id), false);
                                Constants.PrivatesToRemove.add(data.get(info_id));
                                Constants.changeinPrivate = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                        //updateSlider();
                        updateSliderForPrivate();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }


    void lock_simple() {
        if (StatusCheck()) {
            Constants.passwordNalagana = true;
        }

        new LovelyStandardDialog(ImageShowActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.white)
                .setButtonsColorRes(R.color.red)
                .setIcon(R.drawable.ic_info)
                .setTitle("Action")
                .setMessage("Are You Sure You Want to Move This Photo to Private Album?")
                .setPositiveButton("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            if (gallerySqlite.isFavr8(data.get(info_id).getKEY_OLD_PATH())) {
                                //due to removal make it by new
                                Constants.RemovePositionsFromVP.add(new Photo_Item(data.get(info_id).getKEY_OLD_PATH(), data.get(info_id).getKEY_TIMESTAMP()));
                                Constants.change_in_favr8_fragment = true;
                            }
                            Constants.remove_orRemoveandPrivatize = true;
                            gallerySqlite.addPrivatephoto(data.get(info_id));

                            if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_favorite))) {
                                Constants.DeleteAlso = true;
                                Utils.Toast(ImageShowActivity.this, "From Fvr8S");
                                updateSliderForFavroite();
                            } else {
                                updateSlider();
                            }


                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }


    void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        final String path = data.get(info_id).getKEY_OLD_PATH();
        Uri uri = FileProvider.getUriForFile(ImageShowActivity.this, getResources().getString(R.string.authority), new File(path));
        intent.setDataAndType(uri, "image/*");
        PackageManager pm = getPackageManager();
        if (intent.resolveActivity(pm) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(intent);
        }
    }

    private void addFavr8UI() {
        if (gallerySqlite.isFavr8(data.get(info_id).getKEY_OLD_PATH())) {
            //remove

            gallerySqlite.RemoveFvr8(data.get(info_id).getKEY_OLD_PATH());
            Constants.RemovePositionsFromVP.add(data.get(info_id));
            Constants.change_in_favr8_fragment = true;
            fvr8 = false;
            imageView.setImageResource(R.drawable.ic_action_n_star);
        } else {
            //add
            gallerySqlite.addFvr8(data.get(info_id).getKEY_OLD_PATH());
            Constants.AddFavr8.add(data.get(info_id));
            Constants.change_in_favr8_fragment = true;
            fvr8 = true;
            imageView.setImageResource(R.drawable.ic_action_f_star);
        }

        if (StatusCheck()) {
            Constants.passwordNalagana = true;
        }
    }

    void delete_simeple() {
        new LovelyStandardDialog(ImageShowActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColorRes(R.color.white)
                .setButtonsColorRes(R.color.red)
                .setIcon(R.drawable.ic_info)
                .setTitle("Action")
                .setMessage("Are You Sure You Want to Delete this Photo?")
                .setPositiveButton("YES", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Boolean check = false;
                        if (StatusCheck()) {
                            Constants.passwordNalagana = true;
                        }
                        Constants.remove_orRemoveandPrivatize = false;
                        if (gallerySqlite.isFavr8(data.get(info_id).getKEY_OLD_PATH())) {
                            if (gallerySqlite.RemoveFvr8(data.get(info_id).getKEY_OLD_PATH())) {
                                //due to removal make it by new
                                Constants.RemovePositionsFromVP.add(new Photo_Item(data.get(info_id).getKEY_OLD_PATH(), data.get(info_id).getKEY_TIMESTAMP()));
                                Constants.change_in_favr8_fragment = true;
                                DeleteFile(data.get(info_id).getKEY_OLD_PATH());
                            }
                        } else {
                                   DeleteFile(data.get(info_id).getKEY_OLD_PATH());
                        }

                        Log.d("ffT", "onClick: " + Constants.Fragment_name +" iid:"+info_id);
                        if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_favorite))) {
                            Constants.DeleteAlso = true;
                            updateSliderForFavroite();
                        } else {
                            updateSlider();
                        }
                    }
                }).setNegativeButton("NO", null).show();

    }

    private void DeleteFile(String path) {
        File file = new File(path);
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
            }
            if (file.exists()) {
                getApplicationContext().deleteFile(file.getName());
            }
        }
        refreshSystemMediaScanDataBase(getApplicationContext(), data.get(info_id).getKEY_OLD_PATH());
    }

    void updateSlider() {
        Constants.change_in_albums = true;
        RemoveData();
        textView = findViewById(R.id.textView_count);
        sliding_image_adapter.notifyDataSetChanged();
        textView.setText((viewPager.getCurrentItem() + 1) + "/" + data.size());
    }

    void updateSliderForFavroite() {
        Log.d("ffT", "updateSliderForFavroite: B:" + Constants.Fragment_name +" iid:"+info_id +" "+data.size());
        data.remove(data.get(info_id));
        Log.d("ffT", "updateSliderForFavroite: A:" + Constants.Fragment_name +" iid:"+info_id +" "+data.size());
        textView = findViewById(R.id.textView_count);
        sliding_image_adapter.notifyDataSetChanged();
        textView.setText((viewPager.getCurrentItem() + 1) + "/" + data.size());
    }

    void updateSliderForPrivate() {
        data.remove(data.get(info_id));
        Constants.change_in_albums = true;
        textView = findViewById(R.id.textView_count);
        sliding_image_adapter.notifyDataSetChanged();
        textView.setText((viewPager.getCurrentItem() + 1) + "/" + data.size());
    }

    private void RemoveData() {
        String album = data.get(info_id).getKEY_ALBUM();
        if (album.equals("Camera")) {
            Constants.change_in_photo_fragment = true;
            Constants.positions.add(new Positions(data.get(info_id).getKEY_MAIN_POS(), 0, data.get(info_id).getKEY_OLD_PATH()));
            Constants.Selected_list.add(data.get(info_id));
        } else if (Constants.Fragment_name.equals(getResources().getString(R.string.tab_album))) {
            Constants.Tempositions.add(new Positions(data.get(info_id).getKEY_MAIN_POS(), 0, data.get(info_id).getKEY_OLD_PATH()));
            Constants.TempSelected_list.add(data.get(info_id));
        }
        data.remove(data.get(info_id));
    }


}


