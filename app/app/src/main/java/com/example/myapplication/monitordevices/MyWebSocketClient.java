package com.example.myapplication.monitordevices;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * TODO [priority = 2/5, difficulity: 2/5] Enhance separation of networking and GUI
 * TODO [priority = 1/5, difficulity: 1/5] Refactor the name of this class
 */
public class MyWebSocketClient extends WebSocketClient {
    private Device device;
    private ImageView streamView;
    private Activity activity;

    public MyWebSocketClient(URI serverUri, Device device, ImageView streamView, Activity activity) {
        super(serverUri);
        this.device = device;
        this.streamView = streamView;
        this.activity = activity;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
        System.out.println(handshakedata.toString());
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);

        try {
            JSONObject receivedJson = new JSONObject(message);
            String messageType = receivedJson.getString("type");
            switch (messageType) {
                case "device_list":
                    System.out.println("Received device list");
                    String deviceToGet = device.getDevice_id().toString().substring(6);
                    deviceToGet = "{ \"type\": \"select_device\", \"device_id\": " + deviceToGet + "}";
                    System.out.println("deviceToGet: " + deviceToGet);
                    JSONObject jsonObject = new JSONObject(deviceToGet);
                    send(jsonObject.toString());
                    break;

                case "error":
                    String errorMessage = receivedJson.getString("message");
                    System.err.println("Received error: " + errorMessage);
                    break;

                default:
                    System.out.println("Unknown message type: " + messageType);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("Received binary message of length: " + message.remaining());
        try {
            handleFrameData(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void handleDeviceData(JSONObject jsonObject) {
        System.out.println("Processing device data: " + jsonObject.toString());
    }

    private void handleFrameData(ByteBuffer message) throws JSONException {
        byte[] frameData = new byte[message.remaining()];
        System.out.println(message.remaining());
        message.get(frameData);
        System.out.println(message);
        System.out.println(frameData.toString());
        activity.runOnUiThread(() -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(frameData, 0, frameData.length);
            if (bitmap != null) {
                streamView.setImageBitmap(bitmap);
            } else {
                System.err.println("Bitmap is null, cannot decode image.");
            }
        });
        System.out.println("Processing frame data of size: " + frameData.length);
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
