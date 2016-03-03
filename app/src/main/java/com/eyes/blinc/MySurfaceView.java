package com.eyes.blinc;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

/**
 * Created by Ian on 08-Feb-16.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final Resources resources;
    Camera.Parameters parameters;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private MediaRecorder mMediaRecorder;
    private boolean mInitSuccesful;
    private int count;

    public MySurfaceView(Context context, Resources resources) {
        super(context);
        this.resources = resources;

        count = 0;

        getHolder().addCallback(this);
        mHolder = getHolder();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (!mInitSuccesful)
                initRecorder(mHolder.getSurface());
            mMediaRecorder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, int format, int width,
                               int height) {
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            public void onPreviewFrame(byte[] _data, Camera _camera) {
                count++;
                if (count == 180) {
                    mMediaRecorder.stop();
                    shutdown();
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        shutdown();
    }

    public void shutdown() {
        // Release MediaRecorder and especially the Camera as it's a shared
        // object that can be used by other applications
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.stopPreview();
        mCamera.setPreviewCallback(null);
        mCamera.release();
        mCamera = null;
        this.getHolder().removeCallback(this);

        Intent myIntent = new Intent(super.getContext(), ImageActivity.class);
        super.getContext().startActivity(myIntent);
    }

    private void configure(Camera camera) {
        parameters = mCamera.getParameters();

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        camera.setParameters(parameters);
    }

    private void initRecorder(Surface surface) throws IOException {
        // It is very important to unlock the camera before doing setCamera
        // or it will results in a black preview
        if (mCamera == null) {
            mCamera = Camera.open(1);
            configure(mCamera);
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(1, info);
            Log.i("can disable sound", " = " + info.canDisableShutterSound);
            if (info.canDisableShutterSound) {
                mCamera.enableShutterSound(false);
            }
            mCamera.unlock();
        }

        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setPreviewDisplay(surface);

        mMediaRecorder.setCamera(mCamera);


        Method[] methods = mMediaRecorder.getClass().getMethods();
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoFrameRate(18);
        mMediaRecorder.setVideoSize(720, 480);

        for (Method method: methods){
            try{
                if(method.getName().equals("setAudioEncodingBitRate")){
                    method.invoke(mMediaRecorder,12200);
                }
                else if(method.getName().equals("setVideoEncodingBitRate")){
                    method.invoke(mMediaRecorder, 3000000);
                }
                else if(method.getName().equals("setAudioSamplingRate")){
                    method.invoke(mMediaRecorder,8000);
                }
                else if(method.getName().equals("setVideoFrameRate")){
                    method.invoke(mMediaRecorder,24);
                }
            }catch (IllegalArgumentException e) {

                e.printStackTrace();
            } catch (IllegalAccessException e) {

                e.printStackTrace();
            } catch (InvocationTargetException e) {

                e.printStackTrace();
            }
        }

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");

        mMediaRecorder.setOutputFile(file.getAbsolutePath());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // This is thrown if the previous calls are not called with the
            // proper order
            e.printStackTrace();
        }

        mInitSuccesful = true;
    }
}
