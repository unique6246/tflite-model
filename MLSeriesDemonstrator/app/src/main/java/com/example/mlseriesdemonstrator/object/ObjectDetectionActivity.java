package com.example.mlseriesdemonstrator.object;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.widget.Toast;
import com.example.mlseriesdemonstrator.helpers.BoxWithText;
import com.example.mlseriesdemonstrator.helpers.MLImageHelperActivity;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ObjectDetectionActivity extends MLImageHelperActivity {

    private Interpreter tflite;
    private List<String> labels;
    private final float THRESHOLD = 0.7f; // Set confidence threshold to consider object detected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // Load the TFLite model and labels
            tflite = new Interpreter(loadModelFile());
            labels = FileUtil.loadLabels(this, "labels.txt");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading model", Toast.LENGTH_SHORT).show();
        }
    }

    // Load the model file from the assets folder
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("my_birds_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    protected void runDetection(Bitmap bitmap) {
        if (tflite == null) {
            getOutputTextView().setText("Model not loaded");
            return;
        }

        // Convert bitmap to TensorImage for inference
        TensorImage inputImageBuffer = new TensorImage();
        inputImageBuffer.load(bitmap);

        // Create output buffer
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, labels.size()}, DataType.FLOAT32);

        // Run inference
        tflite.run(inputImageBuffer.getBuffer(), outputBuffer.getBuffer().rewind());

        // Get results
        float[] outputArray = outputBuffer.getFloatArray();

        // Process results
        List<BoxWithText> detectionResults = new ArrayList<>();
        StringBuilder resultText = new StringBuilder();
        boolean detected = false;

        for (int i = 0; i < outputArray.length; i++) {
            if (outputArray[i] > THRESHOLD) {
                String detectedLabel = labels.get(i);
                resultText.append(detectedLabel).append(": ").append(outputArray[i]).append("\n");
                detected = true;
            }
        }

        if (detected) {
            getOutputTextView().setText("Object detected: \n" + resultText.toString());
        } else {
            getOutputTextView().setText("Cannot detect object. Try again.");
        }
    }
}
