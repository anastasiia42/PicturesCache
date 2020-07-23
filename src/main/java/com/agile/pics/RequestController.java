package com.agile.pics;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/search")
public class RequestController {

    LocalCacheImpl cache = new LocalCacheImpl();

    RequestController() {
        cache.update();
    }

    @GetMapping(value="/{attributeValue}", produces = "application/json")
    public @ResponseBody Set<String> getPicturesWithAttribute(@PathVariable("attributeValue") String attributeValue) {
        return cache.findByAttribute(attributeValue);
    }


}
