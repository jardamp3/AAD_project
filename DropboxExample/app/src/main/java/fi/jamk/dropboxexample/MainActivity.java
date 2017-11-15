package fi.jamk.dropboxexample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity
{

    final static private String APP_KEY = "c197wszq6tlopn6";
    final static private String APP_SECRET = "qhuuta3vl4hap4h";

    DbxRequestConfig config;
    DbxClientV2 client;
    EditText txtToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        client = new DbxClientV2(config, "98DeY4BltxAAAAAAAAABGjUIj9p0ARyDQH10iykHCB17Q6XOK8rnVZPE7sX6ZxPt");
    }

    public void saveToDropboxClick(View v) throws IOException, DropboxException
    {
        txtToSave = (EditText) findViewById(R.id.editText);
        String text = txtToSave.getText().toString();

        new DropboxTask().execute(text);

    }


    private class DropboxTask extends AsyncTask<String, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File txtFile = new File(getFilesDir(), timeStamp + ".txt");
            try
            {
                FileWriter fw = new FileWriter(txtFile);
                fw.write(params[0]);
                fw.close();

            } catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }

            try (InputStream in = new FileInputStream(txtFile))
            {
                client.files().uploadBuilder("/"+txtFile.getName()).uploadAndFinish(in);

            } catch (UploadErrorException e)
            {
                e.printStackTrace();
                return false;
            } catch (DbxException e)
            {
                e.printStackTrace();
                return false;
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return false;
            } catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean result) {

            Log.d("RESULT: ", String.valueOf(result));

            runOnUiThread(new Runnable() {
                public void run() {
                    if(result){
                        Toast.makeText(MainActivity.this, "Successfully uploaded !", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Problem with uploading !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            super.onPostExecute(result);
        }
    }

}
