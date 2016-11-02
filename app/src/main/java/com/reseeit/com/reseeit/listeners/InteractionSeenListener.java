package com.reseeit.com.reseeit.listeners;

import com.reseeit.models.Interaction;

public interface InteractionSeenListener {
    void onInteractionSeen(Interaction interaction,String couponId);
}
