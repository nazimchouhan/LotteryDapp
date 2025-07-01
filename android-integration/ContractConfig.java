package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ContractConfig {
    private static final String TAG = "ContractConfig";
    private static final String CONTRACT_INFO_FILE = "contract_info.json";
    
    private static String contractAddress;
    private static String networkUrl;
    private static int chainId;
    
    public static void loadContractInfo(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(CONTRACT_INFO_FILE);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            
            JSONObject jsonObject = new JSONObject(jsonString);
            contractAddress = jsonObject.getString("contractAddress");
            networkUrl = jsonObject.getString("networkUrl");
            chainId = jsonObject.getInt("chainId");
            
            Log.d(TAG, "Contract info loaded - Address: " + contractAddress + ", Network: " + networkUrl);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading contract info: " + e.getMessage());
            e.printStackTrace();
            // Fallback to default values
            contractAddress = "0x0000000000000000000000000000000000000000";
            networkUrl = "http://10.0.2.2:8545";
            chainId = 31337;
        }
    }
    
    public static String getContractAddress() {
        return contractAddress;
    }
    
    public static String getNetworkUrl() {
        return networkUrl;
    }
    
    public static int getChainId() {
        return chainId;
    }
} 