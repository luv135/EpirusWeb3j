package io.luowei.epirus

import org.jsoup.Jsoup
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import x.x.x.utils.HttpRequest
import x.x.x.utils.MyJava


class KouTou {
    //http://cn.toaswap.com/invite/index.html?inviter=0x5C342EEfAFB8e8489b28b63cA4A64A9C62B9d81A
    ///http://cn.toaswap.com/invite/getreward.php?address='+address+'&inviter='+0x5C342EEfAFB8e8489b28b63cA4A64A9C62B9d81A;
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            val w3_ropsten = "https://ropsten.infura.io/v3/c65dc98caf2a4466b2f642011705d15f"
//            val web3j = Web3j.build(HttpService(w3_ropsten))
//            val balance = Convert.fromWei(
//                    web3j.ethGetBalance("0x829bd824b016326a401d083b33d092293333a830", DefaultBlockParameterName.LATEST)
//                            .send().balance.toBigDecimal(),
//                    Convert.Unit.ETHER)
//            println("balance = $balance")
            for (page in 16..100) {
                try {
                    val s = "https://cn.etherscan.com/txs?p=$page"
                    println("page = $page $s")
                    val doc = Jsoup.connect(s).timeout(4000).userAgent("Mozilla").get();

                    val tbody = doc.select("#paywall_mask > table > tbody")[0];
                    for (i in 0 until tbody.childrenSize()) {
                        val address = tbody.child(i).child(5).text()
                        if (address.startsWith("0x")) {
                            println("address = $address")
                            val get = "http://cn.toaswap.com/invite/getreward.php?address=$address&inviter=0x5C342EEfAFB8e8489b28b63cA4A64A9C62B9d81A"
                            HttpRequest.sendGet(get, null)
                            Thread.sleep(1000)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Thread.sleep(5000)
                }
//            for (headline in newsHeadlines) {
//                println(headline)
//            }
                //headline.child(0).child(5).text()
            }
        }
    }
}