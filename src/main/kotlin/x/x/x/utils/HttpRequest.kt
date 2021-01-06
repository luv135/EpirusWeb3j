package x.x.x.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.URL
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

object HttpRequest {
    fun sendGet(url: String, param: String?): String {
        try {
            return sendGetE(url, param)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "exception"
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    @Throws(Exception::class)
    fun sendGetE(url: String, param: String?): String {
        var result = ""
        var `in`: BufferedReader? = null
        val urlNameString = url + if (param == null) "" else "?$param"
        //System.out.println(urlNameString);
        val realUrl = URL(urlNameString)
        // 打开和URL之间的连接
        val connection = realUrl.openConnection()
        // https 忽略证书验证
        if (url.substring(0, 5) == "https") {
            val ctx = MyX509TrustManagerUtils()
            (connection as HttpsURLConnection).sslSocketFactory = ctx!!.socketFactory
            connection.hostnameVerifier = HostnameVerifier { arg0, arg1 -> true }
        }
        // 设置通用的请求属性
        connection.setRequestProperty("accept", "*/*")
        connection.setRequestProperty("connection", "Keep-Alive")
        connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
        // 建立实际的连接
        connection.connect()
        // 获取所有响应头字段
        val map = connection.headerFields
        // 遍历所有的响应头字段
        //for (String key : map.keySet()) {
        //    System.out.println(key + "--->" + map.get(key));
        //}
        // 定义 BufferedReader输入流来读取URL的响应
//        `in` = BufferedReader(InputStreamReader(connection.getInputStream()))
//        var line: String
//        while (`in`.readLine().also { line = it } != null) {
//            result += line
//        }
//        try {
//            `in`?.close()
//        } catch (e2: Exception) {
//            e2.printStackTrace()
//        }
        return result
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    fun sendPost(url: String?, param: String?): String? {
        var out: PrintWriter? = null
        var `in`: BufferedReader? = null
        var result: String? = ""
        try {
            val realUrl = URL(url)
            // 打开和URL之间的连接
            val conn = realUrl.openConnection()
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*")
            conn.setRequestProperty("connection", "Keep-Alive")
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
            // 发送POST请求必须设置如下两行
            conn.doOutput = true
            conn.doInput = true
            // 获取URLConnection对象对应的输出流
            out = PrintWriter(conn.getOutputStream())
            // 发送请求参数
            out.print(param)
            // flush输出流的缓冲
            out.flush()
            // 定义BufferedReader输入流来读取URL的响应
            `in` = BufferedReader(InputStreamReader(conn.getInputStream()))
            var line: String?
            while (`in`.readLine().also { line = it } != null) {
                result += line
            }
        } catch (e: Exception) {
            println("发送 POST 请求出现异常！$e")
            e.printStackTrace()
        } finally {
            try {
                out?.close()
                `in`?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return result
    }

    fun MyX509TrustManagerUtils(): SSLContext? {
        val tm = arrayOf<TrustManager>(MyX509TrustManager())
        var ctx: SSLContext? = null
        try {
            ctx = SSLContext.getInstance("TLS")
            ctx.init(null, tm, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ctx
    }

    /*
     * HTTPS忽略证书验证,防止高版本jdk因证书算法不符合约束条件,使用继承X509ExtendedTrustManager的方式
     */
    internal  class MyX509TrustManager : X509ExtendedTrustManager() {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate>? {
            return null
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String, arg2: Socket) {
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String, arg2: SSLEngine) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String, arg2: Socket) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String, arg2: SSLEngine) {
        }
    }
}