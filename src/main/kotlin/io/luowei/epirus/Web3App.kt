package io.luowei.epirus;

import ch.qos.logback.classic.Level
import io.epirus.web3j.Epirus
import io.luowei.epirus.generated.contracts.HelloWorld
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.web3j.crypto.Credentials
import org.web3j.protocol.Network
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger
import kotlin.system.exitProcess

class Web3App {

    private val log: Logger = LoggerFactory.getILoggerFactory().getLogger("org.web3j.protocol.http.HttpService")
    private val NODE_URL = "WEB3J_NODE_URL"
    private val LOCAL_NODE_URL = "http://192.168.1.158:8545"  ////本地的ganache
    private val deployNetwork = Network.valueOf(System.getenv().getOrDefault("EPIRUS_DEPLOY_NETWORK", "rinkeby").toUpperCase())

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Web3App().start(args)
        }
    }

    fun start(args: Array<String>) {
        log as ch.qos.logback.classic.Logger
        log.level = Level.INFO

//        val walletPath = System.getenv("WEB3J_WALLET_PATH")
//        val walletPassword = System.getenv().getOrDefault("WEB3J_WALLET_PASSWORD", "")
//        val credentials: Credentials? = WalletUtils.loadCredentials(walletPassword, Paths.get(walletPath).toFile())
        //直接私钥导入
        val credentials = Credentials.create("a4fa58b6b2a7fa8cef2997a6d7486c62953089b97b07bf9bf393a2292f7680a2");  //直接填写第一个账户的私钥

        val web3j: Web3j = getDeployWeb3j()
        log.info("gas preice = ${web3j.ethGasPrice().send().gasPrice}")
        log.info("balance = ${Convert.fromWei(web3j.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send().balance.toBigDecimal(), Convert.Unit.ETHER)} eth")
//        val helloWorld: HelloWorld? = deployHelloWorld(web3j, credentials, EpirusGasProvider(deployNetwork, GasPrice.High))
        val helloWorld: HelloWorld? = deployHelloWorld(web3j, credentials, StaticGasProvider(BigInteger("4100000000"), BigInteger("2100000")))
//        val helloWorld: HelloWorld? = deployHelloWorld(web3j, credentials, DefaultGasProvider())

        greet(helloWorld!!)
        callGreetMethod(helloWorld)
    }

    private fun getDeployWeb3j(): Web3j {
//        val nodeUrl = System.getenv().getOrDefault(NODE_URL, System.getProperty(NODE_URL))
        val nodeUrl = LOCAL_NODE_URL
        return if (nodeUrl == null || nodeUrl.isEmpty()) {
            Epirus.buildWeb3j(deployNetwork) // deployNetwork = rinkeby/ropsten/mainnet
        } else {
            log.info("Connecting to $nodeUrl")
            Web3j.build(HttpService(nodeUrl))
        }
    }

    @Throws(Exception::class)
    private fun deployHelloWorld(
            web3j: Web3j?,
            credentials: Credentials?,
            contractGasProvider: ContractGasProvider?
    ): HelloWorld? {
        return HelloWorld.load("0xcd02DAac8d0452786e84B884423A1d698196239f", web3j, credentials, contractGasProvider)
//        return HelloWorld.deploy(web3j, credentials, contractGasProvider, "Hello Blockchain World!").send()
    }

    @Throws(Exception::class)
    private fun callGreetMethod(helloWorld: HelloWorld?) {
        log.info("Calling the greeting method of contract HelloWorld")
        val response: String = helloWorld?.greeting()!!.send()
        log.info("Contract returned: $response")

        println(String.format("%-20s", "Contract address") + "https://" + deployNetwork.getNetworkName() + ".epirus.io/contracts/" + helloWorld.contractAddress)
        exitProcess(0)
    }

    private fun greet(helloWorld: HelloWorld) {
        val response = helloWorld.newGreeting("hello 7").send()
        log.info("blockHash = ${response.blockHash}")
    }
}
