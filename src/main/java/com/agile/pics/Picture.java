package com.agile.pics;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Picture {
    String id;
    String jsonText;

    public Picture(String id, String text) {
        this.id = id;
        this.jsonText = text;
    }

    public String getId() {
        return this.id;
    }

    public String getAttributes() {
        return jsonText;
    }
}
