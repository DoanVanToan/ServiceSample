package toandoan.framgia.com.servicesample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static toandoan.framgia.com.servicesample.DownloadService.EXTRA_FILE_NAME;
import static toandoan.framgia.com.servicesample.DownloadService.EXTRA_PROGRESS;
import static toandoan.framgia.com.servicesample.DownloadService.EXTRA_RECEIVER;
import static toandoan.framgia.com.servicesample.DownloadService.EXTRA_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int WRITE_EXTERNAL_REQUEST = 1;
    private final static String URL_SAMPLE =
            "http://mp3.zing.vn/download/song/Yeu-La-Tha-Thu-Em-Chua-18-OST-OnlyC-OnlyC"
                    + "/ZHJGtLmNgkDkilhyLbctDnkmtZAESczZmCh?sig=6dc2cd852f9a168618e70618a7dbcbe3";
    private final static String FILE_NAME = "Yeu_La_Tha_Thu.mp3";

    private EditText mEditUrl, mEditFileName;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_download).setOnClickListener(this);
        mEditUrl = (EditText) findViewById(R.id.edit_url);
        mEditUrl.setText(URL_SAMPLE);

        mEditFileName = (EditText) findViewById(R.id.edit_file_name);
        mEditFileName.setText(FILE_NAME);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.msg_downloading));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    private boolean isPermissonGrant() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    WRITE_EXTERNAL_REQUEST);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_download) {
            if (isPermissonGrant()) {
                downloadFile();
            }
        }
    }

    private void downloadFile() {
        String url = mEditUrl.getText().toString();
        String fileName = mEditFileName.getText().toString();
        mProgressDialog.show();
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_FILE_NAME, fileName);
        intent.putExtra(EXTRA_RECEIVER, new DownloadReceiver(new Handler()));
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadFile();
        }
    }

    private class DownloadReceiver extends ResultReceiver {
        public DownloadReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == DownloadService.UPDATE_PROGRESS) {
                int progress = resultData.getInt(EXTRA_PROGRESS);
                mProgressDialog.setProgress(progress);
                if (progress == 100) {
                    mProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Download successful", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }
}
