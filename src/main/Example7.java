package main;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import service.DataHubService;
import util.HashUtil;

public class Example7 {

  public static void main(String[] args) throws Exception {

      System.out.println(DataHubService.getInstance().getSearchResultByQuery("artist", "1", "18", "son tung mtp"));
   //    Player mediaPlayer = Manager.createRealizedPlayer(mediaURL1
  }
public static final String formatTime(long secs) {
    return String.format("%02d:%02d", (secs % 3600) / 60, secs % 60);
}
}
