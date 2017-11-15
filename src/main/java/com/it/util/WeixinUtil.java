package com.it.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.it.menu.Button;
import com.it.menu.ClickButton;
import com.it.menu.Menu;
import com.it.menu.ViewButton;
import com.it.po.AccessToken;
import com.it.trans.Data;
import com.it.trans.Parts;
import com.it.trans.Symbols;
import com.it.trans.TransResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 微信工具类
 *
 * @author Stephen
 */
@SuppressWarnings("unchecked")
public class WeixinUtil {
    private static final String APPID = "wxb8dee7681564ce25";
    private static final String APPSECRET = "adfbad34c2603e927ac5854090c2f455";
    //获取token的url
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

    private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    private static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";

    private static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";


    /**
     * 输入未知指令时的随机回复
     *
     * @return
     */
    public static String getRandomStr() {
        List<String> jokes = new ArrayList<String>();
        jokes.add("你是在开玩笑的吗,我没听清楚");
        jokes.add("未知指令");
        jokes.add("你又调皮了");
        jokes.add("俺听不懂,说普通话");
        jokes.add("别逗");
        jokes.add("呵呵");
        jokes.add("嘻嘻");
        jokes.add("哈哈");
        jokes.add("你妹");
        jokes.add("客官来啦");
        jokes.add("给你个萝莉");
        jokes.add("给你个御姐");
        jokes.add("请问贵姓");
        jokes.add("你是男是女?");
        int num = jokes.size();
        System.out.println("------------------当前jokes数目:-----------------\n" + num);
        return jokes.get((int) (Math.random() * (num)));
    }


    /**
     * get请求
     *
     * @param url
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static JSONObject doGetStr(String url) throws ParseException, IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        JSONObject jsonObject = null;
        HttpResponse httpResponse = client.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = JSON.parseObject(result);
        }
        return jsonObject;
    }

    /**
     * POST请求
     *
     * @param url
     * @param outStr
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static JSONObject doPostStr(String url, String outStr) throws ParseException, IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);
        JSONObject jsonObject = null;
        httpost.setEntity(new StringEntity(outStr, "UTF-8"));
        HttpResponse response = client.execute(httpost);
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        jsonObject = JSON.parseObject(result);
        return jsonObject;
    }

    /**
     * 文件上传
     *
     * @param filePath
     * @param accessToken
     * @param type
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws KeyManagementException
     */
    public static String upload(String filePath, String accessToken, String type) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在");
        }

        String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);

        URL urlObj = new URL(url);
        //连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        //设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        //设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        //获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        //输出表头
        out.write(head);

        //文件正文部分
        //把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        //结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

        out.write(foot);

        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        String result = null;
        try {
            //定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        JSONObject jsonObj = (JSONObject) JSON.toJSON(result);
        System.out.println(jsonObj);
        String typeName = "media_id";
        if (!"image".equals(type)) {
            typeName = type + "_media_id";
        }
        String mediaId = jsonObj.getString(typeName);
        return mediaId;
    }

    /**
     * 获取accessToken
     *
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static AccessToken getAccessToken() throws ParseException, IOException {
        AccessToken token = new AccessToken();
        String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
        JSONObject jsonObject = doGetStr(url);
        if (jsonObject != null) {
            token.setToken(jsonObject.getString("access_token"));
            token.setExpiresIn(jsonObject.getIntValue("expires_in"));
        }
        return token;
    }

    /**
     * 组装菜单
     *
     * @return
     */
    public static Menu initMenu() {
        Menu menu = new Menu();
        ClickButton button11 = new ClickButton();
        button11.setName("菜单");
        button11.setType("click");
        button11.setKey("11");

        ViewButton button21 = new ViewButton();
        button21.setName("资讯");
        button21.setType("view");
        button21.setUrl("http://www.2345free.com/wordpress/");

        ClickButton button33 = new ClickButton();
        button33.setName("天气查询");
        button33.setType("click");
        button33.setKey("33");

        ClickButton button31 = new ClickButton();
        button31.setName("扫码");
        button31.setType("scancode_push");
        button31.setKey("31");

        ClickButton button32 = new ClickButton();
        button32.setName("地理位置");
        button32.setType("location_select");
        button32.setKey("32");

        Button button = new Button();
        button.setName("工具");
        button.setSub_button(new Button[]{button33, button31, button32});

        menu.setButton(new Button[]{button11, button21, button});
        return menu;
    }

    public static int createMenu(String token, String menu) throws ParseException, IOException {
        int result = 0;
        String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = doPostStr(url, menu);
        if (jsonObject != null) {
            result = jsonObject.getIntValue("errcode");
        }
        return result;
    }

    public static JSONObject queryMenu(String token) throws ParseException, IOException {
        String url = QUERY_MENU_URL.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = doGetStr(url);
        return jsonObject;
    }

    public static int deleteMenu(String token) throws ParseException, IOException {
        String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", token);
        JSONObject jsonObject = doGetStr(url);
        int result = 0;
        if (jsonObject != null) {
            result = jsonObject.getIntValue("errcode");
        }
        return result;
    }

    public static String translate(String source) throws ParseException, IOException {
        String url = "http://api.fanyi.baidu.com/public/2.0/translate/dict/simple?client_id=jNg0LPSBe691Il0CG5MwDupw&q=KEYWORD&from=auto&to=auto";
        url = url.replace("KEYWORD", URLEncoder.encode(source, "UTF-8"));
        JSONObject jsonObject = doGetStr(url);
        String errno = jsonObject.getString("errno");
        Object obj = jsonObject.get("data");
        StringBuffer dst = new StringBuffer();
        if ("0".equals(errno) && !"[]".equals(obj.toString())) {
            TransResult transResult = JSON.toJavaObject(jsonObject, TransResult.class);
            Data data = transResult.getData();
            Symbols symbols = data.getSymbols()[0];
            String phzh = symbols.getPh_zh() == null ? "" : "中文拼音：" + symbols.getPh_zh() + "\n";
            String phen = symbols.getPh_en() == null ? "" : "英式英标：" + symbols.getPh_en() + "\n";
            String pham = symbols.getPh_am() == null ? "" : "美式英标：" + symbols.getPh_am() + "\n";
            dst.append(phzh + phen + pham);

            Parts[] parts = symbols.getParts();
            String pat = null;
            for (Parts part : parts) {
                pat = (part.getPart() != null && !"".equals(part.getPart())) ? "[" + part.getPart() + "]" : "";
                String[] means = part.getMeans();
                dst.append(pat);
                for (String mean : means) {
                    dst.append(mean + ";");
                }
            }
        } else {
            dst.append(translateFull(source));
        }
        return dst.toString();
    }

    public static String translateFull(String source) throws ParseException, IOException {
        String url = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=jNg0LPSBe691Il0CG5MwDupw&q=KEYWORD&from=auto&to=auto";
        url = url.replace("KEYWORD", URLEncoder.encode(source, "UTF-8"));
        JSONObject jsonObject = doGetStr(url);
        StringBuffer dst = new StringBuffer();
        List<Map> list = (List<Map>) jsonObject.get("trans_result");
        for (Map map : list) {
            dst.append(map.get("dst"));
        }
        return dst.toString();
    }

    /**
     * 天气查询
     *
     * @param source
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String getWeather(String city) throws ParseException, IOException {

        String httpUrl = "http://apis.baidu.com/apistore/weatherservice/cityname";
        String httpArg = "cityname=" + city;
        String jsonResult = request(httpUrl, httpArg);
//		System.out.println(jsonResult);
        StringBuffer sb = new StringBuffer();
        JSONObject info = JSON.parseObject(jsonResult);
        if (info.get("errMsg").equals("success")) {
            JSONObject tq = info.getJSONObject("retData");
            sb.append("【" + tq.get("city") + "天气预报】\n");
            sb.append("发布时间:" + tq.get("date") + " " + tq.get("time") + "\n");
            sb.append("天气:" + tq.get("weather") + "\n");
            sb.append("气温:" + tq.get("temp") + "℃\n");
            sb.append("最低气温:" + tq.get("l_tmp") + "℃\n");
            sb.append("最高气温:" + tq.get("h_tmp") + "℃\n");
            sb.append("风向:" + tq.get("WD") + "\n");
            sb.append("风力:" + tq.get("WS") + "\n");
            sb.append("日出时间:" + tq.get("sunrise") + "\n");
            sb.append("日落时间:" + tq.get("sunset") + "\n");

        } else {
            sb.append("未查到\"" + URLDecoder.decode(city, "utf-8") + "\"的城市天气信息,请输入正确的城市名称,如\"上海天气\"");
        }

        return sb.toString();
    }


    /**
     * @param urlAll  :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", "fed3a44d7e10a02a161bd72bf2422a60");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
