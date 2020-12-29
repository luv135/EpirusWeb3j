package io.luowei.epirus

import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Bip44WalletUtils
import org.web3j.crypto.Credentials
import org.web3j.crypto.MnemonicUtils

/*
//根据Path    m/44'/60'/0'/0 加载私钥和地址 采用coinomi，ledger格式
    public static Credentials loadBip44Wallet(String mnemonic, int index) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seed);
        final int[] path = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, index};
        Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, path);
        Credentials credentials = Credentials.create(bip44Keypair);
        printCredentials(credentials);
        return credentials;
    }
* */
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
            WalletTest().createWallet();

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
        val path = intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT, 60 or Bip32ECKeyPair.HARDENED_BIT, 0 or Bip32ECKeyPair.HARDENED_BIT, 0 ,0)
        val bip44KeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path)
//        val bip44KeyPair = Bip44WalletUtils.generateBip44KeyPair(masterKeyPair, false)
        val credentials = Credentials.create(bip44KeyPair)
        println("address = ${credentials.address}")
        //0x9052549e68d4315b44dc16fe02b560ce87d7062f
//        0x1b5b507486620341dd178d9fd95a8c0887c1313b
//        0x1B5B507486620341dD178D9FD95a8C0887c1313b

    }
}