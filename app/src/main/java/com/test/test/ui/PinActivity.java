package com.test.test.ui;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.test.test.net.RequestQueue;
import com.test.test.net.CustomRequest;
import com.test.test.util.AudioPinUtil;
import com.test.test.util.DropBoxUtil;
import com.test.test.util.AudioHelper;
import com.test.test.util.RequestHelper;
import com.test.test.vad.WordDetection;
import com.test.test.vad.WordInterval;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PinActivity extends AppCompatActivity {
    private Button mButton0;
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButtonOk;
    private String mPinString;
    private TextView mPinView;
    private Switch mMFSwitch;
    private TextView mEditText;
    private static String root = null;
    String[] vocabulary = null;
    private int retryCount = 0;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private boolean enrollmentStarted = false;
    private String enrollment;
    private int mCounter = 0;
    private final String TAG = PinActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        inflatComponents();
        mPinString = "";
        createDirectoriesIfNeeded();

        vocabulary = getResources().getStringArray(R.array.vocabulary);
        RequestHelper.initApp(getResources());

    }

    private void inflatComponents(){
        mButton0 = (Button)findViewById(R.id.button0);
        mButton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="0";
                mPinView.setText(mPinString);
            }
        });
        mButton1 = (Button)findViewById(R.id.button1);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="1";
                mPinView.setText(mPinString);
            }
        });
        mButton2 = (Button)findViewById(R.id.button2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="2";
                mPinView.setText(mPinString);
            }
        });
        mButton3 = (Button)findViewById(R.id.button3);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="3";
                mPinView.setText(mPinString);
            }
        });
        mButton4 = (Button)findViewById(R.id.button4);
        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="4";
                mPinView.setText(mPinString);
            }
        });
        mButton5 = (Button)findViewById(R.id.button5);
        mButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="5";
                mPinView.setText(mPinString);
            }
        });
        mButton6 = (Button)findViewById(R.id.button6);
        mButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="6";
                mPinView.setText(mPinString);
            }
        });
        mButton7 = (Button)findViewById(R.id.button7);
        mButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="7";
                mPinView.setText(mPinString);
            }
        });
        mButton8 = (Button)findViewById(R.id.button8);
        mButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="8";
                mPinView.setText(mPinString);
            }
        });
        mButton9 = (Button)findViewById(R.id.button9);
        mButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString+="9";
                mPinView.setText(mPinString);
            }
        });
        mButtonOk = (Button)findViewById(R.id.buttonOk);
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPinString="";
                mPinView.setText(mPinString);
                animateStrings();
            }
        });
        mPinView = (TextView)findViewById(R.id.pinView);
        mEditText = (TextView) findViewById(R.id.editText);

        mMFSwitch = (Switch) findViewById(R.id.switchMaleFemale);
        mMFSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }


    private void createDirectoriesIfNeeded() {
        root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File folder = new File(root, "AudioRecord");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File audioFolder = new File(folder.getAbsolutePath(), "Audio");
        if (!audioFolder.exists()) {
            audioFolder.mkdir();
        }
        root = audioFolder.getAbsolutePath();
    }

    private void animateStrings(){
        final Timer timer = new Timer();
        startRecording();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEditText.setText(vocabulary[mCounter % 3]);
                    }
                });
                mCounter++;
                if(mCounter > 10){
                    timer.cancel();
                    stopRecording();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEditText.setText("Enrollment started ..");
                        }
                    });
                }
            }
        },601,1800);
    }

    private void startRecording() {
        enrollmentStarted = false;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                AudioHelper.RECORDER_SAMPLERATE, AudioHelper.RECORDER_CHANNELS,
                AudioHelper.RECORDER_AUDIO_ENCODING, AudioHelper.bufferSize);
        if (recorder == null || recorder.getState() != 1)
            recorder = AudioHelper.findAudioRecord(this);
        if (recorder == null) {
            Toast.makeText(PinActivity.this, "Not able to open audio recorder!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (recorder.getState() == 1) {
            recorder.startRecording();
        }
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        byte data[] = new byte[AudioHelper.bufferSize];
        String filename = AudioHelper.getTempFilename();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (AudioPinUtil.debug)
                Toast.makeText(PinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        int read = 0;
        if (null != os) {
            while (isRecording) {
                read = recorder.read(data, 0, AudioHelper.bufferSize);
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (AudioPinUtil.debug)
                            Toast.makeText(PinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
                if (AudioPinUtil.debug)
                    Toast.makeText(PinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void stopRecording() {
        if (null != recorder) {
            isRecording = false;
            int i = recorder.getState();
            if (i == 1)
                recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
        enrollment = AudioHelper.getFilename("enrollment");
        AudioHelper.copyWaveFile(AudioHelper.getTempFilename(), enrollment);
        AudioHelper.deleteTempFile();
        if(RequestHelper.dropbox_access_token.isEmpty()){
            //uploadWavFileOnCloud(enrollment);
        }
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    uploadWavFile(enrollment);
                }
            }).start();
        }
        retryCount = 0;
    }

    private void uploadWavFile(String enrollment) {
        try {
            String url = DropBoxUtil.uploadFile(new FileInputStream(new File(enrollment)));
            Log.d(TAG, "Dropbox url:" + url);
            RequestHelper.AUDIO_URL = AudioHelper.makeDownladableLink(url);
            Log.d(TAG, "Dropbox download url:" + RequestHelper.AUDIO_URL);
            List<WordInterval> wordList = WordDetection.detectWordsAutoSensitivity(enrollment, vocabulary.length * 3);
            if (wordList.size() == 0) {
                uploadForIntervals();
                return;
            }
            JSONObject jsonObject = new JSONObject();
            JSONArray intervals = new JSONArray();
            int count = 0;
            try {
                for (WordInterval words :
                        wordList) {
                    JSONObject interval = new JSONObject();
                    if (words.getStopTime() - words.getStartTime() < 600) {
                        interval.accumulate("stop", words.getStartTime() + 601);
                    } else {
                        interval.accumulate("stop", words.getStopTime());
                    }
                    interval.accumulate("start", words.getStartTime());
                    interval.accumulate("phrase", vocabulary[count++ % vocabulary.length]);
                    intervals.put(interval);
                }
                jsonObject.accumulate("intervals", intervals);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //postVerification(jsonObject);
            startEnrollment(jsonObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void uploadForIntervals() {
        Response.Listener<JSONObject> responseListner = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (AudioPinUtil.debug)
                    Toast.makeText(PinActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                try {
                    retryCount = 0;
                    getIntervals(response.getString("taskName"));
                } catch (JSONException e) {
                    Toast.makeText(PinActivity.this, "File Upload to knurld interval is failed", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PinActivity.this, "File Upload to knurld interval is failed", Toast.LENGTH_SHORT).show();
            }
        };
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("audioUrl", RequestHelper.AUDIO_URL);
            jsonObject.accumulate("words", "9");
        } catch (JSONException e) {
            if (AudioPinUtil.debug)
                Toast.makeText(PinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        CustomRequest enrollmentRequest = new CustomRequest(Request.Method.POST, RequestHelper.ANALYTICS_ENDPOINT + "/url", jsonObject.toString(), responseListner, errorListener);

        RequestQueue.submit(this, enrollmentRequest, true);
    }


    private void getIntervals(final String taskName) {
        if (null == taskName || taskName.isEmpty()) {
            Toast.makeText(PinActivity.this, "Invalid task...please retry!", Toast.LENGTH_SHORT).show();
            return;
        }
        Response.Listener<JSONObject> responseListner = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String status = null;
                try {
                    status = response.getString("taskStatus");
                } catch (JSONException e) {
                    if (AudioPinUtil.debug)
                        Toast.makeText(PinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if ("completed".equals(status)) {
                    if (!enrollmentStarted) {
                        enrollmentStarted = true;
                        Log.wtf(TAG, response.toString() + " ; Enrollment Started=" + enrollmentStarted);
                        startEnrollment(response);
                    }
                    return;
                } else {
                    if (AudioPinUtil.debug)
                        Toast.makeText(PinActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    retryCount++;
                    if (retryCount == 10 || "failed".equals(status)) {
                        if (AudioPinUtil.debug)
                            Toast.makeText(PinActivity.this, "Please retry!", Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        return;
                    } else {
                        getIntervals(taskName);
                    }
                }
            }

        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (AudioPinUtil.debug)
                    Toast.makeText(PinActivity.this, "File Upload to knurld interval is failed:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        CustomRequest getInterval = new CustomRequest(Request.Method.GET, RequestHelper.ANALYTICS_ENDPOINT + "/" + taskName, null, responseListner, errorListener);
        RequestQueue.submit(this, getInterval, true);
    }

    private void startEnrollment(final JSONObject _response) {
        Response.Listener<JSONObject> responseListner = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String enrollmentUrl = response.getString("href");
                    if (AudioPinUtil.debug)
                        Toast.makeText(PinActivity.this, "EnrollmentUrl:" + enrollmentUrl, Toast.LENGTH_SHORT).show();
                    postEnrollment(enrollmentUrl, _response);
                } catch (JSONException e) {
                    Toast.makeText(PinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (AudioPinUtil.debug)
                    Toast.makeText(PinActivity.this, "Create enrollment is failed:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        String url = RequestHelper.buildUrl(RequestHelper.ENROLLMENT_ENDPOINT);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("application", RequestHelper.APP_MODEL_URL);
            jsonObject.accumulate("consumer", RequestHelper.getFromPref(PinActivity.this, RequestHelper.CONSUMER_HREF_KEY));
        } catch (JSONException e) {
            Toast.makeText(PinActivity.this, "In create enrollment:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        CustomRequest enrollmentRequest = new CustomRequest(Request.Method.POST, url, jsonObject.toString(), responseListner, errorListener);
        RequestQueue.submit(this, enrollmentRequest, true);
    }



    private void postEnrollment(final String enrollmentUrl, JSONObject response) {
        Response.Listener<JSONObject> responseListner = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(PinActivity.this, "Successfully enrolled voice...", Toast.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf(TAG, "Updated enrollment failed:" + error.getMessage(), error);
                Toast.makeText(PinActivity.this, "Updated enrollment failed:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("enrollment.wav", RequestHelper.AUDIO_URL);
            jsonObject.accumulate("intervals", response.getJSONArray("intervals"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.wtf(TAG, enrollmentUrl + " ->" + jsonObject.toString());
        CustomRequest enrollmentRequest = new CustomRequest(Request.Method.POST, enrollmentUrl, jsonObject.toString(), responseListner, errorListener);
        enrollmentRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue.submit(this, enrollmentRequest, true);
    }
}
