package ru.track;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class App2 {

    public static void main(String[] args) throws Exception {
        HttpResponse<JsonNode> r =  Unirest.post("http://guarded-mesa-31536.herokuapp.com/track")
                .field("name", "Michael")
                .field("github", "mikhan333")
                .field("email", "mikhan333@mail.ru")
                .asJson();
        System.out.println(r.getBody().getObject().get("success"));
    }
}
