package com.example.meisterbot.listeners;

import com.example.meisterbot.models.PostLoginApiResponse;

public interface PostLoginListener {
    void didFetch(PostLoginApiResponse pojo, String message);
    void didError(String message);
}
