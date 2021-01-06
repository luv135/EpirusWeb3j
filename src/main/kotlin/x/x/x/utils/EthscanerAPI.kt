//package x.x.x.utils
//
//
//
//object EthscanerAPI {
//    //-------------------------------------------------------------------------------------------------------
//    //api-ropsten.etherscan.io
//    //api-kovan.etherscan.io
//    //api-rinkeby.etherscan.io
//    val ip = "https://api-cn.etherscan.com"
//    var apikey = "CI4AX657QB1TNM2NPBQJZWX55XDSMA3AEM"
//
//    //查询余额
//    //多地址
//    fun getEthBalance(address: String, block: (String) -> Unit) {
//        val url =
//            "${ip}/api?module=account&action=balance&address=${address}&tag=latest&apikey=${apikey}"
//        val response = httpGet(url)
//        try {
//            val jsonObject = org.json.JSONObject(response)
//            val result = jsonObject.getString("result")
//            block(result)
//        } catch (e: Exception) {
//        }
//    }
//
//    fun getTokenBalance(address: String, coinAddress: String, block: (String) -> Unit) {
//        val url =
//            "https://api-cn.etherscan.com/api?module=account&action=tokenbalance&contractaddress=${coinAddress}&address=${address}&tag=latest&apikey=${apikey}"
//        val response = httpGet(url)
//        try {
//            val jsonObject = org.json.JSONObject(response)
//            val result = jsonObject.getString("result")
//            block(result)
//        } catch (e: Exception) {
//        }
//    }
//
//    //获取区块高度 16进制 需要转成10进制
//    fun getHeight(): String {
//        return httpGet("${ip}/api?module=proxy&action=eth_blockNumber&apiKey=${apikey}")
//    }
//
//    //获取eth交易记录
//    fun queryAddress(address: String): MutableList<String> {
//        val list = mutableListOf<String>()
//        val response =
//            httpGet("${ip}/api?module=account&action=txlist&address=${address}&startblock=11598380&endblock=11598390&sort=asc&apiKey=${apikey}")
//        //代币交易记录
//        //val tokenResponse = httpGet("${ip}/api?module=account&action=tokentx&address=${address}&apiKey=${apikey}")
//        //println(response)
//        val jsonObject = JSONObject(response)
//        val jsonArray = jsonObject.getJSONArray("result")
//        for (i in 0 until jsonArray.length()) {
//            val itemObject = jsonArray.getJSONObject(i)
//            val to = itemObject.getString("to")
//            list.add(to)
//            println(to)
//        }
//        print(response)
//        return list
//    }
//
//    //-----------------------------------------------------
//    private val isTest = true
//    private val ethplorerIP =
//        if (isTest) "https://kovan-api.ethplorer.io/" else "https://api.ethplorer.io/"
//    private val ethplorerAPIKey = "freekey"
//
//    //getLastBlock getTokenInfo getAddressInfo  getTxInfo getTokenHistory getAddressHistory getAddressTransactions
//    //getTopTokenHolders
//    //获取某个代币的信息
//    fun getTokenInfo(coinAddress: String) {
//        val url = "${ethplorerIP}getTokenInfo/${coinAddress}?apiKey=${ethplorerAPIKey}"
//        val response = httpGet(url)
//        println(response)
//    }
//
////    //获取本地址的代币信息
////    fun getTokenAddress(address: String): AddressToken {
////        val list = mutableListOf<AddressTokenInfo>()
////        val url = "${ethplorerIP}getAddressInfo/${address}?apiKey=${ethplorerAPIKey}"
////        val response = httpGet(url)
////        try {
////            println("$url \n $response")
////            val jsonObject = JSONObject(response)
////            val ethObject = jsonObject.getJSONObject("ETH")
////            val ethBalance = ethObject.getDouble("balance")
////            ETHUtils.printlnLog2File("$address  -> ETH[$ethBalance]")
////            if (jsonObject.has("tokens")) {
////                val tokensArray = jsonObject.getJSONArray("tokens")
////                for (i in 0 until tokensArray.length()) {
////                    val tokenJSONObject = tokensArray.getJSONObject(i)
////                    val tokenInfo = tokenJSONObject.getJSONObject("tokenInfo")
////                    val coinAddress = tokenInfo.getString("address")
////                    val symbol = if (tokenInfo.has("symbol")) tokenInfo.getString("symbol") else coinAddress
////                    val decimals = tokenInfo.getString("decimals")
////                    val balance = tokenJSONObject.getLong("balance")
////                    val addressTokenInfo = AddressTokenInfo(
////                        coinAddress = coinAddress,
////                        decimals = decimals,
////                        tokenBalance = balance.toString()
////                    )
////                    ETHUtils.printlnLog2File("$address [${coinAddress}] $decimals -> $symbol[$balance]")
////                    list.add(addressTokenInfo)
////                }
////            }
////            return AddressToken(ethBalance = ethBalance.toString(), listToken = list)
////        } catch (e: Exception) {
////            e.printStackTrace()
////        }
////        return AddressToken("0", list)
////    }
//
//    fun httpGet(url: String): String {
//        return HttpRequest.sendGet(url, null)
//    }
//}