/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import constant.Constants;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.apache.commons.lang3.StringUtils;
import util.HashUtil;

/**
 *
 * @author hocgioinhatlop
 */
public class DataHubService {
    private static DataHubService instance;
    public DataHubService() {
    }
    public static DataHubService getInstance() {
        if (instance == null) {
            instance = new DataHubService();
        }
        return instance;
    }
    //API ZING MP3
    //tạo param từ hashmap ex: page=1&encodeId=xxxx...
    private String getParamsString(Map<String, String> params) 
      throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          result.append("=");
          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
          result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
    // dùng get json từ url
    private String getDataFromUrl(String urlStr, Map<String, String> params) throws IOException{

        urlStr = urlStr + getParamsString(params);
        System.out.println(urlStr);
        CookieManager cookieManager = new CookieManager();
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
	connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.100 Safari/537.36");
        String cookiesHeader = connection.getHeaderField("Set-Cookie");
        if (cookiesHeader != null && !cookiesHeader.isEmpty()) {
            List<HttpCookie> cookies = HttpCookie.parse(cookiesHeader);
            cookies.forEach(cookie -> cookieManager.getCookieStore().add(null, cookie));
        }

        connection.disconnect();
        connection = (HttpURLConnection) url.openConnection();
       // connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1");
        connection.setRequestProperty("Cookie",
                StringUtils.join(cookieManager.getCookieStore().getCookies(), ";"));
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Connection", "Keep-Alive");
       // connection.setRequestProperty("Host", "zingmp3.vn");
        //folow to end
        
//Get Response  
        Reader reader = null;
        if ("gzip".equals(connection.getContentEncoding())) {
            reader = new InputStreamReader(new GZIPInputStream(connection.getInputStream()));
        } else {
            reader = new InputStreamReader(connection.getInputStream());
        }
        BufferedReader in = new BufferedReader(reader);
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        } 
        in.close();
        connection.disconnect();
        return content.toString();
    }
    // dùng get slider cho dashboard
    public String getHomeData() {
        try {
            String ctime = getCtime();
            Map<String, String> params = new HashMap<>();
            params.put("page", "1");
            params.put("segmentId", "-1");
            params.put("ctime", ctime);
            params.put("version", constant.Constants.ZING_VERSION);
            params.put("sig", HashUtil.genarateSignature(ctime, "/api/v2/page/get/home", null,"1",null,null));
            params.put("apiKey", Constants.ZING_API_KEY);
            String content = getDataFromUrl("https://zingmp3.vn/api/v2/page/get/home?",params);
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DataHubService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    // dùng get link stream bằng song id
    public String getStreamingUrlBySongId(String songId) {
        try {
            String ctime = getCtime();
            System.out.println();
            Map<String, String> params = new HashMap<>();
            params.put("id", songId);
            params.put("ctime", ctime);
            params.put("version", constant.Constants.ZING_VERSION);
            params.put("sig",HashUtil.genarateSignature(ctime, "/api/v2/song/get/streaming", songId, null,null,null));
            params.put("apiKey", Constants.ZING_API_KEY);
            String content = getDataFromUrl("https://zingmp3.vn/api/v2/song/get/streaming?", params);
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DataHubService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    // dùng get data hot song 15 bài hát cho dashboard
    public String getHubDetail() {
        try {
            String ctime = getCtime();
            System.out.println();
            Map<String, String> params = new HashMap<>();
            params.put("id", "IWZ9Z087");
            params.put("ctime", ctime);
            params.put("version", Constants.ZING_VERSION);
            params.put("sig", HashUtil.genarateSignature(ctime, "/api/v2/page/get/hub-detail", "IWZ9Z087",null,null,null));
            params.put("apiKey", Constants.ZING_API_KEY);
            String content = getDataFromUrl("https://zingmp3.vn/api/v2/page/get/hub-detail?",params);
            System.out.println(content.toString());
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DataHubService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private String getCtime() {
        Long datetime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(datetime);
        return String.valueOf(Math.round(timestamp.getTime() / 1e3));
    }
    //dùng để dowload bài hát từ link stream 
    private void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
    public String getArtistByAlias(String alias) {
        try {
            String ctime = getCtime();
            System.out.println();
            Map<String, String> params = new HashMap<>();
            params.put("alias", alias);
            params.put("ctime", ctime);
            params.put("version", constant.Constants.ZING_VERSION);
            params.put("sig",HashUtil.genarateSignature(ctime, "/api/v2/page/get/artist", null, null,null,null));
            params.put("apiKey", Constants.ZING_API_KEY);
            String content = getDataFromUrl("https://zingmp3.vn/api/v2/page/get/artist?", params);
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DataHubService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String getLyricBySongId(String id) {
        try {
            String ctime = getCtime();
            System.out.println();
            Map<String, String> params = new HashMap<>();
            params.put("media_id", id);
            String content = getDataFromUrl("https://m.zingmp3.vn/xhr/lyrics/get-lyrics?", params);
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DataHubService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String getInfoSongBySongId(String songId) {
        try {
            String ctime = getCtime();
            Map<String, String> params = new HashMap<>();
            params.put("id", songId);
            params.put("ctime", ctime);
            params.put("version", constant.Constants.ZING_VERSION);
            params.put("sig",HashUtil.genarateSignature(ctime, "/api/v2/song/get/info", songId, null,null,null));
            params.put("apiKey", Constants.ZING_API_KEY);
            String content = getDataFromUrl("https://zingmp3.vn/api/v2/song/get/info?", params);
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DataHubService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String getKeywordByQuery(String query) {
        try {
            String ctime = getCtime();
            Map<String, String> params = new HashMap<>();
            params.put("num", "10");
            params.put("query", query);
            params.put("language", "vi");
            params.put("ctime", ctime);
            params.put("version", constant.Constants.ZING_VERSION);
            params.put("sig",HashUtil.genarateSignature(ctime, "/v1/web/suggestion-keywords", null, null,null,null));
            params.put("apiKey", Constants.ZING_API_KEY);
            String content = getDataFromUrl("https://ac.zingmp3.vn/v1/web/suggestion-keywords?", params);
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DataHubService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public String getSearchResultByQuery(String type,String page,String count,String query) {
        try {
            String ctime = getCtime();
            Map<String, String> params = new HashMap<>();
            params.put("q", query);
            params.put("type", type);
            params.put("page", page);
            params.put("count", count);
            params.put("ctime", ctime);
            params.put("version", constant.Constants.ZING_VERSION);
            params.put("sig",HashUtil.genarateSignature(ctime, "/api/v2/search", null, page,type,count));
            params.put("apiKey", Constants.ZING_API_KEY);
            String content = getDataFromUrl("https://zingmp3.vn/api/v2/search?", params);
            return content.toString();
        } catch (IOException ex) {
            Logger.getLogger(DataHubService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
