package com.example.myapplication;

import java.math.BigInteger;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.gas.ContractGasProvider;

public class Lottery extends Contract {
    
    public static final String FUNC_GETBALANCE = "getBalance";
    public static final String FUNC_SELECTWINNER = "selectWinner";
    public static final String FUNC_ENTER = "receive"; // This is the payable function

    protected Lottery(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super("", contractAddress, web3j, credentials, contractGasProvider);
    }

    public RemoteCall<BigInteger> getBalance() {
        final Function function = new Function(FUNC_GETBALANCE, 
                java.util.Arrays.<Type>asList(), 
                java.util.Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> selectWinner() {
        final Function function = new Function(
                FUNC_SELECTWINNER, 
                java.util.Arrays.<Type>asList(), 
                java.util.Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> enter(BigInteger value) {
        final Function function = new Function(
                FUNC_ENTER, 
                java.util.Arrays.<Type>asList(), 
                java.util.Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, value);
    }

    public static Lottery load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Lottery(contractAddress, web3j, credentials, contractGasProvider);
    }
}
