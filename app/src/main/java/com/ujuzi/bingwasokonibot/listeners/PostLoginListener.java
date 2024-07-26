package com.ujuzi.bingwasokonibot.listeners;

import com.ujuzi.bingwasokonibot.models.PostLoginApiResponse;

public interface PostLoginListener {
    void didFetch(PostLoginApiResponse pojo, String message);
    void didError(String message);
}
