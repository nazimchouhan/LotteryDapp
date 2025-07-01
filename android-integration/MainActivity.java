package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.crypto.Credentials;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Web3j web3j;
    private Credentials credentials;
    private Lottery contract;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load contract configuration
        ContractConfig.loadContractInfo(this);

        // Initialize web3j with the configured network URL
        String networkUrl = ContractConfig.getNetworkUrl();
        if (networkUrl == null || networkUrl.isEmpty()) {
            Log.e(TAG, "Network URL is not set!");
            return;
        }
        web3j = Web3j.build(new HttpService(networkUrl));
        String privateKey = "0x5690fe57b2ba96ddc25fe7a5fb61321aa55cd5389eb7a0d2ea4798b0fa5d78f9";
        if (privateKey == null || privateKey.isEmpty()) {
            Log.e(TAG, "Private key is not set!");
            return;
        }
        credentials = Credentials.create(privateKey);

        // Load contract with the deployed address
        String contractAddress = ContractConfig.getContractAddress();
        if (contractAddress == null || contractAddress.isEmpty()) {
            Log.e(TAG, "Contract address is not set!");
            return;
        }
        contract = Lottery.load(
                contractAddress,
                web3j,
                credentials,
                new DefaultGasProvider()
        );

        Log.d(TAG, "Contract loaded at address: " + contractAddress);

        if (findViewById(R.id.btnEnter) != null)
            findViewById(R.id.btnEnter).setOnClickListener(v -> enterLottery());
        if (findViewById(R.id.btnPickWinner) != null)
            findViewById(R.id.btnPickWinner).setOnClickListener(v -> pickWinner());
        updatePot();

        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    // Helper to parse and display user-friendly error messages
    private String getFriendlyErrorMessage(Exception e) {
        String msg = e.getMessage();
        if (msg == null) return "An unknown error occurred.";
        if (msg.contains("insufficient funds")) {
            return "Not enough ETH to enter the lottery.";
        } else if (msg.contains("revert")) {
            if (msg.contains("Only manager can call this function")) {
                return "Only the manager can pick a winner.";
            } else if (msg.contains("Not enough ETH")) {
                return "You must send at least 2 ETH to enter.";
            } else {
                return "Transaction reverted by the contract.";
            }
        } else if (msg.contains("User denied transaction")) {
            return "Transaction was cancelled.";
        } else if (msg.contains("replacement transaction underpriced")) {
            return "Try increasing the gas price.";
        }
        return "An error occurred: " + msg;
    }

    private void enterLottery() {
        executor.submit(() -> {
            try {
                if (contract == null) throw new IllegalStateException("Contract not initialized");
                BigInteger entryAmount = Convert.toWei("2", Convert.Unit.ETHER).toBigInteger();
                TransactionReceipt receipt = contract.enter(entryAmount).send();
                runOnUiThread(() -> {
                    TextView tvStatus = findViewById(R.id.tvStatus);
                    if (tvStatus != null) {
                        tvStatus.setText("Entered lottery!");
                    }
                    updatePot();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error entering lottery: " + e.getMessage());
                String friendlyMsg = getFriendlyErrorMessage(e);
                runOnUiThread(() -> {
                    TextView tvStatus = findViewById(R.id.tvStatus);
                    if (tvStatus != null) {
                        tvStatus.setText(friendlyMsg);
                    }
                });
            }
        });
    }

    private void pickWinner() {
        executor.submit(() -> {
            try {
                if (contract == null) throw new IllegalStateException("Contract not initialized");
                TransactionReceipt receipt = contract.selectWinner().send();
                runOnUiThread(() -> {
                    TextView tvStatus = findViewById(R.id.tvStatus);
                    if (tvStatus != null) {
                        tvStatus.setText("Winner picked!");
                    }
                    updatePot();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error picking winner: " + e.getMessage());
                String friendlyMsg = getFriendlyErrorMessage(e);
                runOnUiThread(() -> {
                    TextView tvStatus = findViewById(R.id.tvStatus);
                    if (tvStatus != null) {
                        tvStatus.setText(friendlyMsg);
                    }
                });
            }
        });
    }

    private void updatePot() {
        executor.submit(() -> {
            try {
                if (web3j == null) throw new IllegalStateException("Web3j not initialized");
                String contractAddress = ContractConfig.getContractAddress();
                if (contractAddress == null || contractAddress.isEmpty()) throw new IllegalStateException("Contract address not set");
                BigInteger pot = web3j.ethGetBalance(contractAddress, DefaultBlockParameterName.LATEST).send().getBalance();
                runOnUiThread(() -> {
                    TextView tvPot = findViewById(R.id.tvPot);
                    if (tvPot != null) {
                        tvPot.setText("Pot: " + Convert.fromWei(pot.toString(), Convert.Unit.ETHER) + " ETH");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error updating pot: " + e.getMessage());
                runOnUiThread(() -> {
                    TextView tvPot = findViewById(R.id.tvPot);
                    if (tvPot != null) {
                        tvPot.setText("Pot: Error");
                    }
                });
            }
        });
    }
}
@Override
protected void onDestroy() {
    super.onDestroy();
    executor.shutdown();
    if (web3j != null) {
        web3j.shutdown();
    }
}