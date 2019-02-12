package com.hyperledger.simplesdk;

import com.hyperledger.simplesdk.chaincode.ChaincodeInstallRequest;
import com.hyperledger.simplesdk.chaincode.ChaincodeInstantiateRequest;
import com.hyperledger.simplesdk.chaincode.ChaincodeRequest;
import com.hyperledger.simplesdk.chaincode.TransactionResult;
import com.hyperledger.simplesdk.channel.ChannelClient;
import com.hyperledger.simplesdk.wallet.WalletConfig;
import com.hyperledger.simplesdk.wallet.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * SimpleSdk 暴露的超级账本客户端
 *
 * @author jinlong
 */
public class FabricClient {


    private static final Logger logger = LoggerFactory.getLogger(FabricClient.class);


    private ChannelClient peerAdminChannelClient;
    private ChannelClient userChannelClient;


    public FabricClient(ConnectionProfile connectionProfile, WalletConfig walletConfig, String channelName) throws Exception {
        WalletRepository walletRepository = new WalletRepository(
                walletConfig,
                connectionProfile.getNetworkConfig().getClientOrganization());
        peerAdminChannelClient = new ChannelClient(connectionProfile.getPeerAdmin(), connectionProfile, channelName);
        userChannelClient = new ChannelClient(walletRepository.registerUser(), connectionProfile, channelName);
    }

    public void installChainCode(ChaincodeInstallRequest chaincodeInstallRequest) {
        peerAdminChannelClient.installChainCode(chaincodeInstallRequest.getChaincodeDefinition(),
                chaincodeInstallRequest.getChaincodeDir());
    }

    public void instantiateChainCode(ChaincodeInstantiateRequest chaincodeInstantiateRequest) {
        ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
        chaincodeRequest.setFunction("init");
        chaincodeRequest.setArgumentList(chaincodeInstantiateRequest.getInitArguments());
        chaincodeRequest.setChaincodeDefinition(chaincodeInstantiateRequest.getChaincodeDefinition());
        peerAdminChannelClient.instantiateChainCode(chaincodeRequest, chaincodeInstantiateRequest.getEndorsementPolicyFile());

    }

    public TransactionResult query(ChaincodeRequest chaincodeRequest) {
        return userChannelClient.query(chaincodeRequest);
    }

    public TransactionResult submit(ChaincodeRequest chaincodeRequest) {
        return userChannelClient.submit(chaincodeRequest);
    }

    public void upgradeChainCode(ChaincodeInstantiateRequest chaincodeInstantiateRequest) {
        ChaincodeRequest chaincodeRequest = new ChaincodeRequest();
        chaincodeRequest.setFunction("init");
        chaincodeRequest.setArgumentList(new ArrayList<>());
        chaincodeRequest.setChaincodeDefinition(chaincodeInstantiateRequest.getChaincodeDefinition());
        peerAdminChannelClient.upgradeChainCode(chaincodeRequest, chaincodeInstantiateRequest.getEndorsementPolicyFile());
    }
}
