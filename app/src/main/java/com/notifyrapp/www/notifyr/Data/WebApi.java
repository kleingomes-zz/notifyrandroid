package com.notifyrapp.www.notifyr.Data;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.notifyrapp.www.notifyr.Model.Article;
/**
 * Created by K on 11/4/2016.
 */

public class WebApi {

    private String apiBaseUrl = "http://www.notifyr.ca/service/api/";

    public List<Article> GetArticles(int skip, int take,String sortBy, int itemTypeId )
    {
     //   Map<String,Object> map = new ObjectMapper().readValue(new URL("http://dot.com/api/?customerId=1234").openStream(), Map.class);
        List<Article> articles = new ArrayList<Article>();
        Article a = new Article();
        a.setId(2321);
        a.setTitle("Apple Releases new iPhone");
        a.setSource("CNN");
        articles.add(a);

        return articles;
    }

}
