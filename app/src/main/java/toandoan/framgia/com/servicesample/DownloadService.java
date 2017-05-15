package toandoan.framgia.com.servicesample;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by framgia on 15/05/2017.
 */

public class DownloadService extends IntentService {
    public static final int UPDATE_PROGRESS = 1;
    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_FILE_NAME = "EXTRA_FILE_NAME";
    public static final String EXTRA_RECEIVER = "EXTRA_RECEIVER";
    public static final String EXTRA_PROGRESS = "EXTRA_PROGRESS";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String urlToDownload = intent.getStringExtra(EXTRA_URL);
        String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
        ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();

            int fileLength = connection.getContentLength();

            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream("/sdcard/" + fileName);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                Bundle resultData = new Bundle();
                resultData.putInt(EXTRA_PROGRESS, (int) (total * 100 / fileLength));
                receiver.send(UPDATE_PROGRESS, resultData);
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle resultData = new Bundle();
        resultData.putInt(EXTRA_PROGRESS, 100);
        receiver.send(UPDATE_PROGRESS, resultData);
    }
}