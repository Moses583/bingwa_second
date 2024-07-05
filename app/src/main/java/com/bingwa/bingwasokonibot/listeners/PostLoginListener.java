package com.bingwa.bingwasokonibot.listeners;

import com.bingwa.bingwasokonibot.models.PostLoginApiResponse;

public interface PostLoginListener {
    void didFetch(PostLoginApiResponse pojo, String message);
    void didError(String message);
}
