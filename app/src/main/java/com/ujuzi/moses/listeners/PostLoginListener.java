package com.ujuzi.moses.listeners;

import com.ujuzi.moses.models.PostLoginApiResponse;

public interface PostLoginListener {
    void didFetch(PostLoginApiResponse pojo, String message);
    void didError(String message);
}
