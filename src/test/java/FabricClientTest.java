import com.alibaba.fastjson.JSONObject;
import com.hyperledger.simplesdk.*;
import com.hyperledger.simplesdk.chaincode.*;
import com.hyperledger.simplesdk.wallet.WalletConfig;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

public class FabricClientTest {

    private static final String PROJECT_PATH ="/Users/long/blockchain/simplesdk";


    @Test
    public void testInstall() throws Exception{
        InputStream inputStream = FabricClientTest.class.getResourceAsStream("connection.json");
        ConnectionProfile connectionProfile = new ConnectionProfile(inputStream);
        WalletConfig walletConfig = new WalletConfig("test", PROJECT_PATH +"/fabcar/test/cards");
        FabricClient fabricClient = new FabricClient(connectionProfile, walletConfig,"mychannel");
        ChaincodeDefinition chaincodeDefinition = new ChaincodeDefinition("fabcar", "1.0");
        ChaincodeInstallRequest chaincodeInstallRequest  =new ChaincodeInstallRequest();
        chaincodeInstallRequest.setChaincodeDefinition(chaincodeDefinition);
        chaincodeInstallRequest.setChaincodeDir(new File(PROJECT_PATH +"/fabcar/chaincode/fabcar/go/"));
        fabricClient.installChainCode(chaincodeInstallRequest);
    }

    @Test
    public void testInstantiate() throws Exception{
         InputStream inputStream = FabricClientTest.class.getResourceAsStream("connection.json");
        ConnectionProfile connectionProfile = new ConnectionProfile(inputStream);
        WalletConfig walletConfig = new WalletConfig("test", PROJECT_PATH +"/fabcar/test/cards");
        FabricClient fabricClient = new FabricClient(connectionProfile, walletConfig,"mychannel");
        ChaincodeDefinition chaincodeDefinition = new ChaincodeDefinition("fabcar", "1.0");
        ChaincodeInstantiateRequest chaincodeInstantiateRequest  =new ChaincodeInstantiateRequest();
        chaincodeInstantiateRequest.setChaincodeDefinition(chaincodeDefinition);
        chaincodeInstantiateRequest.setEndorsementPolicyFile(new File(PROJECT_PATH +"/src/test/resources/chaincodeendorsementpolicyAllMembers.yaml"));
        fabricClient.instantiateChainCode(chaincodeInstantiateRequest);
    }

    @Test
    public void testInitFarcar() throws Exception {
        InputStream inputStream = FabricClientTest.class.getResourceAsStream("connection.json");
        ConnectionProfile connectionProfile = new ConnectionProfile(inputStream);
        WalletConfig walletConfig = new WalletConfig("test", PROJECT_PATH+"/fabcar/test/cards");
        FabricClient fabricClient = new FabricClient(connectionProfile, walletConfig,"mychannel");
        ChaincodeRequest request = new ChaincodeRequest();
        request.setChaincodeDefinition(new ChaincodeDefinition("fabcar", "1.0"));
        request.setFunction("initLedger");
        fabricClient.submit(request);
    }


    @Test
    public void testQuery() throws Exception {
        InputStream inputStream = FabricClientTest.class.getResourceAsStream("connection.json");
        ConnectionProfile connectionProfile = new ConnectionProfile(inputStream);
        WalletConfig walletConfig = new WalletConfig("test", PROJECT_PATH+"/fabcar/test/cards");
        FabricClient fabricClient = new FabricClient(connectionProfile, walletConfig,"mychannel");
        ChaincodeRequest request = new ChaincodeRequest();
        request.setChaincodeDefinition(new ChaincodeDefinition("fabcar", "1.0"));
        request.setFunction("queryAllCars");
        QueryResult queryResult = fabricClient.query(request);
        System.out.println(JSONObject.toJSONString(queryResult));
    }

}
