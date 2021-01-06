package x.x.x.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;


//ETH操作脚本
//打印16进制
//System.out.println("seed = " + Numeric.toHexString(toSeed));
//System.out.println("seed: " + new BigInteger(1, toSeed).toString(16));
public class ETHUtils {

    //随机生成12位助记词
//    public static List<String> randomMnemonic() throws Exception {
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
//        secureRandom.nextBytes(entropy);
//        return MnemonicCode.INSTANCE.toMnemonic(entropy);
//    }

//    private String generateMnemonics() {
//        byte[] initialEntropy = new byte[16];
//        SecureRandomUtils.secureRandom().nextBytes(initialEntropy);
//        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
//    }

    //根据私钥生成地址
    public static String getETHAddressFromPrivateKey(String privateKey) throws Exception {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        WalletFile walletFile = Wallet.createStandard("", ecKeyPair);
        String address = "0x" + walletFile.getAddress();
        printlnLog(address);
        return address;
    }

    /**
     * The path is a human-friendly representation of the deterministic path. For example:
     * <p>
     * "44H / 0H / 0H / 1 / 1"
     * <p>
     * Where a letter "H" means hardened key. Spaces are ignored.
     * M/44'/60'/0'/0/0
     */
    public static List<ChildNumber> parsePath(String path) {
        String[] parsedNodes = path.replace("M", "").split("/");
        List<ChildNumber> nodes = new ArrayList<ChildNumber>();
        for (String n : parsedNodes) {
            n = n.replaceAll(" ", "");
            if (n.length() == 0) continue;
            boolean isHard = n.endsWith("'");
            if (isHard) n = n.substring(0, n.length() - 1);
            int nodeNumber = Integer.parseInt(n);
            nodes.add(new ChildNumber(nodeNumber, isHard));
        }
        return nodes;
    }

    //加载兼容imtoken 比特派 matemask等钱包的格式  //TODO 优化生成1000个地址
    public static void loadBip44Address(String mnemonic) throws Exception {
        loadBip44Address(mnemonic, 0);
    }

    //会打印log4f日志
    public static String loadBip44Address(String mnemonic, int index) throws Exception {
        byte[] toSeed = MnemonicUtils.generateSeed(mnemonic, "");
        DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonic, toSeed, "", 0);
        DeterministicKeyChain deterministicKeyChain = DeterministicKeyChain.builder().seed(deterministicSeed).build();
        BigInteger key = deterministicKeyChain.getKeyByPath(ETHUtils.parsePath("M/44'/60'/0'/0/" + index), true).getPrivKey();
        ECKeyPair privateKey = ECKeyPair.create(key);
        System.out.println("0x" + Keys.getAddress(privateKey) + " -> [0x" + privateKey.getPrivateKey().toString(16) + "]");
        return "0x" + Keys.getAddress(privateKey);
    }
    // ========================================================================================================================
    //https://github.com/Catherinelhl/CrateWalletDemo/blob/c627dbb1bf75536c16fdd9d2377b1918d974bd12/app/src/main/java/io/catherine/cratewallet/tool/ecc/KeyTool.java

    /**
     * Checks if the given String is a valid Bitcoin address.
     * ^5[HJK][1-9A-Za-z][^OIl]{49}  私钥正则
     * <p>
     * address The address to check
     * True, if the String is a valid Bitcoin address, false otherwise
     */
//    public static boolean validateBitcoinAddress(String address) {
//
//        if (address == null) {
//            return false;
//        }
//
//        // Check the length
//        if (address.length() < 26 || address.length() > 35) {
//            return false;
//        }
//        byte[] addressBytes = Base58.decode(address);
//
//        // Check the version byte
//        if (addressBytes[0] != 0) {
//            return false;
//        }
//
//        MessageDigest md;
//        try {
//            md = MessageDigest.getInstance("SHA-256");
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("BitcoinUtils.validateBitcoinAddress: SHA-256 digest not found");
//        }
//
//        md.update(addressBytes, 0, 21);
//        byte[] sha256Hash = md.digest();
//        sha256Hash = md.digest(sha256Hash);
//
//        byte[] addressChecksum = Arrays.copyOfRange(addressBytes, 21, addressBytes.length);
//        byte[] calculatedChecksum = Arrays.copyOfRange(sha256Hash, 0, 4);
//        return Arrays.equals(addressChecksum, calculatedChecksum);
//    }

    //钱包文件操作////////////////////////////////////////////////////////////////////////
    //创建一个钱包文件
    public static String createWallet(String password, String walletFilePath) throws Exception {
        String walletFileName = WalletUtils.generateNewWalletFile(password, new File(walletFilePath), false);
        printlnLog("创建成功的钱包文件：" + walletFileName);
        return walletFileName;
    }

    //加载钱包文件
    public static Credentials loadWallet(String password, String walletFilePath) throws Exception {
        Credentials credentials = WalletUtils.loadCredentials(password, walletFilePath);
        printCredentials(credentials);
        return credentials;
    }

    //加载钱包私钥
    public static String loadPrivateKey(Credentials credentials) {
        return credentials.getEcKeyPair().getPrivateKey().toString(16);
    }

    //通过私钥加载钱包
    public static Credentials loadPrivateKey(String privateKey) {
        return Credentials.create(privateKey);
    }

    public static Credentials loadFile(String filePath) throws Exception {
        return WalletUtils.loadCredentials("", filePath);
    }


    //生成json文件格式
    public static String createJsonWallet(String password) {
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            WalletFile walletFile = Wallet.createStandard(password, ecKeyPair);
            printlnLog("address = " + "0x" + walletFile.getAddress());
            return ObjectMapperFactory.getObjectMapper().writeValueAsString(walletFile);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "";
    }

    public static Credentials loadJsonWallet(String password, String jsonFileContent) throws Exception {
        Credentials credentials = WalletUtils.loadJsonCredentials(password, jsonFileContent);
        printCredentials(credentials);
        return credentials;
    }

    //解析json钱包
    public static String decryptWallet(String keystore, String password) throws Exception {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        try {
            WalletFile walletFile = objectMapper.readValue(keystore, WalletFile.class);
            ECKeyPair ecKeyPair = Wallet.decrypt(password, walletFile);

            String privateKey = ecKeyPair.getPrivateKey().toString(16);
            printlnLog("privateKey = " + privateKey);

            String publicKey = ecKeyPair.getPublicKey().toString(16);
            printlnLog("publicKey = " + publicKey);
            return privateKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //生成bip39的钱包。就会有助记词  备份 keystore  私钥(助记词可不备份)
    public static void generateBip44Wallet() throws Exception {
        Bip39Wallet bip39Wallet = Bip44WalletUtils.generateBip44Wallet("", new File("key"));
        System.out.println("文件名：" + bip39Wallet.getFilename());
        System.out.println("助记词：[" + bip39Wallet.getMnemonic() + "]");
        loadBip44Wallet(bip39Wallet.getMnemonic());
        //loadBip44Address(bip39Wallet.getMnemonic());
    }

    //加载钱包 采用coinomi，ledger
    public static Credentials loadBip44Wallet(String mnemonic) {
        Credentials credentials = Bip44WalletUtils.loadBip44Credentials("", mnemonic);
        printCredentials(credentials);
        return credentials;
    }

    //根据Path    m/44'/60'/0'/0/0 加载私钥和地址
    public static Credentials loadBip44Wallet(String mnemonic, int index) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        final int[] path = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0, index};
        Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
        Credentials credentials = Credentials.create(bip44Keypair);
        printCredentials(credentials);
        return credentials;
    }

    /**
     * ETH转账
     *
     * @param web3j
     * @param fromPrivateKey 发送方私钥
     * @param to             接收方地址
     * @param amount         转账数量
     */
    public static String signETHTransaction(Web3j web3j, String fromPrivateKey, String to, String amount) throws Exception {
        printlnLog2File("开始ETH转账-----------------------------" + now());
        String from = Credentials.create(fromPrivateKey).getAddress();
        printlnLog2File("转账地址:" + from + "  收款地址:" + to + " [" + amount + "]");
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = new BigInteger("210000");
        printlnLog2File("none:" + nonce + "   gasPrice:" + gasPrice + "  gasLimit:" + gasLimit);
        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        //签名
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amountWei, "");
        printlnLog("Raw:" + rawTransaction.getData());
        Credentials credentials = Credentials.create(fromPrivateKey);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        //printlnLog("sign:" + Numeric.toHexString(signMessage));
        //广播交易
        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
        printlnLog2File("结束ETH转账-----------------------------" + now() + "  hash:" + hash + "\n");
        return hash;
    }

    /**
     * ERC20 代币转帐
     *
     * @param web3j
     * @param coinAddress    代币合约地址
     * @param fromPrivateKey 发送方的私钥
     * @param to             接收方地址
     * @param amount         转账数量
     * @throws Exception
     */
    public static String signTokenTransaction(Web3j web3j, String coinAddress, String fromPrivateKey, String
            to, String amount) throws Exception {
        printlnLog2File("开始ERC20代币[" + coinAddress + "]转账-----------------------------" + now());
        String from = Credentials.create(fromPrivateKey).getAddress();
        printlnLog2File("转账地址:" + from + "  收款地址:" + to + " [" + amount + "]");
        //查询地址交易编号
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        //TODO 测试如果第一笔交易失败，第二笔重放
        //BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        //支付的矿工费
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = new BigInteger("210000");
        printlnLog2File("none:" + nonce + "   gasPrice:" + gasPrice + "  gasLimit:" + gasLimit);
        Credentials credentials = Credentials.create(fromPrivateKey);
        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        //封装转账交易 //TODO 批量交易
        Function function = new Function("transfer", Arrays.<Type>asList(new Address(to), new Uint256(amountWei)), Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        //签名交易
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        //广播交易
        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
        printlnLog2File("结束ERC20转账-----------------------------" + now() + "  hash:" + hash + "\n");
        return hash;
    }

    /**
     * 自定义手续费
     *
     * @param gasPrice
     * @return
     */
    public static BigInteger sendCustomGasPrice(String gasPrice) {
        return Convert.toWei(gasPrice, Convert.Unit.GWEI).toBigInteger();
    }

    public static String wei2GasPrice(String gasPrice) {
        return new BigDecimal(gasPrice).divide(BigDecimal.valueOf(Math.pow(10, 9))).toPlainString();
    }

    //将以太坊链上余额转成human 单位
    public static String wei2ETHER(String balance) {
        return Convert.fromWei(balance, Convert.Unit.ETHER).toPlainString();
    }

    //将erc20代币转成human单位
    public static String wei2Token(String balance, double decimals) {
        return new BigDecimal(balance).divide(BigDecimal.valueOf(Math.pow(10, decimals))).toPlainString();
    }

    //砍掉小数部分，不进行四舍五入
    public static String cutTail(String number, double decimals) {
        double a = Double.parseDouble(number) * Math.pow(10, decimals);
        return String.valueOf(Math.floor(a) / Math.pow(10, decimals));
    }

    private static void sendETH() {
        //Transaction transaction = Transaction.createEtherTransaction(fromAddr, nonce, gasPrice, null, toAddr, value);
    }

    private void sendToken() {
        //Transaction transaction = Transaction.createFunctionCallTransaction(fromAddr, nonce, gasPrice, null, contractAddr, funcABI);
    }

    //获取eth余额
    public static BigInteger getETHBalance(Web3j web3j, String address) throws Exception {
        BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        BigDecimal amount = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
        System.out.println(address + " " + wei2Token(balance.toString(), 18) + "  " + amount);
        return balance;
    }

    /**
     * 获取XX地址的token余额
     *
     * @param web3j
     * @param coinAddress 代币合约地址
     * @param address     需要查询余额的地址
     */
    public static String getTokenBalance(Web3j web3j, String coinAddress, String address) throws Exception {
        List inputParameters = new ArrayList<>();
        List stringParameters = Arrays.asList(new TypeReference<Utf8String>() {
        });
        List bigIntegerParameters = Collections.singletonList(new TypeReference<Uint256>() {
        });
        //String name = queryToken(web3j, String.class, "name", inputParameters, stringParameters, address, tokenAddress);
        //System.out.println("token-name:" + name);
        String symbol = queryToken(web3j, String.class, "symbol", inputParameters, stringParameters, address, coinAddress);
        System.out.println("token-symbol:" + symbol);
        BigInteger decimals = queryToken(web3j, BigInteger.class, "decimals", inputParameters, bigIntegerParameters, address, coinAddress);
        System.out.println("token-decimals:" + decimals);
        BigInteger totalSupply = queryToken(web3j, BigInteger.class, "totalSupply", inputParameters, bigIntegerParameters, address, coinAddress);
        System.out.println("token-totalSupply:" + wei2Token(totalSupply.toString(), Integer.parseInt(decimals.toString())));
        BigInteger banalce = queryToken(web3j, BigInteger.class, "balanceOf", Collections.singletonList(new Address(address)), bigIntegerParameters, address, coinAddress);
        if (banalce == null) {
            banalce = BigInteger.ZERO;
        }
        System.out.println(String.format("address:%s, token-balance:%s", address, wei2Token(banalce.toString(), decimals.intValue())));
        return wei2Token(banalce.toString(), decimals.intValue());
    }

    /**
     * TODO: 代币执行事务
     *
     * @param t
     * @param methodName       执行方法名称
     * @param inputParameters  输入参数
     * @param outputParameters 输出参数类型
     * @param from             执行地址
     * @param to               合约地址
     * @param <T>              返回参数
     * @return
     */
    private static <T> T queryToken(Web3j web3j, Class<T> t, String methodName, List<Type> inputParameters, List<TypeReference<?>> outputParameters, String from, String to) {
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        //智能合约事务
        Transaction transaction = Transaction.createEthCallTransaction(from, to, data);
        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            return (T) results.get(0).getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printlnLog(String log) {
        System.out.println(log);
    }

//    public static void printlnLog2File(String log) {
//        printlnLog(log);
//        if (!new File("log").exists()) new File("log").mkdir();
//        FileUtils.writeFile("log/log.txt", log + "\n", true);
//    }
//
//    public static void printlnError2File(String error) {
//        System.err.println("-----------------------------------------" + error);
//        FileUtils.writeFile("log/error.txt", error + "\n", true);
//    }
//
//    public static void printCredentials(Credentials credentials) {
//        String address = credentials.getAddress();
//        //String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
//        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
//        if (privateKey.length() == 63) privateKey = "0" + privateKey;
//        System.out.println(address + " -> [0x" + privateKey + "]");
//        QRCodeUtils.createQRCode(privateKey, "./key/img/" + address + ".png", 300, 300);
//    }

    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(System.currentTimeMillis()));
    }
}
