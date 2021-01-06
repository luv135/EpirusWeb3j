package io.luowei.epirus

import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Transfer
import org.web3j.utils.Convert

class WalletTest {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            val secureRandom = SecureRandom()
//
//            val entropy = ByteArray(12);
//            MnemonicUtils.generateMnemonic()
//
//            secureRandom.nextBytes(entropy)
//            val dir = File("walletDir").apply { mkdir() }
//            val wallet = WalletUtils.generateBip39Wallet("123456", dir)
//            println("mem = ${wallet.mnemonic}")
            WalletTest().initWeb3();
        }
    }

    private fun createWallet() {
//        val byteArray = ByteArray(16)
//        SecureRandom().nextBytes(byteArray)
//
//        val mnemonic = MnemonicUtils.generateMnemonic(byteArray)
//
//        val seed = MnemonicUtils.generateSeed(mnemonic, null)
//
//        val wallet = Bip44WalletUtils.generateBip44Wallet("123456", File("Bip44_wallet"))
//        Bip32ECKeyPair.deriveKeyPair()
        val seed = MnemonicUtils.generateSeed("anger winter either mutual spring achieve wet exhibit turkey enhance leader broom welcome valley note", null)
        val masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed)

        // m/44'/60'/0'/0/0
        val path = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0, 0)
        val bip44KeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path)
//        val bip44KeyPair = Bip44WalletUtils.generateBip44KeyPair(masterKeyPair, false)
        val credentials = Credentials.create(bip44KeyPair)
        println("address = ${credentials.address}")
        //0x9052549e68d4315b44dc16fe02b560ce87d7062f
//        0x1b5b507486620341dd178d9fd95a8c0887c1313b
//        0x1B5B507486620341dD178D9FD95a8C0887c1313b
    }

    var rinkeby = "https://rinkeby.infura.io/v3/ac8fee58ba2744288c3c2199aec75684"

    // eth 钱包私钥 rinkeyby
// "0X8a7d41b5d26125017dbb5fd9b0c1159deb426e176b0647f5ec4bdf05ca22d234";
// 地址
// 0x1CA1b57E4e624536e3915A1824a93c70dF978585
    private fun initWeb3() {
        val web3j = Web3j.build(HttpService(rinkeby))
        val credentials = Credentials.create("0x8a7d41b5d26125017dbb5fd9b0c1159deb426e176b0647f5ec4bdf05ca22d234")
//        println("address = ${credentials.address}")
        val balance = Convert.fromWei(
                web3j.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST)
                        .send().balance.toBigDecimal(),
                Convert.Unit.ETHER)
        println("balance = $balance")
//        Transfer.sendFunds(web3j)
    }


}