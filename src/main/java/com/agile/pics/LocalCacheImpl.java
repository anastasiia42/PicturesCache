package com.agile.pics;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
public class LocalCacheImpl {
    ConcurrentHashMap<String, HashSet<String>> attributeToPictureIdMap;
    HashMap<String, Picture> picturesMap;

    public LocalCacheImpl() {
        attributeToPictureIdMap = new ConcurrentHashMap<>();
        picturesMap = new HashMap<>();
    }


    @Scheduled(fixedDelay = 300000)
    public void update() {
        Connection connection = new Connection();
        try {
            //create new cache to enable the old one working while updating
            LocalCacheImpl newCache = new LocalCacheImpl();
            for (String id : connection.getPictureIds()) {
                Picture picture = connection.getPictureInfo(id);
                JSONObject jsonPictureInfo = new JSONObject(picture.getAttributes());
                Iterator<String> attributes = jsonPictureInfo.keys();
                while (attributes.hasNext()) {
                    String attribute = attributes.next();
                    if (!attribute.equals("id") && !attribute.equals("cropped_picture")
                            && !attribute.equals("full_picture")) {
                        newCache.addToCache(jsonPictureInfo.get(attribute).toString(), id);
                    }
                }
                newCache.picturesMap.put(id, picture);

                //update current cache with new values
                this.attributeToPictureIdMap = newCache.attributeToPictureIdMap;
                this.picturesMap = newCache.picturesMap;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void addToCache(String key, String pictureId) {
        //split attributes into smaller tags and store picture IDs matching the small tags separately
        String[] keySplitBySpace = key.split(" ");
        for (String entry : keySplitBySpace) {
            if (this.attributeToPictureIdMap.containsKey(entry)) {
                this.attributeToPictureIdMap.get(entry).add(pictureId);
            } else {
                HashSet<String> value = new HashSet<>();
                value.add(pictureId);
                this.attributeToPictureIdMap.put(entry, value);
            }
        }
    }



    public Set<String> findByAttribute(String attr) {
        String[] attributes = attr.split(",");
        HashSet<String> result = new HashSet<>();
        for (String attribute : attributes) {
            for (String id : this.attributeToPictureIdMap.get(attribute)) {
                result.add(this.picturesMap.get(id).getAttributes());
            }
        }
        return result;
    }

    public static void main(String[] args) {
        LocalCacheImpl cache = new LocalCacheImpl();
        cache.update();
    }
}
