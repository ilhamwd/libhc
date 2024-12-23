package com.dawn.decoderapijni;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.IOException;

public class ScanCamera {
    /* access modifiers changed from: private */
    public static final String TAG = ScanCamera.class.getCanonicalName();
    private static Camera mCamera;
    private static ScanCamera mInstance;
    private SurfaceHolder mSurfaceHolder;
    private SurfaceTexture mSurfaceTexture;

    public static ScanCamera getInstance() {
        Log.d(TAG, "Camera getInstance .... ");
        if (mInstance == null) {
            mInstance = new ScanCamera();
        }
        return mInstance;
    }

    /* access modifiers changed from: protected */
    public int cameraCheckFacing(int facing) {
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        String str = TAG;
        Log.d(str, "Camera num: " + cameraCount);
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return 0;
            }
        }
        return -2;
    }

    public void cameraInit() {
        Log.i(TAG, " cameraInit ++++++++++++++");
    }

    public void cameraOpen(int port, int width, int height) {
        if (mCamera == null) {
            Log.i(TAG, "检测摄像头成功 初始化摄像头 ++++++++++++++");
            try {
                mCamera = Camera.open(port);
                Camera.Parameters params = mCamera.getParameters();
                params.setPreviewSize(width, height);
                mCamera.setParameters(params);
                this.mSurfaceTexture = new SurfaceTexture(10);
                mCamera.setPreviewTexture(this.mSurfaceTexture);
                mCamera.setPreviewCallback(new ScanPreviewCallback());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int cameraClose() {
        Log.e("TAG", "cameraClose: ");
        Camera camera = mCamera;
        if (camera == null) {
            return 0;
        }
        camera.setPreviewCallback((Camera.PreviewCallback) null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.e("TAG", "cameraClose: 2");
        return 0;
    }

    public void cameraStart() {
        if (mCamera != null) {
            Log.i(TAG, "cameraStart ++++++++++++++");
            mCamera.setPreviewCallback(new ScanPreviewCallback());
            mCamera.startPreview();
        }
    }

    public void cameraStop() {
        if (mCamera != null) {
            Log.i(TAG, "cameraStop ++++++++++++++");
            mCamera.stopPreview();
        }
    }

    public void cameraSetSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.mSurfaceHolder = surfaceHolder;
    }

    public class ScanPreviewCallback implements Camera.PreviewCallback {
        public ScanPreviewCallback() {
        }

        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Size size = camera.getParameters().getPreviewSize();
            String access$000 = ScanCamera.TAG;
            Log.i(access$000, "++++++++++++++++onPreviewFrame++++++++++  " + size.height + "  " + size.width + "bufsize : " + data.length);
            SoftEngine.getInstance().setSoftEngineIOCtrlEx(SoftEngine.JNI_IOCTRL_SET_DECODE_IMG, data.length, data);
        }
    }
}
