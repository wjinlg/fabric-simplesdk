package com.hyperledger.simplesdk.ca;

import com.hyperledger.simplesdk.utils.PemUtils;
import com.hyperledger.simplesdk.channel.FabricUser;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

/**
 * CA 通信客户端
 *
 * @author jinlong
 */
public class CaClient {
    //fabric 内置管理员用户信息
    private static final String adminUsername = "admin";
    private static final String adminPassword = "adminpw";

    private NetworkConfig.CAInfo caInfo;

    public CaClient(NetworkConfig.CAInfo caInfo) {
        this.caInfo = caInfo;
    }

    /**
     * 从CA取得admin授权证书
     *
     * @return 授权用户
     * @throws Exception
     */
    public FabricUser enrollAdmin() throws Exception {
        FabricUser fabricUser = new FabricUser();
        HFCAClient caClient = HFCAClient.createNewInstance(caInfo);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        Enrollment enrollment = caClient.enroll(adminUsername, adminPassword);
        fabricUser.setName(adminUsername);
        fabricUser.setPrivateKey(PemUtils.getPEMString(enrollment.getKey()));
        fabricUser.setSignedCert(enrollment.getCert());
        return fabricUser;
    }

    /**
     * 根据admin授权，新注册用户并授权
     *
     * @param username 用户名
     * @return 授权用户
     * @throws Exception
     */
    public FabricUser registerUser(FabricUser adminUser, String username) throws Exception {
        HFCAClient caClient = HFCAClient.createNewInstance(caInfo);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        FabricUser newUser = new FabricUser();
        newUser.setName(username);
        RegistrationRequest rr = new RegistrationRequest(newUser.getName());
        String enrollmentSecret = caClient.register(rr, adminUser);
        caClient = HFCAClient.createNewInstance(caInfo);
        caClient.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        Enrollment enrollment = caClient.enroll(newUser.getName(), enrollmentSecret);
        String signedCert = enrollment.getCert();
        newUser.setPrivateKey(PemUtils.getPEMString(enrollment.getKey()));
        newUser.setSignedCert(signedCert);
        return newUser;
    }
}
