package com.agile.pics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureService {

    LocalCacheImpl pictureRepository;

    public List<Picture> findByAttribute(String attr) {
        return null;
    }

    public void updateDatabase() {

    }
}
