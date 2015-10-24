package net.hoyoung.wfp.stockdown;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.selector.Selectable;

import java.io.File;
import java.io.IOException;

/**
 * Created by hoyoung on 2015/10/24.
 */
public class LocationJsonParse {
    public static void main(String[] args) throws IOException {
        String s = FileUtils.readFileToString(new File("data/content.json")).replace("\\","");
        Json json = new Json(s);



        String status = json.jsonPath("$.status").get();
        String t_lng = json.jsonPath("$.results[0].location.lng").get();
        String t_lat = json.jsonPath("$.results[0].location.lat").get();
        if(!StringUtils.isEmpty(t_lng) && !StringUtils.isEmpty(t_lat)){
            float pos_x = Float.parseFloat(t_lng);
            float pos_y = Float.parseFloat(t_lat);
        }
    }
}
