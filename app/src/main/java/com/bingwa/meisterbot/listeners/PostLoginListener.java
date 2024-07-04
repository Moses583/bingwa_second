package com.bingwa.meisterbot.listeners;

import com.bingwa.meisterbot.models.PostLoginApiResponse;

public interface PostLoginListener {
    void didFetch(PostLoginApiResponse pojo, String message);
    void didError(String message);
}
